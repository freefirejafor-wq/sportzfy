import { useState } from "react";

interface Props {
  open: boolean;
  onClose: () => void;
}

type ActivePanel = null | "network" | "playlists" | "highlights" | "cricket" | "football" | "quality";

const menuItems = [
  {
    key: "network",
    label: "Network Stream",
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <rect x="2" y="3" width="20" height="14" rx="2" />
        <path d="M8 21h8M12 17v4" />
        <circle cx="12" cy="10" r="3" />
      </svg>
    ),
  },
  {
    key: "playlists",
    label: "Playlists",
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <line x1="8" y1="6" x2="21" y2="6" /><line x1="8" y1="12" x2="21" y2="12" />
        <line x1="8" y1="18" x2="21" y2="18" />
        <line x1="3" y1="6" x2="3.01" y2="6" strokeWidth="3" />
        <line x1="3" y1="12" x2="3.01" y2="12" strokeWidth="3" />
        <line x1="3" y1="18" x2="3.01" y2="18" strokeWidth="3" />
      </svg>
    ),
  },
  {
    key: "highlights",
    label: "Highlights",
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <polygon points="5 3 19 12 5 21 5 3" />
      </svg>
    ),
  },
  {
    key: null,
    label: "Floating Player",
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <rect x="2" y="3" width="20" height="14" rx="2" />
        <rect x="12" y="11" width="8" height="5" rx="1" fill="currentColor" opacity="0.4" />
      </svg>
    ),
  },
  {
    key: "quality",
    label: "Force Low Quality",
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <circle cx="12" cy="12" r="3" />
        <path d="M19.07 4.93a10 10 0 0 1 0 14.14M15.54 8.46a5 5 0 0 1 0 7.07M4.93 19.07a10 10 0 0 1 0-14.14M8.46 15.54a5 5 0 0 1 0-7.07" />
      </svg>
    ),
  },
  {
    key: "cricket",
    label: "Cricket Score",
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <path d="M12 2L15 9H22L16.5 13.5L18.5 21L12 16.5L5.5 21L7.5 13.5L2 9H9L12 2Z" />
      </svg>
    ),
  },
  {
    key: "football",
    label: "Football Score",
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <circle cx="12" cy="12" r="10" />
        <path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z" />
        <path d="M2 12h20" />
      </svg>
    ),
  },
  {
    key: null,
    label: "Telegram",
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <line x1="22" y1="2" x2="11" y2="13" />
        <polygon points="22 2 15 22 11 13 2 9 22 2" />
      </svg>
    ),
    href: "https://t.me/sportzfy",
  },
  {
    key: null,
    label: "Website",
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <circle cx="12" cy="12" r="10" />
        <line x1="2" y1="12" x2="22" y2="12" />
        <path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z" />
      </svg>
    ),
    href: "https://sportzfy.com",
  },
  {
    key: null,
    label: "Exit",
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
        <polyline points="16 17 21 12 16 7" />
        <line x1="21" y1="12" x2="9" y2="12" />
      </svg>
    ),
  },
];

const cricketMatches = [
  { home: "India", away: "Pakistan", score: "245/6 (40)", status: "LIVE" },
  { home: "England", away: "Australia", score: "Eng: 320, Aus: 198/4", status: "LIVE" },
  { home: "Sri Lanka", away: "Bangladesh", score: "SL: 210, BD: 45/2", status: "LIVE" },
  { home: "SA", away: "NZ", score: "Today 14:00", status: "UPCOMING" },
];

const footballMatches = [
  { home: "Colombia", away: "Ghana", score: "2 - 1", status: "LIVE", time: "67'" },
  { home: "Arsenal", away: "Chelsea", score: "1 - 0", status: "LIVE", time: "34'" },
  { home: "Real Madrid", away: "Barcelona", score: "- vs -", status: "UPCOMING", time: "20:00" },
  { home: "PSG", away: "Bayern", score: "0 - 0", status: "LIVE", time: "89'" },
];

const playlists = [
  { name: "My Favourites", count: 12, icon: "⭐" },
  { name: "Football Streams", count: 8, icon: "⚽" },
  { name: "Cricket Live", count: 5, icon: "🏏" },
  { name: "MLB Baseball", count: 14, icon: "⚾" },
  { name: "Tennis Grand Slam", count: 4, icon: "🎾" },
];

const highlights = [
  { title: "Colombia vs Ghana — Goal Highlights", duration: "4:22", league: "World Championship" },
  { title: "Yankees vs Twins — Best Plays", duration: "6:10", league: "MLB" },
  { title: "India vs Pakistan — T20 Highlights", duration: "8:45", league: "ICC T20" },
  { title: "Arsenal vs Chelsea — Match Review", duration: "5:30", league: "Premier League" },
  { title: "Real Madrid Top Goals 2026", duration: "12:00", league: "La Liga" },
];

export default function SideMenu({ open, onClose }: Props) {
  const [activePanel, setActivePanel] = useState<ActivePanel>(null);
  const [lowQuality, setLowQuality] = useState(false);

  const handleItemClick = (key: string | null, href?: string) => {
    if (href) { window.open(href, "_blank"); return; }
    if (key === null) return;
    if (key === "quality") {
      setLowQuality((v) => !v);
      return;
    }
    setActivePanel(key as ActivePanel);
  };

  const closePanel = () => setActivePanel(null);

  return (
    <>
      {open && <div className="side-menu-overlay" onClick={() => { closePanel(); onClose(); }} />}

      <div className={`side-menu ${open ? "open" : ""}`} style={{ display: "flex", flexDirection: "column", overflowY: "auto" }}>
        {/* Logo header */}
        <div style={{ padding: "20px 20px 16px", borderBottom: "1px solid #1e2535" }}>
          <div style={{
            width: 52, height: 52, borderRadius: 14,
            background: "hsl(215 50% 22%)",
            border: "2px solid #00d4e8",
            display: "flex", alignItems: "center", justifyContent: "center",
            marginBottom: 10,
          }}>
            <svg width="28" height="28" viewBox="0 0 72 72" fill="none">
              <path d="M52 14C52 14 42 16 36 24C30 32 32 42 24 48C16 54 14 58 14 58"
                stroke="url(#sgm)" strokeWidth="18" strokeLinecap="round" />
              <defs>
                <linearGradient id="sgm" x1="14" y1="58" x2="52" y2="14" gradientUnits="userSpaceOnUse">
                  <stop stopColor="#00d4e8" />
                  <stop offset="0.5" stopColor="#00a8c4" />
                  <stop offset="1" stopColor="#e53e3e" />
                </linearGradient>
              </defs>
            </svg>
          </div>
          <div style={{ fontSize: 16, fontWeight: 800, color: "#fff" }}>Sportzfy</div>
          <div style={{ fontSize: 11, color: "#00d4e8", marginTop: 2, fontWeight: 700 }}>Version 6.0</div>
        </div>

        {/* Menu items */}
        <div style={{ flex: 1, overflowY: "auto" }}>
          {menuItems.map((item, i) => (
            <div
              key={i}
              className="menu-item"
              onClick={() => handleItemClick(item.key, (item as any).href)}
              style={{ justifyContent: "space-between" }}
            >
              <div style={{ display: "flex", alignItems: "center", gap: 14 }}>
                <span style={{ color: "#00d4e8", flexShrink: 0 }}>{item.icon}</span>
                <span>{item.label}</span>
              </div>
              {item.key === "quality" && (
                <div style={{
                  width: 36, height: 20, borderRadius: 999,
                  background: lowQuality ? "#00d4e8" : "#333",
                  position: "relative", transition: "background 0.2s", flexShrink: 0,
                }}>
                  <div style={{
                    position: "absolute", top: 2,
                    left: lowQuality ? 18 : 2,
                    width: 16, height: 16, borderRadius: "50%",
                    background: "#fff", transition: "left 0.2s",
                  }} />
                </div>
              )}
              {item.key && item.key !== "quality" && (
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#555" strokeWidth="2">
                  <polyline points="9 18 15 12 9 6" />
                </svg>
              )}
            </div>
          ))}
        </div>
      </div>

      {/* Sub-panels */}
      {activePanel && (
        <div style={{
          position: "fixed", inset: 0, zIndex: 202,
          background: "rgba(0,0,0,0.6)",
        }} onClick={closePanel}>
          <div
            onClick={(e) => e.stopPropagation()}
            style={{
              position: "absolute", top: 0, right: 0,
              width: "100%", maxWidth: 300, height: "100%",
              background: "hsl(220 22% 11%)",
              borderLeft: "1.5px solid hsl(185 80% 30%)",
              display: "flex", flexDirection: "column",
              animation: "slideLeft 0.25s ease",
            }}
          >
            {/* Panel header */}
            <div style={{
              padding: "16px", borderBottom: "1px solid #1e2535",
              display: "flex", alignItems: "center", gap: 12,
            }}>
              <button onClick={closePanel} style={{ background: "none", border: "none", cursor: "pointer", color: "#00d4e8" }}>
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                  <path d="M19 12H5M12 5l-7 7 7 7" />
                </svg>
              </button>
              <span style={{ fontSize: 16, fontWeight: 800, color: "#fff" }}>
                {activePanel === "network" && "Network Stream"}
                {activePanel === "playlists" && "Playlists"}
                {activePanel === "highlights" && "Highlights"}
                {activePanel === "cricket" && "🏏 Cricket Score"}
                {activePanel === "football" && "⚽ Football Score"}
              </span>
            </div>

            {/* Panel content */}
            <div style={{ flex: 1, overflowY: "auto", padding: 12 }}>
              {activePanel === "network" && (
                <div>
                  <div style={{ color: "#aaa", fontSize: 13, marginBottom: 14, padding: "0 4px" }}>
                    Add a custom M3U or HLS stream URL
                  </div>
                  <input
                    placeholder="https://example.com/stream.m3u8"
                    style={{
                      width: "100%", padding: "12px", borderRadius: 10,
                      background: "hsl(220 18% 16%)", border: "1.5px solid #00d4e8",
                      color: "#fff", fontSize: 13, outline: "none",
                      boxSizing: "border-box",
                    }}
                  />
                  <button style={{
                    width: "100%", marginTop: 12, padding: "13px",
                    background: "#00d4e8", color: "#000", fontWeight: 800,
                    border: "none", borderRadius: 10, cursor: "pointer", fontSize: 14,
                  }}>
                    ▶ Load Stream
                  </button>
                  <div style={{ color: "#555", fontSize: 12, marginTop: 16, padding: "0 4px" }}>
                    Supports HLS (.m3u8), DASH (.mpd), MP4
                  </div>
                </div>
              )}

              {activePanel === "playlists" && playlists.map((p, i) => (
                <div key={i} style={{
                  display: "flex", alignItems: "center", gap: 12,
                  padding: "12px 10px", borderRadius: 10,
                  background: "hsl(220 18% 15%)", marginBottom: 8,
                  border: "1px solid hsl(220 18% 22%)", cursor: "pointer",
                }}>
                  <span style={{ fontSize: 22 }}>{p.icon}</span>
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: 14, fontWeight: 700, color: "#fff" }}>{p.name}</div>
                    <div style={{ fontSize: 11, color: "#666" }}>{p.count} channels</div>
                  </div>
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#00d4e8" strokeWidth="2">
                    <polyline points="9 18 15 12 9 6" />
                  </svg>
                </div>
              ))}

              {activePanel === "highlights" && highlights.map((h, i) => (
                <div key={i} style={{
                  padding: "12px 10px", borderRadius: 10,
                  background: "hsl(220 18% 15%)", marginBottom: 8,
                  border: "1px solid hsl(220 18% 22%)", cursor: "pointer",
                }}>
                  <div style={{ fontSize: 13, fontWeight: 700, color: "#fff", marginBottom: 4 }}>{h.title}</div>
                  <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
                    <span style={{ fontSize: 11, color: "#00d4e8" }}>{h.league}</span>
                    <span style={{ fontSize: 11, color: "#666" }}>▶ {h.duration}</span>
                  </div>
                </div>
              ))}

              {activePanel === "cricket" && cricketMatches.map((m, i) => (
                <div key={i} style={{
                  padding: "12px 10px", borderRadius: 10,
                  background: "hsl(220 18% 15%)", marginBottom: 8,
                  border: "1px solid hsl(220 18% 22%)",
                }}>
                  <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 6 }}>
                    <span style={{ fontSize: 13, fontWeight: 700, color: "#fff" }}>{m.home} vs {m.away}</span>
                    <span style={{
                      fontSize: 10, fontWeight: 800, padding: "2px 7px", borderRadius: 999,
                      background: m.status === "LIVE" ? "#e53e3e" : "#333",
                      color: "#fff",
                    }}>{m.status}</span>
                  </div>
                  <div style={{ fontSize: 12, color: "#00d4e8" }}>{m.score}</div>
                </div>
              ))}

              {activePanel === "football" && footballMatches.map((m, i) => (
                <div key={i} style={{
                  padding: "12px 10px", borderRadius: 10,
                  background: "hsl(220 18% 15%)", marginBottom: 8,
                  border: "1px solid hsl(220 18% 22%)",
                }}>
                  <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 6 }}>
                    <span style={{ fontSize: 13, fontWeight: 700, color: "#fff" }}>{m.home} vs {m.away}</span>
                    <span style={{
                      fontSize: 10, fontWeight: 800, padding: "2px 7px", borderRadius: 999,
                      background: m.status === "LIVE" ? "#e53e3e" : "#333",
                      color: "#fff",
                    }}>{m.status}</span>
                  </div>
                  <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
                    <span style={{ fontSize: 18, fontWeight: 900, color: "#00d4e8", letterSpacing: 2 }}>{m.score}</span>
                    <span style={{ fontSize: 12, color: "#666" }}>{m.time}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      <style>{`
        @keyframes slideLeft {
          from { transform: translateX(100%); }
          to { transform: translateX(0); }
        }
      `}</style>
    </>
  );
}
