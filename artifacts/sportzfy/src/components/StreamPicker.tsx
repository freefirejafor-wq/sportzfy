import type { Match } from "../data/matches";

/**
 * Wrap any HLS/stream URL through our server-side CORS proxy.
 * The proxy rewrites M3U8 playlists so that segment requests also
 * go through the proxy — fixing CORS blocks on all stream sources.
 */
function proxied(url: string): string {
  return `/api/proxy?url=${encodeURIComponent(url)}`;
}

interface Stream {
  name: string;
  quality: "FHD" | "HD" | "SD";
  url: string;
}

// All verified-active streams (checked July 2026)
const ALL_STREAMS: Stream[] = [
  // ── FIFA / World Cup dedicated ──────────────────────────────────
  { name: "FIFA WC ENG (Master)",   quality: "FHD", url: "https://hls.livekhelatv.website/mks/lktv/fifa-worldcup-eng/master.m3u8" },
  { name: "FIFA WC ENG (1080p)",    quality: "FHD", url: "https://hls.livekhelatv.website/mks/lktv/s/fifa-worldcup-eng/playlist/1080p_FHD/livekhela.m3u8" },
  { name: "Caze TV (FIFA 4K)",      quality: "FHD", url: "https://dfr80qz435crc.cloudfront.net/MNOP/Amagi/Caze/Caze_TV_BR/1080p-vtt/index.m3u8" },
  { name: "FIFA+ English",          quality: "FHD", url: "https://a62dad94.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/UmFrdXRlblRWLWV1X0ZJRkFQbHVzRW5nbGlzaF9ITFM/playlist.m3u8" },
  { name: "FIFA+ USA",              quality: "FHD", url: "https://d2w9q46ikgrcwx.cloudfront.net/v1/master/3722c60a815c199d9c0ef36c5b73da68a62b09d1/cc-of5cbk3sav3w5/v1/sysdata_s_p_a_fifa_7/samsungheadend_us/latest/main/hls/playlist.m3u8" },
  { name: "FIFA+ Español",          quality: "FHD", url: "https://d63fabad.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/UmFrdXRlblRWLWVzX0ZJRkFQbHVzU3BhbmlzaF9ITFM/playlist.m3u8" },
  { name: "FIFA+ Brasil",           quality: "FHD", url: "https://e3be9ac5.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/TEctYnJfRklGQVBsdXNQb3J0dWd1ZXNlX0hMUw/playlist.m3u8" },
  { name: "FIFA+ Deutschland",      quality: "FHD", url: "https://4397879b.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/UmFrdXRlblRWLWRlX0ZJRkFQbHVzR2VybWFuX0hMUw/playlist.m3u8" },
  { name: "FIFA+ Argentina",        quality: "FHD", url: "https://6c849fb3.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/TEctbXhfRklGQVBsdXNTcGFuaXNoLTFfSExT/playlist.m3u8" },
  { name: "FIFA+ Italia",           quality: "FHD", url: "https://5d95f7d7.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/UmFrdXRlblRWLWl0X0ZJRkFQbHVzSXRhbGlhbl9ITFM/playlist.m3u8" },
  { name: "FIFA+ France",           quality: "FHD", url: "https://37b4c228.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/UmFrdXRlblRWLWZyX0ZJRkFQbHVzRnJlbmNoX0hMUw/playlist.m3u8" },
  { name: "Telemundo (FIFA)",       quality: "HD",  url: "https://nbculocallive.akamaized.net/hls/live/2037499/puertorico/stream1/master.m3u8" },
  { name: "FIFA Bangla",            quality: "HD",  url: "https://d1g8wgjurz8via.cloudfront.net/bpk-tv/ColorsHD/default/ColorsHD.m3u8" },
  { name: "FIFA Stream A",          quality: "HD",  url: "https://1nyaler.streamhostingcdn.top/stream/23/index.m3u8" },
  { name: "World Cup S2",           quality: "HD",  url: "https://andro.226503.xyz/checklist/androstreamlivebs1.m3u8" },
  { name: "Win Sports WC",          quality: "HD",  url: "https://1nyaler.streamhostingcdn.top/stream/32/index.m3u8" },
  { name: "2TV Sports WC",          quality: "HD",  url: "https://tv.cdn.xsg.ge/gpb-2tv/index.m3u8" },

  // ── beIN Sports ─────────────────────────────────────────────────
  { name: "beIN Sports 1 UHD",      quality: "FHD", url: "http://proxpanel.cc/h1wqD6CY/byxHYgX/707929" },
  { name: "beIN Sports 1 (Amagi)",  quality: "FHD", url: "https://cdn-uw2-prod.tsv2.amagi.tv/linear/amg02873-kravemedia-mtrspt1-distrotv/playlist.m3u8" },
  { name: "beIN Sports 2",          quality: "HD",  url: "https://andro.226503.xyz/checklist/androstreamlivebs2.m3u8" },
  { name: "beIN Sports 3",          quality: "HD",  url: "https://andro.226503.xyz/checklist/androstreamlivebs3.m3u8" },
  { name: "beIN Sports 4",          quality: "HD",  url: "https://andro.226503.xyz/checklist/androstreamlivebs4.m3u8" },
  { name: "beIN Sports Xtra",       quality: "HD",  url: "https://bein-esp-xumo.amagi.tv/playlistR720P.m3u8" },

  // ── Other sports channels ────────────────────────────────────────
  { name: "ESPN 2",                 quality: "HD",  url: "https://tv.topmediatv.net:25463/live/TopMediaWeb/bOteTR8ED1/108.m3u8" },
  { name: "TyC Sports Argentina",   quality: "HD",  url: "https://amg26268-amg26268c14-freelivesports-emea-10267.playouts.now.amagi.tv/ts-us-e2-n2/playlist/amg26268-sportsstudio-tycsports-freelivesportsemea/playlist.m3u8" },
  { name: "Euro TV",                quality: "HD",  url: "https://stream.ottplus.bd/live/euro_sports_hd_abr/live/euro_sports_hd/chunks.m3u8" },
  { name: "Cricket Gold",           quality: "HD",  url: "https://streams2.sofast.tv/ptnr-yupptv/title-cricketgold/v1/manifest/611d79b11b77e2f571934fd80ca1413453772ac7/b2048bb8-1686-4432-aa50-647245383e0c/bfc6a36e-c250-4afe-b6c9-2bc57855bb7d/4.m3u8" },
];

function getStreams(_match: Match): Stream[] {
  return ALL_STREAMS;
}

interface Props {
  match: Match;
  onSelect: (url: string, streamName: string) => void;
  onClose: () => void;
}

const qualityColors: Record<string, string> = {
  FHD: "#00d4e8",
  HD: "#4ade80",
  SD: "#f59e0b",
};

export default function StreamPicker({ match, onSelect, onClose }: Props) {
  const streams = getStreams(match);

  return (
    <div
      style={{
        position: "fixed", inset: 0,
        background: "rgba(0,0,0,0.75)",
        zIndex: 400,
        display: "flex", alignItems: "flex-end",
        justifyContent: "center",
      }}
      onClick={onClose}
    >
      <div
        onClick={(e) => e.stopPropagation()}
        style={{
          width: "100%", maxWidth: 480,
          background: "hsl(220 22% 13%)",
          borderRadius: "24px 24px 0 0",
          border: "1.5px solid hsl(185 80% 35%)",
          borderBottom: "none",
          padding: "0 0 16px",
          animation: "slideUp 0.3s ease",
          maxHeight: "88vh",
          display: "flex", flexDirection: "column",
        }}
      >
        {/* Header */}
        <div style={{ padding: "24px 20px 16px", textAlign: "center", borderBottom: "1px solid hsl(220 18% 20%)" }}>
          <div style={{
            width: 52, height: 52, borderRadius: "50%",
            border: "2px solid #00d4e8",
            display: "flex", alignItems: "center", justifyContent: "center",
            margin: "0 auto 12px",
            background: "hsl(220 22% 18%)",
          }}>
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#00d4e8" strokeWidth="2">
              <circle cx="12" cy="12" r="2" fill="#00d4e8" stroke="none" />
              <path d="M16.24 7.76a6 6 0 0 1 0 8.49" />
              <path d="M7.76 7.76a6 6 0 0 0 0 8.49" />
              <path d="M20.07 4.93a10 10 0 0 1 0 14.14" />
              <path d="M3.93 4.93a10 10 0 0 0 0 14.14" />
            </svg>
          </div>
          <div style={{ fontSize: 18, fontWeight: 800, color: "#fff", marginBottom: 4 }}>
            Multiple Streams Available
          </div>
          <div style={{ fontSize: 13, color: "#888" }}>Choose your preferred stream</div>
        </div>

        {/* Stream list */}
        <div style={{ overflowY: "auto", flex: 1 }}>
          {streams.map((stream, i) => (
            <div
              key={i}
              onClick={() => onSelect(proxied(stream.url), stream.name)}
              style={{
                display: "flex", alignItems: "center", justifyContent: "space-between",
                padding: "14px 20px",
                borderBottom: "1px solid hsl(220 18% 18%)",
                cursor: "pointer",
                transition: "background 0.15s",
              }}
              onMouseEnter={(e) => (e.currentTarget.style.background = "hsl(220 18% 18%)")}
              onMouseLeave={(e) => (e.currentTarget.style.background = "transparent")}
            >
              <div style={{ display: "flex", alignItems: "center", gap: 14 }}>
                {/* Lines icon */}
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#00d4e8" strokeWidth="2">
                  <line x1="8" y1="6" x2="21" y2="6" />
                  <line x1="8" y1="12" x2="21" y2="12" />
                  <line x1="8" y1="18" x2="21" y2="18" />
                  <line x1="3" y1="6" x2="3.01" y2="6" strokeWidth="3" />
                  <line x1="3" y1="12" x2="3.01" y2="12" strokeWidth="3" />
                  <line x1="3" y1="18" x2="3.01" y2="18" strokeWidth="3" />
                </svg>
                <span style={{ fontSize: 14, fontWeight: 600, color: "#00d4e8" }}>{stream.name}</span>
              </div>
              <div style={{
                background: "hsl(220 22% 20%)",
                border: `1px solid ${qualityColors[stream.quality]}`,
                color: qualityColors[stream.quality],
                fontSize: 11, fontWeight: 800,
                padding: "3px 10px", borderRadius: 999,
              }}>
                {stream.quality}
              </div>
            </div>
          ))}
        </div>

        {/* Cancel */}
        <div style={{ padding: "12px 20px 0" }}>
          <button
            onClick={onClose}
            style={{
              width: "100%", padding: "14px",
              background: "hsl(220 18% 18%)",
              border: "1.5px solid hsl(220 18% 25%)",
              borderRadius: 14, cursor: "pointer",
              color: "#aaa", fontSize: 15, fontWeight: 700,
              display: "flex", alignItems: "center", justifyContent: "center", gap: 8,
            }}
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
              <line x1="18" y1="6" x2="6" y2="18" /><line x1="6" y1="6" x2="18" y2="18" />
            </svg>
            Cancel
          </button>
        </div>
      </div>

      <style>{`
        @keyframes slideUp {
          from { transform: translateY(100%); }
          to { transform: translateY(0); }
        }
      `}</style>
    </div>
  );
}
