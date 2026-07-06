import { Router } from "express";
import https from "https";
import http from "http";
import { URL } from "url";

const router = Router();

const CORS_HEADERS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "GET, HEAD, OPTIONS",
  "Access-Control-Allow-Headers": "*",
  "Access-Control-Expose-Headers": "*",
  "Cache-Control": "no-cache, no-store, must-revalidate",
};

const UPSTREAM_HEADERS = {
  "User-Agent":
    "Mozilla/5.0 (Linux; Android 11; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36",
  Accept: "*/*",
  "Accept-Language": "en-US,en;q=0.9",
  Connection: "keep-alive",
};

function resolveUrl(base: string, relative: string): string {
  try {
    if (relative.startsWith("http://") || relative.startsWith("https://"))
      return relative;
    return new URL(relative, base).toString();
  } catch {
    return relative;
  }
}

/** Block requests to private/loopback IP ranges (SSRF protection) */
function isPrivateHost(hostname: string): boolean {
  // Loopback
  if (hostname === "localhost" || hostname === "::1") return true;
  // IPv4 private / link-local / loopback
  const privatePatterns = [
    /^127\./,
    /^10\./,
    /^172\.(1[6-9]|2\d|3[01])\./,
    /^192\.168\./,
    /^169\.254\./,   // link-local (AWS metadata etc.)
    /^100\.6[4-9]\.|^100\.[7-9]\d\.|^100\.1[0-1]\d\.|^100\.12[0-7]\./, // CGNAT
    /^0\./,          // 0.0.0.0/8
    /^::/, // IPv6 loopback / link-local
  ];
  return privatePatterns.some((re) => re.test(hostname));
}

function makeUpstreamRequest(
  targetUrl: string,
  redirects: number,
  onResponse: (res: http.IncomingMessage, finalUrl: string) => void,
  onError: (err: Error) => void
): void {
  if (redirects > 8) { onError(new Error("Too many redirects")); return; }

  let parsed: URL;
  try { parsed = new URL(targetUrl); } catch (e) { onError(new Error("Invalid URL")); return; }

  /* SSRF guard on every hop — blocks redirect-to-private attacks */
  if (isPrivateHost(parsed.hostname)) {
    onError(new Error("SSRF: redirect target is a private/internal host"));
    return;
  }

  const proto = targetUrl.startsWith("https://") ? https : http;
  const options: http.RequestOptions = {
    hostname: parsed.hostname,
    port: parsed.port
      ? Number(parsed.port)
      : targetUrl.startsWith("https://") ? 443 : 80,
    path: parsed.pathname + parsed.search,
    headers: UPSTREAM_HEADERS,
    timeout: 15000,
  };

  const req = proto.get(options, (res) => {
    const loc = res.headers.location;
    if (res.statusCode && [301, 302, 303, 307, 308].includes(res.statusCode) && loc) {
      res.resume();
      const nextUrl = resolveUrl(targetUrl, loc);
      makeUpstreamRequest(nextUrl, redirects + 1, onResponse, onError);
      return;
    }
    onResponse(res, targetUrl);
  });

  req.on("timeout", () => { req.destroy(); onError(new Error("Upstream timeout")); });
  req.on("error", onError);
}

/* OPTIONS preflight */
router.options("/proxy", (_req, res) => {
  res.set(CORS_HEADERS).sendStatus(204);
});

/* GET /api/proxy?url=<encoded-url> */
router.get("/proxy", (req, res) => {
  const url = req.query.url as string;

  if (!url || (!url.startsWith("http://") && !url.startsWith("https://"))) {
    return res.status(400).json({ error: "Missing or invalid ?url= parameter" });
  }

  /* SSRF guard: reject private / loopback targets */
  try {
    const { hostname } = new URL(url);
    if (isPrivateHost(hostname)) {
      return res.status(403).json({ error: "Forbidden: private/internal hosts are not allowed" });
    }
  } catch {
    return res.status(400).json({ error: "Malformed URL" });
  }

  makeUpstreamRequest(
    url,
    0,
    (upRes, finalUrl) => {
      const ct = (upRes.headers["content-type"] || "").toLowerCase();
      const isM3u8 =
        finalUrl.toLowerCase().includes(".m3u8") ||
        ct.includes("mpegurl") ||
        ct.includes("m3u");

      res.set(CORS_HEADERS);

      if (isM3u8) {
        /* Buffer entire playlist, then rewrite all URIs through proxy */
        const chunks: Buffer[] = [];
        upRes.on("data", (c: Buffer) => chunks.push(c));
        upRes.on("end", () => {
          const text = Buffer.concat(chunks).toString("utf8");

          /* Root-relative proxy prefix — HLS.js prepends scheme+host automatically */
          const proxyPrefix = "/api/proxy?url=";

          const rewritten = text
            .split("\n")
            .map((rawLine) => {
              const line = rawLine.trim();
              if (!line) return rawLine;

              /* Rewrite URI="..." attributes inside tag lines (#EXT-X-MEDIA, #EXT-X-KEY, #EXT-X-MAP etc.) */
              if (line.startsWith("#")) {
                return rawLine.replace(/URI="([^"]+)"/g, (_match, uri: string) => {
                  const absUrl = resolveUrl(finalUrl, uri);
                  return `URI="${proxyPrefix}${encodeURIComponent(absUrl)}"`;
                });
              }

              /* Plain URI line (segment, variant playlist) */
              const absUrl = resolveUrl(finalUrl, line);
              return proxyPrefix + encodeURIComponent(absUrl);
            })
            .join("\n");

          res
            .status(200)
            .set("Content-Type", "application/vnd.apple.mpegurl")
            .send(rewritten);
        });
        upRes.on("error", () => {
          if (!res.headersSent) res.status(502).json({ error: "Upstream stream error" });
        });
      } else {
        /* Binary passthrough — TS segments, keys, etc. */
        res.status(upRes.statusCode || 200).set(
          "Content-Type",
          ct || "application/octet-stream"
        );
        upRes.pipe(res);
        upRes.on("error", () => res.end());
      }
    },
    (err) => {
      if (!res.headersSent)
        res.status(502).json({ error: "Proxy error", detail: err.message });
    }
  );
});

export default router;
