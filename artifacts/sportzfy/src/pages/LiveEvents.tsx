import { useState, useEffect, type ReactElement } from "react";
import { useSportsData, type SportEvent } from "../hooks/useSportsData";
import type { Match } from "../data/matches";

type StatusFilter = "all" | "live" | "upcoming" | "finished";

interface Props { onWatch: (m: Match) => void; searchQuery?: string; }

/* ── HH:MM:SS countdown clock (ticks down live) ── */
function CountdownClock({ targetDate }: { targetDate: string }) {
  const remaining = () => Math.max(0, Math.floor((new Date(targetDate).getTime() - Date.now()) / 1000));
  const [secs, setSecs] = useState(remaining);
  useEffect(() => {
    const id = setInterval(() => setSecs(remaining()), 1000);
    return () => clearInterval(id);
  }, [targetDate]);
  const pad = (n: number) => String(n).padStart(2, "0");
  const h = Math.floor(secs / 3600), m = Math.floor((secs % 3600) / 60), sc = secs % 60;
  return <>{`${pad(h)}:${pad(m)}:${pad(sc)}`}</>;
}

/* ── Live elapsed clock (counts up) ── */
function LiveClock({ seed }: { seed: string }) {
  const parse = (s: string) => {
    const parts = s.replace(/[^\d:]/g, "").split(":").map(Number).filter(n => !isNaN(n));
    if (parts.length === 3) return parts[0] * 3600 + parts[1] * 60 + parts[2];
    if (parts.length === 2) return parts[0] * 60 + parts[1];
    return 0;
  };
  const [secs, setSecs] = useState(() => parse(seed));
  useEffect(() => {
    const id = setInterval(() => setSecs(s => s + 1), 1000);
    return () => clearInterval(id);
  }, []);
  const pad = (n: number) => String(n).padStart(2, "0");
  const h = Math.floor(secs / 3600), m = Math.floor((secs % 3600) / 60), sc = secs % 60;
  return <>{`${pad(h)}:${pad(m)}:${pad(sc)}`}</>;
}

/* ── Team logo circle ── */
function TeamLogo({ logo, name, size = 46 }: { logo?: string; name: string; size?: number }) {
  const [err, setErr] = useState(false);
  if (logo && !err) {
    return (
      <img src={logo} alt={name}
        style={{ width: size, height: size, borderRadius: "50%", objectFit: "contain", background: "#fff", flexShrink: 0, padding: 2 }}
        onError={() => setErr(true)}
      />
    );
  }
  return (
    <div style={{ width: size, height: size, borderRadius: "50%", background: "#1e2a3a", display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0, border: "1px solid #2a3a50" }}>
      <span style={{ fontSize: size * 0.3, fontWeight: 800, color: "#00d4e8" }}>{name.slice(0, 2).toUpperCase()}</span>
    </div>
  );
}

/* ── Single match card ── */
function MatchCard({ ev, onWatch }: { ev: SportEvent & { [k: string]: any }; onWatch: (m: Match) => void }) {
  const isLive     = ev.status === "live";
  const isUpcoming = ev.status === "upcoming";

  /* BST match time from API fields */
  const dayLabel   = ev.dayLabel   || "";   // "আজ" / "আগামীকাল"
  const session    = ev.session    || "";   // "রাত" / "সকাল" / "বিকাল" etc.
  const bstTime    = ev.bstTime    || "";   // "02:00"
  const isDayTime  = ev.isDayTime  ?? false;

  const matchTimeLabel = dayLabel && bstTime
    ? `${dayLabel} ${session} ${bstTime} (BST)`
    : "";

  const handleClick = () => {
    onWatch({
      id: ev.id,
      league: ev.league,
      leagueIcon: "⚽",
      homeTeam: ev.homeName,
      homeFlag: ev.homeFlag || ev.homeLogo || "",
      awayTeam: ev.awayName,
      awayFlag: ev.awayFlag || ev.awayLogo || "",
      status: ev.status,
      time: ev.clock || ev.bstTime || "",
    } as Match);
  };

  return (
    <div onClick={handleClick} style={{
      background: "#0d1520",
      border: "1.5px solid #00d4e8",
      borderRadius: 14,
      marginBottom: 10,
      cursor: "pointer",
      position: "relative",
      overflow: "hidden",
    }}>
      {/* HOT badge */}
      {ev.hot && (
        <div style={{
          position: "absolute", top: 0, right: 0,
          background: "#00d4e8", color: "#000",
          fontSize: 11, fontWeight: 900, letterSpacing: 1,
          padding: "3px 12px 3px 10px",
          borderRadius: "0 14px 0 12px",
        }}>HOT</div>
      )}

      {/* League row */}
      <div style={{ display: "flex", alignItems: "center", gap: 7, padding: "10px 14px 0", paddingRight: ev.hot ? 54 : 14 }}>
        <span style={{ fontSize: 16 }}>⚽</span>
        <span style={{ fontSize: 12, color: "#bbb", fontWeight: 700, letterSpacing: 0.2, overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
          {ev.league}
        </span>
      </div>

      {/* Teams row */}
      <div style={{ display: "flex", alignItems: "center", padding: "12px 14px 6px" }}>
        {/* Home */}
        <div style={{ flex: 1, display: "flex", alignItems: "center", gap: 10, minWidth: 0 }}>
          <TeamLogo logo={ev.homeLogo} name={ev.homeName} size={46} />
          <span style={{ fontSize: 15, fontWeight: 700, color: "#eee", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
            {ev.homeName}
          </span>
        </div>

        {/* Center */}
        <div style={{ display: "flex", flexDirection: "column", alignItems: "center", padding: "0 14px", flexShrink: 0, gap: 4 }}>
          {isLive ? (
            <>
              <div style={{ display: "flex", alignItems: "center", gap: 5 }}>
                <div style={{ width: 9, height: 9, borderRadius: "50%", background: "#e53e3e", animation: "pulse 1.2s ease-in-out infinite" }} />
                <span style={{ fontSize: 11, color: "#e53e3e", fontWeight: 800 }}>(•)</span>
              </div>
              <span style={{ fontSize: 11, color: "#e53e3e", fontWeight: 700 }}>Live</span>
            </>
          ) : isUpcoming ? (
            <span style={{ fontSize: 15, color: "#555", fontWeight: 600 }}>vs</span>
          ) : ev.homeScore !== "" ? (
            <span style={{ fontSize: 15, fontWeight: 900, color: "#00d4e8", letterSpacing: 2 }}>
              {ev.homeScore} – {ev.awayScore}
            </span>
          ) : (
            <span style={{ fontSize: 13, color: "#555" }}>FT</span>
          )}
        </div>

        {/* Away */}
        <div style={{ flex: 1, display: "flex", alignItems: "center", justifyContent: "flex-end", gap: 10, minWidth: 0 }}>
          <span style={{ fontSize: 15, fontWeight: 700, color: "#eee", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap", textAlign: "right" }}>
            {ev.awayName}
          </span>
          <TeamLogo logo={ev.awayLogo} name={ev.awayName} size={46} />
        </div>
      </div>

      {/* Time row */}
      <div style={{ textAlign: "center", padding: "2px 14px 10px", display: "flex", flexDirection: "column", alignItems: "center", gap: 3 }}>
        {/* Countdown / elapsed clock */}
        <span style={{ fontFamily: "monospace", fontSize: 14, color: "#555", letterSpacing: 3 }}>
          {isLive
            ? <LiveClock seed={ev.clock || "00:00:00"} />
            : isUpcoming
            ? <CountdownClock targetDate={ev.date} />
            : ev.bstTime || "FT"}
        </span>

        {/* BST match time with day/night info */}
        {isUpcoming && matchTimeLabel && (
          <span style={{ fontSize: 11, color: isDayTime ? "#facc15" : "#7c6fff", fontWeight: 600, letterSpacing: 0.3 }}>
            {isDayTime ? "☀️" : "🌙"} {matchTimeLabel}
          </span>
        )}
      </div>
    </div>
  );
}

/* ── Skeleton loader ── */
function Skeleton() {
  return (
    <div style={{ padding: "0 12px" }}>
      {[1, 2, 3, 4].map(i => (
        <div key={i} style={{ background: "#0d1520", border: "1.5px solid #1a2540", borderRadius: 14, marginBottom: 10, padding: "10px 14px" }}>
          <div style={{ height: 11, background: "#1a2540", borderRadius: 6, marginBottom: 12, width: "50%" }} />
          <div style={{ display: "flex", alignItems: "center", gap: 10, marginBottom: 10 }}>
            <div style={{ width: 46, height: 46, borderRadius: "50%", background: "#1a2540", flexShrink: 0 }} />
            <div style={{ height: 12, background: "#1a2540", borderRadius: 6, flex: 1 }} />
            <div style={{ width: 30, height: 12, background: "#1a2540", borderRadius: 6, flexShrink: 0 }} />
            <div style={{ height: 12, background: "#1a2540", borderRadius: 6, flex: 1 }} />
            <div style={{ width: 46, height: 46, borderRadius: "50%", background: "#1a2540", flexShrink: 0 }} />
          </div>
          <div style={{ height: 11, background: "#1a2540", borderRadius: 6, width: "25%", margin: "0 auto" }} />
        </div>
      ))}
    </div>
  );
}

/* ── Main LiveEvents ── */
export default function LiveEvents({ onWatch, searchQuery = "" }: Props) {
  const [statusFilter, setStatusFilter] = useState<StatusFilter>("all");
  const { events, loading, error, refresh } = useSportsData();

  const allCnt      = events.length;
  const liveCnt     = events.filter(e => e.status === "live").length;
  const upcomingCnt = events.filter(e => e.status === "upcoming").length;
  const finishedCnt = events.filter(e => e.status === "finished").length;

  const filtered = events.filter(e => {
    const matchStatus = statusFilter === "all" || e.status === statusFilter;
    const q = searchQuery.toLowerCase();
    const matchSearch = !q || e.homeName?.toLowerCase().includes(q) || e.awayName?.toLowerCase().includes(q) || e.league?.toLowerCase().includes(q);
    return matchStatus && matchSearch;
  });

  const pills: { k: StatusFilter; label: string }[] = [
    { k: "all",      label: `✓ All (${allCnt})` },
    { k: "live",     label: `Live (${liveCnt})` },
    { k: "upcoming", label: `Upcoming (${upcomingCnt})` },
    { k: "finished", label: `Finished (${finishedCnt})` },
  ];

  return (
    <div style={{ flex: 1, overflowY: "auto", background: "#0a0e1a", paddingBottom: 24 }}>

      {/* Status filter pills */}
      <div style={{ display: "flex", gap: 8, overflowX: "auto", padding: "12px 12px 10px", scrollbarWidth: "none" }}>
        {pills.map(({ k, label }) => (
          <button key={k} onClick={() => setStatusFilter(k)} style={{
            padding: "7px 18px", borderRadius: 999,
            border: `1.5px solid ${statusFilter === k ? "#00d4e8" : "#2a3550"}`,
            fontSize: 13, fontWeight: 600, cursor: "pointer", whiteSpace: "nowrap",
            color: statusFilter === k ? "#00d4e8" : "#888",
            background: statusFilter === k ? "rgba(0,212,232,0.07)" : "transparent",
            flexShrink: 0, transition: "all 0.15s",
          }}>{label}</button>
        ))}
      </div>

      {/* Error */}
      {error && (
        <div style={{ margin: "0 12px 10px", padding: 12, background: "rgba(229,62,62,0.1)", border: "1px solid #e53e3e", borderRadius: 10, fontSize: 12, color: "#e53e3e", textAlign: "center" }}>
          ⚠️ {error}{"  "}
          <button onClick={refresh} style={{ background: "none", border: "none", color: "#00d4e8", cursor: "pointer", fontSize: 12 }}>Retry</button>
        </div>
      )}

      {/* Cards */}
      {loading ? <Skeleton /> : (
        <div style={{ padding: "0 12px" }}>
          {filtered.length === 0 ? (
            <div style={{ textAlign: "center", color: "#444", marginTop: 60, fontSize: 14 }}>No matches found</div>
          ) : (
            filtered.map(e => <MatchCard key={e.id} ev={e as any} onWatch={onWatch} />)
          )}
        </div>
      )}

      <style>{`
        @keyframes pulse { 0%,100%{opacity:1} 50%{opacity:0.3} }
        ::-webkit-scrollbar { display: none; }
      `}</style>
    </div>
  );
}
