import { Router } from "express";
import https from "https";
import { logger } from "../lib/logger";

const router = Router();

function fetchJson(url: string): Promise<unknown> {
  return new Promise((resolve, reject) => {
    https
      .get(url, { headers: { "User-Agent": "Sportzfy/6.0" } }, (res) => {
        let body = "";
        res.on("data", (c: string) => (body += c));
        res.on("end", () => {
          try {
            resolve(JSON.parse(body));
          } catch {
            reject(new Error("Invalid JSON"));
          }
        });
      })
      .on("error", reject);
  });
}

function toBST(isoDate: string) {
  const bst = new Date(new Date(isoDate).getTime() + 6 * 3600000);
  const h = bst.getUTCHours(),
    m = bst.getUTCMinutes();
  const pad = (n: number) => String(n).padStart(2, "0");

  let session: string;
  if (h < 6) session = "রাত";
  else if (h < 12) session = "সকাল";
  else if (h < 16) session = "দুপুর";
  else if (h < 19) session = "বিকাল";
  else if (h < 21) session = "সন্ধ্যা";
  else session = "রাত";

  const nowBST = new Date(Date.now() + 6 * 3600000);
  const todayMid = Date.UTC(
    nowBST.getUTCFullYear(),
    nowBST.getUTCMonth(),
    nowBST.getUTCDate(),
  );
  const matchMid = Date.UTC(
    bst.getUTCFullYear(),
    bst.getUTCMonth(),
    bst.getUTCDate(),
  );
  const diff = Math.round((matchMid - todayMid) / 86400000);
  const dayLabel =
    diff === 0
      ? "আজ"
      : diff === 1
        ? "আগামীকাল"
        : ["রবি", "সোম", "মঙ্গল", "বুধ", "বৃহস্পতি", "শুক্র", "শনি"][
            bst.getUTCDay()
          ];

  return {
    bstTime: `${pad(h)}:${pad(m)}`,
    session,
    sessionFull: `${session} ${pad(h)}:${pad(m)}`,
    isDayTime: h >= 6 && h < 19,
    dayLabel,
  };
}

function countdown(isoDate: string): string | null {
  const ms = new Date(isoDate).getTime() - Date.now();
  if (ms <= 0) return null;
  const totalMin = Math.round(ms / 60000);
  const d = Math.floor(totalMin / 1440);
  const h = Math.floor((totalMin % 1440) / 60);
  const mn = totalMin % 60;
  if (d > 0) return `${d} দিন ${h} ঘণ্টা পরে`;
  if (h > 0) return `${h} ঘণ্টা ${mn} মিনিট পরে`;
  return `${mn} মিনিট পরে`;
}

const LIVE_STATUSES = new Set([
  "STATUS_IN_PROGRESS",
  "STATUS_HALFTIME",
  "STATUS_END_PERIOD",
  "STATUS_FIRST_HALF",
  "STATUS_SECOND_HALF",
  "STATUS_OVERTIME",
]);

// eslint-disable-next-line @typescript-eslint/no-explicit-any
function mapEvent(ev: any) {
  const comp = ev.competitions?.[0] || {};
  const home =
    comp.competitors?.find((c: any) => c.homeAway === "home") ||
    comp.competitors?.[0] ||
    {};
  const away =
    comp.competitors?.find((c: any) => c.homeAway === "away") ||
    comp.competitors?.[1] ||
    {};
  const sName = ev.status?.type?.name || "";
  const clock = ev.status?.displayClock || null;
  const isLive = LIVE_STATUSES.has(sName);
  const isDone = sName.includes("FINAL") || sName === "STATUS_FULL_TIME";
  const bst = toBST(ev.date || new Date().toISOString());

  return {
    id: ev.id,
    sport: "football",
    league: "Football | FIFA বিশ্বকাপ ২০২৬",
    homeName: home.team?.shortDisplayName || home.team?.displayName || "Home",
    awayName: away.team?.shortDisplayName || away.team?.displayName || "Away",
    homeLogo: home.team?.logo || "",
    awayLogo: away.team?.logo || "",
    homeScore: home.score ?? "",
    awayScore: away.score ?? "",
    status: isLive ? "live" : isDone ? "finished" : "upcoming",
    clock,
    bstTime: bst.bstTime,
    session: bst.session,
    sessionFull: bst.sessionFull,
    isDayTime: bst.isDayTime,
    dayLabel: bst.dayLabel,
    countdown: isLive || isDone ? null : countdown(ev.date),
    date: ev.date || new Date().toISOString(),
    hot: isLive && home.score !== away.score,
    venue: comp.venue?.fullName || null,
  };
}

async function fetchWorldCup() {
  const dates = Array.from({ length: 14 }, (_, i) => {
    const d = new Date(Date.now() + i * 86400000);
    return d.toISOString().slice(0, 10).replace(/-/g, "");
  });

  const results = await Promise.allSettled(
    dates.map((d) =>
      fetchJson(
        `https://site.api.espn.com/apis/site/v2/sports/soccer/fifa.world/scoreboard?dates=${d}`,
      ),
    ),
  );

  const seen = new Set<string>();
  const events: ReturnType<typeof mapEvent>[] = [];

  results.forEach((r) => {
    if (r.status !== "fulfilled") return;
    const data = r.value as { events?: unknown[] };
    (data.events || []).forEach((ev: unknown) => {
      const e = ev as { id?: string };
      if (!e.id || seen.has(e.id)) return;
      seen.add(e.id);
      events.push(mapEvent(ev));
    });
  });

  const ord = (s: string) => (s === "live" ? 0 : s === "upcoming" ? 1 : 2);
  events.sort((a, b) => {
    const od = ord(a.status) - ord(b.status);
    return od !== 0 ? od : new Date(a.date).getTime() - new Date(b.date).getTime();
  });

  return events;
}

router.get("/sports/scores", async (req, res) => {
  try {
    const events = await fetchWorldCup();
    res.json({ events, total: events.length, updatedAt: new Date().toISOString() });
  } catch (err: unknown) {
    const msg = err instanceof Error ? err.message : "Failed to fetch data";
    req.log.error({ err }, "Sports scores fetch failed");
    res.status(500).json({ error: msg });
  }
});

export default router;
