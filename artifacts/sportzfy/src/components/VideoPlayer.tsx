import { useEffect, useRef, useState, useCallback } from "react";
import Hls from "hls.js";
import "plyr/dist/plyr.css";
import "video.js/dist/video-js.min.css";
import type { Match } from "../data/matches";

/* Expose HLS.js globally so DPlayer (CDN) finds it without fetching its own copy */
(window as any).Hls = Hls;

interface Props {
  match: Match;
  streamUrl: string;
  streamName: string;
  onClose: () => void;
}

interface HlsLevel { height: number; width: number; bitrate: number; index: number; }

type PlayerEngine = "hlsjs" | "dashjs" | "shaka" | "videojs" | "plyr" | "dplayer";

const ENGINES: { id: PlayerEngine; label: string; color: string; icon: string; desc: string }[] = [
  { id: "hlsjs",   label: "HLS.js",   color: "#ff6b35", icon: "⚡", desc: "HLS / M3U8" },
  { id: "dashjs",  label: "DASH.js",  color: "#e879f9", icon: "📡", desc: "MPEG-DASH" },
  { id: "shaka",   label: "Shaka",    color: "#4ade80", icon: "💎", desc: "DRM + DASH" },
  { id: "videojs", label: "Video.js", color: "#f7931e", icon: "🎬", desc: "Feature-rich" },
  { id: "plyr",    label: "Plyr",     color: "#00b4d8", icon: "🎨", desc: "Beautiful UI" },
  { id: "dplayer", label: "DPlayer",  color: "#a855f7", icon: "🚀", desc: "Pro streaming" },
];

/**
 * Quick-pick streams shown as tabs inside the player.
 * All URLs are HLS (.m3u8) and go through the server-side CORS proxy.
 * Verified live — July 2026.
 */
function proxied(url: string) { return `/api/proxy?url=${encodeURIComponent(url)}`; }

const DEMO_STREAMS = [
  { name: "FIFA WC ENG",  url: proxied("https://hls.livekhelatv.website/mks/lktv/fifa-worldcup-eng/master.m3u8") },
  { name: "FIFA+ English",url: proxied("https://a62dad94.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/UmFrdXRlblRWLWV1X0ZJRkFQbHVzRW5nbGlzaF9ITFM/playlist.m3u8") },
  { name: "beIN Sports 1",url: proxied("https://cdn-uw2-prod.tsv2.amagi.tv/linear/amg02873-kravemedia-mtrspt1-distrotv/playlist.m3u8") },
  { name: "beIN Sports 2",url: proxied("https://andro.226503.xyz/checklist/androstreamlivebs2.m3u8") },
  { name: "beIN Sports 3",url: proxied("https://andro.226503.xyz/checklist/androstreamlivebs3.m3u8") },
  { name: "beIN Xtra",    url: proxied("https://bein-esp-xumo.amagi.tv/playlistR720P.m3u8") },
  { name: "Caze TV 4K",   url: proxied("https://dfr80qz435crc.cloudfront.net/MNOP/Amagi/Caze/Caze_TV_BR/1080p-vtt/index.m3u8") },
  { name: "TyC Sports",   url: proxied("https://amg26268-amg26268c14-freelivesports-emea-10267.playouts.now.amagi.tv/ts-us-e2-n2/playlist/amg26268-sportsstudio-tycsports-freelivesportsemea/playlist.m3u8") },
  { name: "Telemundo",    url: proxied("https://nbculocallive.akamaized.net/hls/live/2037499/puertorico/stream1/master.m3u8") },
  { name: "Euro TV",      url: proxied("https://stream.ottplus.bd/live/euro_sports_hd_abr/live/euro_sports_hd/chunks.m3u8") },
  { name: "ESPN 2",       url: proxied("https://tv.topmediatv.net:25463/live/TopMediaWeb/bOteTR8ED1/108.m3u8") },
  { name: "Cricket Gold", url: proxied("https://streams2.sofast.tv/ptnr-yupptv/title-cricketgold/v1/manifest/611d79b11b77e2f571934fd80ca1413453772ac7/b2048bb8-1686-4432-aa50-647245383e0c/bfc6a36e-c250-4afe-b6c9-2bc57855bb7d/4.m3u8") },
  { name: "FIFA+ USA",    url: proxied("https://d2w9q46ikgrcwx.cloudfront.net/v1/master/3722c60a815c199d9c0ef36c5b73da68a62b09d1/cc-of5cbk3sav3w5/v1/sysdata_s_p_a_fifa_7/samsungheadend_us/latest/main/hls/playlist.m3u8") },
  { name: "FIFA+ España", url: proxied("https://d63fabad.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/UmFrdXRlblRWLWVzX0ZJRkFQbHVzU3BhbmlzaF9ITFM/playlist.m3u8") },
  { name: "FIFA+ Brasil", url: proxied("https://e3be9ac5.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/TEctYnJfRklGQVBsdXNQb3J0dWd1ZXNlX0hMUw/playlist.m3u8") },
];

const pad = (n: number) => String(Math.floor(n)).padStart(2, "0");
const fmtTime = (s: number) => `${pad(s / 3600)}:${pad((s % 3600) / 60)}:${pad(s % 60)}`;

/* ── Team logo image (falls back to initials) ── */
function TeamLogo({ src, name, size = 32 }: { src?: string; name: string; size?: number }) {
  const [err, setErr] = useState(false);
  const isUrl = src && (src.startsWith("http") || src.startsWith("/"));
  if (isUrl && !err) {
    return (
      <img src={src} alt={name}
        style={{ width: size, height: size, borderRadius: "50%", objectFit: "contain", background: "#fff", flexShrink: 0, padding: 2 }}
        onError={() => setErr(true)}
      />
    );
  }
  /* emoji flag or initials fallback */
  if (src && !isUrl) {
    return <span style={{ fontSize: size * 0.7, lineHeight: 1 }}>{src}</span>;
  }
  return (
    <div style={{ width: size, height: size, borderRadius: "50%", background: "#1e2a3a", display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0, border: "1px solid #2a3a50" }}>
      <span style={{ fontSize: size * 0.3, fontWeight: 800, color: "#00d4e8" }}>{name.slice(0, 2).toUpperCase()}</span>
    </div>
  );
}

/* ── Quality / Track selector ── */
function QualityModal({ levels, currentLevel, onSelect, onClose }: {
  levels: HlsLevel[]; currentLevel: number; onSelect: (i: number) => void; onClose: () => void;
}) {
  const [sel, setSel] = useState(currentLevel);
  const displayLevels = levels.length > 0
    ? levels.map((l, i) => ({ label: l.height >= 1080 ? `🔵 HD ${l.height}p` : l.height >= 720 ? `🟢 HD ${l.height}p` : `🟡 SD ${l.height}p`, idx: i }))
    : [
        { label: "🔵 HD 1080p", idx: 7 }, { label: "🔵 HD 720p", idx: 5 },
        { label: "🟡 SD 480p", idx: 3 },  { label: "🟡 SD 360p", idx: 1 },
      ];
  return (
    <div style={{ position: "absolute", inset: 0, background: "rgba(0,0,0,0.7)", display: "flex", alignItems: "flex-end", justifyContent: "center", zIndex: 60 }} onClick={onClose}>
      <div onClick={e => e.stopPropagation()} style={{ background: "#1a1f2e", borderRadius: "18px 18px 0 0", width: "100%", maxWidth: 480, paddingBottom: 16, animation: "slideUp 0.2s ease" }}>
        <div style={{ width: 40, height: 4, background: "#333", borderRadius: 2, margin: "12px auto 16px" }} />
        <h3 style={{ textAlign: "center", color: "#fff", fontSize: 16, fontWeight: 800, margin: "0 0 8px" }}>Quality / Track</h3>
        <div onClick={() => setSel(-1)} style={{ display: "flex", justifyContent: "space-between", alignItems: "center", padding: "13px 20px", borderTop: "1px solid #2a2f40", cursor: "pointer" }}>
          <span style={{ color: "#fff", fontSize: 15, fontWeight: 600 }}>🔄 Auto</span>
          {sel === -1 && <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#00d4e8" strokeWidth="2.5"><polyline points="20 6 9 17 4 12" /></svg>}
        </div>
        {displayLevels.map(opt => (
          <div key={opt.idx} onClick={() => setSel(opt.idx)} style={{ display: "flex", justifyContent: "space-between", alignItems: "center", padding: "13px 20px", borderTop: "1px solid #2a2f40", cursor: "pointer" }}>
            <span style={{ color: "#fff", fontSize: 15 }}>{opt.label}</span>
            {sel === opt.idx && <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#00d4e8" strokeWidth="2.5"><polyline points="20 6 9 17 4 12" /></svg>}
          </div>
        ))}
        <div style={{ display: "flex", borderTop: "1px solid #2a2f40", marginTop: 8 }}>
          <button onClick={onClose} style={{ flex: 1, padding: 14, background: "none", border: "none", cursor: "pointer", color: "#888", fontSize: 14, fontWeight: 700 }}>CANCEL</button>
          <button onClick={() => { onSelect(sel); onClose(); }} style={{ flex: 1, padding: 14, background: "none", border: "none", cursor: "pointer", color: "#00d4e8", fontSize: 14, fontWeight: 700 }}>APPLY</button>
        </div>
      </div>
    </div>
  );
}

/* ── Engine selector modal ── */
function EngineModal({ current, onSelect, onClose }: { current: PlayerEngine; onSelect: (e: PlayerEngine) => void; onClose: () => void }) {
  return (
    <div style={{ position: "absolute", inset: 0, background: "rgba(0,0,0,0.7)", display: "flex", alignItems: "flex-end", justifyContent: "center", zIndex: 60 }} onClick={onClose}>
      <div onClick={e => e.stopPropagation()} style={{ background: "#1a1f2e", borderRadius: "18px 18px 0 0", width: "100%", maxWidth: 480, paddingBottom: 16, animation: "slideUp 0.2s ease" }}>
        <div style={{ width: 40, height: 4, background: "#333", borderRadius: 2, margin: "12px auto 16px" }} />
        <h3 style={{ textAlign: "center", color: "#fff", fontSize: 16, fontWeight: 800, margin: "0 0 8px" }}>Player Engine</h3>
        {ENGINES.map(eng => (
          <div key={eng.id} onClick={() => { onSelect(eng.id); onClose(); }} style={{ display: "flex", alignItems: "center", gap: 14, padding: "13px 20px", borderTop: "1px solid #2a2f40", cursor: "pointer", background: current === eng.id ? "rgba(255,255,255,0.04)" : "transparent" }}>
            <div style={{ width: 10, height: 10, borderRadius: "50%", background: eng.color, flexShrink: 0, boxShadow: current === eng.id ? `0 0 8px ${eng.color}` : "none" }} />
            <div style={{ flex: 1 }}>
              <div style={{ color: "#fff", fontSize: 15, fontWeight: 700 }}>{eng.label}</div>
              <div style={{ color: "#666", fontSize: 12 }}>{eng.desc}</div>
            </div>
            {current === eng.id && <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#00d4e8" strokeWidth="2.5"><polyline points="20 6 9 17 4 12" /></svg>}
          </div>
        ))}
        <button onClick={onClose} style={{ width: "100%", padding: 14, background: "none", border: "none", cursor: "pointer", color: "#888", fontSize: 14, fontWeight: 700 }}>CANCEL</button>
      </div>
    </div>
  );
}

/* ══════════════════════════════════════════════
   MAIN PLAYER COMPONENT
══════════════════════════════════════════════ */
export default function VideoPlayer({ match, streamUrl: initUrl, streamName: initName, onClose }: Props) {
  const videoRef     = useRef<HTMLVideoElement>(null);
  const hlsRef         = useRef<Hls | null>(null);
  const dashRef        = useRef<any>(null);
  const vjsRef         = useRef<any>(null);
  const shakaRef       = useRef<any>(null);
  const plyrRef        = useRef<any>(null);
  const dpRef          = useRef<any>(null);
  const dpContainerRef = useRef<HTMLDivElement>(null); // DPlayer dedicated mount point
  const controlTimer = useRef<ReturnType<typeof setTimeout> | null>(null);
  const stallTimer   = useRef<ReturnType<typeof setTimeout> | null>(null);

  const [engine, setEngine]               = useState<PlayerEngine>("hlsjs");
  const [streamUrl, setStreamUrl]         = useState(initUrl);
  const [streamName, setStreamName]       = useState(initName);
  const [status, setStatus]               = useState<"loading"|"ready"|"error">("loading");
  const [playing, setPlaying]             = useState(false);
  const [buffering, setBuffering]         = useState(true);
  const [currentTime, setCurrentTime]     = useState(0);
  const [duration, setDuration]           = useState(0);
  const [progress, setProgress]           = useState(0);
  const [volume, setVolume]               = useState(1);
  const [muted, setMuted]                 = useState(false);
  const [levels, setLevels]               = useState<HlsLevel[]>([]);
  const [currentLevel, setCurrentLevel]   = useState(-1);
  const [bufferPct, setBufferPct]         = useState(0);
  const [stallCount, setStallCount]       = useState(0);
  const [showControls, setShowControls]   = useState(true);
  const [showEngine, setShowEngine]       = useState(false);
  const [showQuality, setShowQuality]     = useState(false);
  const [locked, setLocked]               = useState(false);       // 🔒 Lock controls
  const [floating, setFloating]           = useState(false);       // 📌 Floating player
  const [fullscreen, setFullscreen]       = useState(false);
  const [autoRecovering, setAutoRecovering] = useState(false);

  const currentEng = ENGINES.find(e => e.id === engine)!;
  const useNative  = engine === "plyr" || engine === "videojs" || engine === "dplayer";

  /* ── destroy all engines ── */
  const destroyAll = useCallback(() => {
    hlsRef.current?.destroy(); hlsRef.current = null;
    if (dashRef.current) { try { dashRef.current.destroy(); } catch {} dashRef.current = null; }
    if (vjsRef.current) { try { vjsRef.current.dispose(); } catch {} vjsRef.current = null; }
    if (shakaRef.current) { try { shakaRef.current.destroy(); } catch {} shakaRef.current = null; }
    if (plyrRef.current) { try { plyrRef.current.destroy(); } catch {} plyrRef.current = null; }
    dpRef.current = null;
    if (stallTimer.current) { clearTimeout(stallTimer.current); stallTimer.current = null; }
  }, []);

  /* ── video element events helper ── */
  const attachVideoEvents = (v: HTMLVideoElement) => {
    v.onplaying = () => { setBuffering(false); setPlaying(true); setStatus("ready"); };
    v.onpause   = () => setPlaying(false);
    v.onwaiting = () => setBuffering(true);
    v.ontimeupdate = () => {
      setCurrentTime(v.currentTime);
      if (v.duration) { setDuration(v.duration); setProgress((v.currentTime / v.duration) * 100); }
      /* buffer health */
      if (v.buffered.length > 0) {
        const end  = v.buffered.end(v.buffered.length - 1);
        const pct  = v.duration ? Math.min(100, Math.round((end / v.duration) * 100)) : 0;
        setBufferPct(pct);
      }
    };
    v.onerror = () => setStatus("error");
  };

  /* ── HLS.js engine ── */
  const loadHlsJs = useCallback((url: string) => {
    const v = videoRef.current; if (!v) return;
    setStatus("loading");
    if (Hls.isSupported()) {
      const hls = new Hls({
        enableWorker: true, lowLatencyMode: false,
        maxBufferLength: 60, maxMaxBufferLength: 120,
        maxBufferSize: 60 * 1000 * 1000,
        fragLoadingTimeOut: 20000, manifestLoadingTimeOut: 10000,
      });
      hlsRef.current = hls;
      hls.loadSource(url);
      hls.attachMedia(v);
      hls.on(Hls.Events.MANIFEST_PARSED, (_, d) => {
        setLevels(d.levels.map((l, i) => ({ height: l.height, width: l.width || 0, bitrate: l.bitrate, index: i })));
        v.play().catch(() => {});
      });
      hls.on(Hls.Events.LEVEL_SWITCHED, (_, d) => setCurrentLevel(d.level));
      hls.on(Hls.Events.ERROR, (_, d) => {
        if (d.details === Hls.ErrorDetails.BUFFER_STALLED_ERROR) {
          setStallCount(c => c + 1); setBuffering(true); setAutoRecovering(true);
          if (hls.currentLevel > 0) hls.currentLevel = Math.max(0, hls.currentLevel - 1);
          stallTimer.current = setTimeout(() => setAutoRecovering(false), 3000);
        }
        if (d.fatal) {
          if (d.type === Hls.ErrorTypes.NETWORK_ERROR) { setAutoRecovering(true); hls.startLoad(); stallTimer.current = setTimeout(() => setAutoRecovering(false), 4000); }
          else hls.recoverMediaError();
        }
      });
    } else if (v.canPlayType("application/vnd.apple.mpegurl")) {
      v.src = url; v.play().catch(() => {});
    } else { setStatus("error"); return; }
    attachVideoEvents(v);
  }, []);

  /* ── DASH.js engine — auto-detects stream type ── */
  /* .mpd  → real dash.js MPEG-DASH player               */
  /* .m3u8 → HLS.js fallback (DASH.js can't play HLS)    */
  const loadDashJs = useCallback((url: string) => {
    const v = videoRef.current; if (!v) return;
    setStatus("loading");

    const isHls = url.includes(".m3u8") || url.includes("m3u8");

    /* ── HLS stream: use HLS.js directly ── */
    if (isHls) {
      if (Hls.isSupported()) {
        const hls = new Hls({ enableWorker: true, maxBufferLength: 60, maxMaxBufferLength: 120 });
        hlsRef.current = hls;
        hls.loadSource(url);
        hls.attachMedia(v);
        hls.on(Hls.Events.MANIFEST_PARSED, (_, d) => {
          setLevels(d.levels.map((l, i) => ({ height: l.height, width: l.width || 0, bitrate: l.bitrate, index: i })));
          v.play().catch(() => {});
        });
        hls.on(Hls.Events.LEVEL_SWITCHED, (_, d) => setCurrentLevel(d.level));
        hls.on(Hls.Events.ERROR, (_, d) => {
          if (d.fatal) {
            if (d.type === Hls.ErrorTypes.NETWORK_ERROR) { hls.startLoad(); setAutoRecovering(true); stallTimer.current = setTimeout(() => setAutoRecovering(false), 4000); }
            else hls.recoverMediaError();
          }
        });
        attachVideoEvents(v);
      } else if (v.canPlayType("application/vnd.apple.mpegurl")) {
        v.src = url; v.play().catch(() => {}); attachVideoEvents(v);
      } else { setStatus("error"); }
      return;
    }

    /* ── DASH .mpd stream: load dash.js from CDN ── */
    const loadScript = () => new Promise<void>((res, rej) => {
      if ((window as any).dashjs) { res(); return; }
      const s = document.createElement("script");
      s.src = "https://cdn.dashjs.org/latest/dash.all.min.js";
      s.onload = () => res();
      s.onerror = () => rej(new Error("dash.js CDN failed"));
      document.head.appendChild(s);
    });

    loadScript().then(() => {
      const dashjs = (window as any).dashjs;
      if (!dashjs) { setStatus("error"); return; }
      const player = dashjs.MediaPlayer().create();
      dashRef.current = player;
      player.initialize(v, url, true);
      player.updateSettings({
        streaming: {
          abr: { autoSwitchBitrate: { video: true, audio: true } },
          buffer: { bufferTimeAtTopQuality: 30, stableBufferTime: 12 },
        },
      });
      player.on(dashjs.MediaPlayer.events.PLAYBACK_STARTED, () => { setPlaying(true); setBuffering(false); setStatus("ready"); });
      player.on(dashjs.MediaPlayer.events.PLAYBACK_PAUSED,  () => setPlaying(false));
      player.on(dashjs.MediaPlayer.events.PLAYBACK_WAITING, () => setBuffering(true));
      player.on(dashjs.MediaPlayer.events.PLAYBACK_PLAYING, () => { setBuffering(false); setStatus("ready"); });
      player.on(dashjs.MediaPlayer.events.ERROR,            () => setStatus("error"));
      player.on(dashjs.MediaPlayer.events.QUALITY_CHANGE_RENDERED, () => {
        try {
          const bi = player.getBitrateInfoListFor("video") || [];
          setLevels(bi.map((b: any, i: number) => ({ height: b.height ?? 0, width: b.width ?? 0, bitrate: b.bitrate ?? 0, index: i })));
          setCurrentLevel(player.getQualityFor("video"));
        } catch {}
      });
      attachVideoEvents(v);
    }).catch(() => setStatus("error"));
  }, []);

  /* ── Shaka Player engine — HLS.js handles stream, Shaka used for DASH/DRM ── */
  const loadShaka = useCallback(async (url: string) => {
    const v = videoRef.current; if (!v) return;
    setStatus("loading");

    const isDash = url.includes(".mpd");

    /* For HLS streams, use HLS.js (more reliable than Shaka HLS parser in browser) */
    if (!isDash && Hls.isSupported()) {
      const hls = new Hls({ enableWorker: true, maxBufferLength: 60, maxMaxBufferLength: 120 });
      hlsRef.current = hls;
      hls.loadSource(url);
      hls.attachMedia(v);
      hls.on(Hls.Events.MANIFEST_PARSED, (_, d) => {
        setLevels(d.levels.map((l, i) => ({ height: l.height, width: l.width || 0, bitrate: l.bitrate, index: i })));
        v.play().catch(() => {});
      });
      hls.on(Hls.Events.LEVEL_SWITCHED, (_, d) => setCurrentLevel(d.level));
      hls.on(Hls.Events.ERROR, (_, d) => {
        if (d.fatal) {
          if (d.type === Hls.ErrorTypes.NETWORK_ERROR) { setAutoRecovering(true); hls.startLoad(); stallTimer.current = setTimeout(() => setAutoRecovering(false), 4000); }
          else hls.recoverMediaError();
        }
      });
      attachVideoEvents(v);
      return;
    }

    /* For DASH .mpd streams — use real Shaka */
    try {
      const shaka = await import("shaka-player") as any;
      shaka.polyfill.installAll();
      if (!shaka.Player.isBrowserSupported()) { setStatus("error"); return; }
      const player = new shaka.Player();
      await player.attach(v);
      shakaRef.current = player;
      player.configure({
        streaming: { bufferingGoal: 30, rebufferingGoal: 2, bufferBehind: 30 },
        abr: { enabled: true },
      });
      player.addEventListener("error", () => setStatus("error"));
      attachVideoEvents(v);
      await player.load(url);
      v.play().catch(() => {});
      const tracks = player.getVariantTracks();
      setLevels(tracks.map((t: any, i: number) => ({ height: t.height ?? 0, width: t.width ?? 0, bitrate: t.bandwidth ?? 0, index: i })));
    } catch { setStatus("error"); }
  }, []);

  /* ── Video.js engine — HLS.js loads stream, Video.js provides controls UI ── */
  const loadVideoJs = useCallback(async (url: string) => {
    const v = videoRef.current; if (!v) return;
    setStatus("loading");
    try {
      /* HLS.js loads the stream reliably */
      if (Hls.isSupported()) {
        const hls = new Hls({ enableWorker: true, maxBufferLength: 60, maxMaxBufferLength: 120 });
        hlsRef.current = hls;
        hls.loadSource(url);
        hls.attachMedia(v);
        hls.on(Hls.Events.MANIFEST_PARSED, (_, d) => {
          setLevels(d.levels.map((l, i) => ({ height: l.height, width: l.width || 0, bitrate: l.bitrate, index: i })));
          v.play().catch(() => {});
        });
        hls.on(Hls.Events.LEVEL_SWITCHED, (_, d) => setCurrentLevel(d.level));
        hls.on(Hls.Events.ERROR, (_, d) => {
          if (d.fatal) {
            if (d.type === Hls.ErrorTypes.NETWORK_ERROR) hls.startLoad();
            else hls.recoverMediaError();
          }
        });
      } else if (v.canPlayType("application/vnd.apple.mpegurl")) {
        v.src = url; v.play().catch(() => {});
      } else { setStatus("error"); return; }

      /* Video.js wraps for its dark-theme control bar only — no source via vjs */
      const { default: videojs } = await import("video.js");
      v.className = "video-js vjs-big-play-centered";
      const player = videojs(v, { autoplay: true, controls: true, preload: "auto" });
      vjsRef.current = player;
      player.on("playing", () => { setBuffering(false); setPlaying(true); setStatus("ready"); });
      player.on("pause",   () => setPlaying(false));
      player.on("waiting", () => setBuffering(true));
      player.on("error",   () => { /* HLS.js handles errors — ignore vjs error */ });

      attachVideoEvents(v);
    } catch { setStatus("error"); }
  }, []);

  /* ── Plyr engine ── */
  const loadPlyr = useCallback(async (url: string) => {
    const v = videoRef.current; if (!v) return;
    setStatus("loading");
    try {
      const { default: Plyr } = await import("plyr");

      if (Hls.isSupported()) {
        const hls = new Hls({ maxBufferLength: 60, enableWorker: true, maxMaxBufferLength: 120 });
        hlsRef.current = hls;
        hls.loadSource(url);
        hls.attachMedia(v);

        /* Init Plyr only AFTER HLS has parsed manifest & attached stream */
        hls.on(Hls.Events.MANIFEST_PARSED, (_, d) => {
          setLevels(d.levels.map((l, i) => ({ height: l.height, width: l.width || 0, bitrate: l.bitrate, index: i })));

          /* Plyr wraps the video element in-place — use LOCAL svg sprite, no CDN */
          const player = new Plyr(v, {
            autoplay: true,
            muted: false,
            controls: ["play", "progress", "current-time", "mute", "volume", "fullscreen"],
            fullscreen: { enabled: true, fallback: true, iosNative: false },
            iconUrl: "/plyr.svg",        // local copy in public/ — avoids cdn.plyr.io
            blankVideo: "data:video/mp4;base64,AAAAIGZ0eXBpc29tAAACAGlzb21pc28yYXZjMW1wNDEAAAAIZnJlZQAAAu1tZGF0", // tiny blank
          });
          plyrRef.current = player;

          /* force Plyr's wrapper to fill our container */
          const wrap = v.closest(".plyr") as HTMLElement | null;
          if (wrap) {
            wrap.style.cssText += ";width:100%!important;height:100%!important;position:absolute!important;inset:0!important;background:#000";
            const inner = wrap.querySelector(".plyr__video-wrapper") as HTMLElement | null;
            if (inner) inner.style.cssText += ";height:100%!important";
          }

          player.on("playing", () => { setBuffering(false); setPlaying(true); setStatus("ready"); });
          player.on("pause",   () => setPlaying(false));
          player.on("waiting", () => setBuffering(true));
          player.on("error",   () => setStatus("error"));

          v.play().catch(() => {});
        });

        hls.on(Hls.Events.ERROR, (_, d) => {
          if (d.fatal) {
            if (d.type === Hls.ErrorTypes.NETWORK_ERROR) hls.startLoad();
            else hls.recoverMediaError();
          }
        });
      } else if (v.canPlayType("application/vnd.apple.mpegurl")) {
        v.src = url;
        const player = new Plyr(v, { autoplay: true, iconUrl: "/plyr.svg", controls: ["play","progress","current-time","mute","volume","fullscreen"] });
        plyrRef.current = player;
        player.on("playing", () => { setBuffering(false); setPlaying(true); setStatus("ready"); });
        player.on("pause",   () => setPlaying(false));
        v.play().catch(() => {});
      } else {
        setStatus("error");
      }

      attachVideoEvents(v);
    } catch { setStatus("error"); }
  }, []);

  /* ── DPlayer engine (uses dedicated dpContainerRef — never touches React DOM) ── */
  const loadDPlayer = useCallback((url: string) => {
    const container = dpContainerRef.current;
    if (!container) { setStatus("error"); return; }
    setStatus("loading");

    const loadScripts = () => new Promise<void>((res, rej) => {
      /* CSS */
      if (!document.getElementById("dp-css")) {
        const l = document.createElement("link");
        l.id = "dp-css"; l.rel = "stylesheet";
        l.href = "https://cdn.jsdelivr.net/npm/dplayer@1.26.0/dist/DPlayer.min.css";
        document.head.appendChild(l);
      }
      /* JS */
      if ((window as any).DPlayer) { res(); return; }
      const s = document.createElement("script");
      s.src = "https://cdn.jsdelivr.net/npm/dplayer@1.26.0/dist/DPlayer.min.js";
      s.onload  = () => res();
      s.onerror = () => rej(new Error("DPlayer CDN failed"));
      document.head.appendChild(s);
    });

    loadScripts().then(() => {
      const DP = (window as any).DPlayer;
      if (!DP) { setStatus("error"); return; }
      container.innerHTML = ""; // clear previous instance

      const dp = new DP({
        container,
        autoplay: true,
        theme: "#a855f7",
        video: {
          url,
          type: "customHls",
          customType: {
            customHls: (el: HTMLVideoElement) => {
              if (Hls.isSupported()) {
                const hls = new Hls({ maxBufferLength: 60, enableWorker: true });
                hlsRef.current = hls;
                hls.loadSource(el.src);
                hls.attachMedia(el);
                hls.on(Hls.Events.MANIFEST_PARSED, (_, d) => {
                  setLevels(d.levels.map((l, i) => ({ height: l.height, width: l.width || 0, bitrate: l.bitrate, index: i })));
                  el.play().catch(() => {});
                  setStatus("ready"); setBuffering(false);
                });
                hls.on(Hls.Events.ERROR, (_, d) => { if (d.fatal) hls.recoverMediaError(); });
              } else {
                el.src = url; el.play().catch(() => {});
              }
            },
          },
        },
      });

      dpRef.current = dp;
      dp.on("play",    () => { setPlaying(true);  setBuffering(false); setStatus("ready"); });
      dp.on("pause",   () => setPlaying(false));
      dp.on("waiting", () => setBuffering(true));
      dp.on("playing", () => { setBuffering(false); setStatus("ready"); });
      dp.on("error",   () => setStatus("error"));
    }).catch(() => setStatus("error"));
  }, []);

  /* ── Load stream ── */
  const loadStream = useCallback((url: string, eng: PlayerEngine) => {
    destroyAll();
    setBuffering(true); setPlaying(false); setLevels([]); setCurrentLevel(-1);
    setBufferPct(0); setStallCount(0); setStatus("loading"); setAutoRecovering(false);
    if (eng === "hlsjs")        loadHlsJs(url);
    else if (eng === "dashjs")  loadDashJs(url);
    else if (eng === "shaka")   loadShaka(url);
    else if (eng === "videojs") loadVideoJs(url);
    else if (eng === "plyr")    loadPlyr(url);
    else if (eng === "dplayer") loadDPlayer(url);
  }, [destroyAll, loadHlsJs, loadDashJs, loadShaka, loadVideoJs, loadPlyr, loadDPlayer]);

  useEffect(() => { loadStream(streamUrl, engine); return () => destroyAll(); }, [streamUrl, engine]);

  /* ── Switch engine on error ── */
  const switchToNextEngine = () => {
    const order: PlayerEngine[] = ["hlsjs","dashjs","shaka","videojs","plyr","dplayer"];
    const next = order[(order.indexOf(engine) + 1) % order.length];
    setEngine(next);
  };

  /* ── Controls auto-hide ── */
  const resetControlsTimer = () => {
    setShowControls(true);
    if (controlTimer.current) clearTimeout(controlTimer.current);
    controlTimer.current = setTimeout(() => setShowControls(false), 3500);
  };

  useEffect(() => { resetControlsTimer(); return () => { if (controlTimer.current) clearTimeout(controlTimer.current); }; }, []);

  /* ── Playback helpers ── */
  const togglePlay = () => {
    const v = videoRef.current; if (!v) return;
    if (v.paused) v.play().catch(() => {}); else v.pause();
  };
  const seek = (s: number) => { const v = videoRef.current; if (v) v.currentTime = Math.max(0, v.currentTime + s); };
  const setQuality = (idx: number) => { setCurrentLevel(idx); if (hlsRef.current) hlsRef.current.currentLevel = idx; };

  /* ── Fullscreen + orientation lock ── */
  const toggleFullscreen = async () => {
    const el = document.documentElement;
    if (!document.fullscreenElement) {
      await el.requestFullscreen?.();
      try { await (screen.orientation as any).lock?.("landscape"); } catch {}
      setFullscreen(true);
    } else {
      await document.exitFullscreen?.();
      try { (screen.orientation as any).unlock?.(); } catch {}
      setFullscreen(false);
    }
  };
  useEffect(() => {
    const fn = () => setFullscreen(!!document.fullscreenElement);
    document.addEventListener("fullscreenchange", fn);
    return () => document.removeEventListener("fullscreenchange", fn);
  }, []);

  /* ── Floating pip style ── */
  const floatingStyle: React.CSSProperties = floating ? {
    position: "fixed", bottom: 16, right: 16,
    width: 240, height: 135,
    borderRadius: 10, zIndex: 9999,
    boxShadow: "0 8px 32px rgba(0,0,0,0.8)",
    border: "2px solid #00d4e8",
    overflow: "hidden",
  } : {};

  /* icon helper */
  const Btn = ({ onClick, children, style = {} }: { onClick: (e: React.MouseEvent) => void; children: React.ReactNode; style?: React.CSSProperties }) => (
    <button onClick={onClick} style={{ background: "none", border: "none", cursor: "pointer", color: "#fff", padding: 0, display: "flex", alignItems: "center", justifyContent: "center", ...style }}>
      {children}
    </button>
  );

  /* ══════════════ RENDER ══════════════ */
  return (
    <div style={{
      position: floating ? "static" : "fixed", inset: floating ? undefined : 0,
      background: "#0a0d16", zIndex: floating ? undefined : 999,
      display: "flex", flexDirection: "column",
      overflowY: floating ? undefined : "auto",
      maxWidth: 480, margin: "0 auto",
      ...floatingStyle,
    }}>

      {/* ══ VIDEO AREA — 16:9 aspect ratio ══ */}
      <div
        style={{
          position: "relative", width: "100%",
          aspectRatio: floating ? undefined : "16/9",
          height: floating ? "100%" : undefined,
          background: "#000", flexShrink: 0, cursor: "pointer",
          userSelect: "none",
        }}
        onClick={() => { if (!locked) { resetControlsTimer(); togglePlay(); } }}
        onTouchEnd={() => { if (!locked) resetControlsTimer(); }}
      >
        <video
          ref={videoRef}
          style={{ width: "100%", height: "100%", objectFit: "contain", display: engine === "dplayer" ? "none" : "block" }}
          playsInline
        />
        <div ref={dpContainerRef} style={{ position: "absolute", inset: 0, display: engine === "dplayer" ? "block" : "none", background: "#000" }} />

        {/* ── Loading spinner ── */}
        {(status === "loading" || buffering) && status !== "error" && (
          <div style={{ position: "absolute", inset: 0, display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", background: "rgba(0,0,0,0.45)", gap: 10, pointerEvents: "none" }}>
            <div style={{ width: 48, height: 48, borderRadius: "50%", border: "3px solid rgba(255,255,255,0.15)", borderTop: "3px solid #fff", animation: "spin 0.75s linear infinite" }} />
            {autoRecovering && <span style={{ color: "#facc15", fontSize: 11, fontWeight: 700, background: "rgba(0,0,0,0.6)", padding: "2px 10px", borderRadius: 20 }}>Recovering…</span>}
          </div>
        )}

        {/* ── Error ── */}
        {status === "error" && (
          <div style={{ position: "absolute", inset: 0, display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", background: "rgba(0,0,0,0.88)", gap: 14 }}>
            <div style={{ fontSize: 38 }}>⚠️</div>
            <p style={{ color: "#fff", fontSize: 13, fontWeight: 700, margin: 0 }}>Stream error</p>
            <div style={{ display: "flex", gap: 10 }}>
              <button onClick={e => { e.stopPropagation(); switchToNextEngine(); }} style={{ background: "#00d4e8", border: "none", borderRadius: 8, padding: "9px 20px", color: "#000", fontWeight: 800, fontSize: 13, cursor: "pointer" }}>Try Next</button>
              <button onClick={e => { e.stopPropagation(); loadStream(streamUrl, engine); }} style={{ background: "rgba(255,255,255,0.1)", border: "1px solid #444", borderRadius: 8, padding: "9px 18px", color: "#aaa", fontWeight: 700, fontSize: 13, cursor: "pointer" }}>Retry</button>
            </div>
          </div>
        )}

        {/* ══ CONTROLS OVERLAY ══ */}
        {!locked && !floating && (
          <div style={{
            position: "absolute", inset: 0,
            opacity: showControls ? 1 : 0,
            transition: "opacity 0.25s",
            pointerEvents: showControls ? "auto" : "none",
            background: "linear-gradient(to bottom, rgba(0,0,0,0.65) 0%, transparent 30%, transparent 60%, rgba(0,0,0,0.75) 100%)",
            display: "flex", flexDirection: "column", justifyContent: "space-between",
          }}>

            {/* ── TOP BAR: ← title | vol lock ── */}
            <div style={{ display: "flex", alignItems: "center", padding: "10px 12px", gap: 10 }}>
              <Btn onClick={e => { e.stopPropagation(); onClose(); }} style={{ flexShrink: 0 }}>
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><path d="M19 12H5M12 5l-7 7 7 7"/></svg>
              </Btn>
              <span style={{ flex: 1, color: "#fff", fontSize: 13, fontWeight: 700, overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                {match.homeTeam} vs {match.awayTeam}
              </span>
              <Btn onClick={e => { e.stopPropagation(); const v = videoRef.current; if (v) { v.muted = !v.muted; setMuted(v.muted); } }}>
                {muted
                  ? <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"/><line x1="23" y1="9" x2="17" y2="15"/><line x1="17" y1="9" x2="23" y2="15"/></svg>
                  : <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"/><path d="M19.07 4.93a10 10 0 0 1 0 14.14M15.54 8.46a5 5 0 0 1 0 7.07"/></svg>}
              </Btn>
              <Btn onClick={e => { e.stopPropagation(); setLocked(true); }}>
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
              </Btn>
            </div>

            {/* ── CENTER CONTROLS: ⛶ PiP ◀◀ ⏸ ▶▶ ⚙ ⛶ ── */}
            <div style={{ display: "flex", alignItems: "center", justifyContent: "center", gap: 18, padding: "0 12px" }}>
              {/* Expand corners */}
              <Btn onClick={e => { e.stopPropagation(); toggleFullscreen(); }}>
                {fullscreen
                  ? <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M8 3v3a2 2 0 0 1-2 2H3m18 0h-3a2 2 0 0 1-2-2V3m0 18v-3a2 2 0 0 1 2-2h3M3 16h3a2 2 0 0 1 2 2v3"/></svg>
                  : <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="15 3 21 3 21 9"/><polyline points="9 21 3 21 3 15"/><line x1="21" y1="3" x2="14" y2="10"/><line x1="3" y1="21" x2="10" y2="14"/></svg>}
              </Btn>
              {/* PiP */}
              <Btn onClick={e => { e.stopPropagation(); setFloating(f => !f); }}>
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><rect x="2" y="3" width="20" height="14" rx="2"/><rect x="12" y="10" width="9" height="6" rx="1" fill="currentColor" stroke="none"/></svg>
              </Btn>
              {/* Rewind */}
              <Btn onClick={e => { e.stopPropagation(); seek(-10); }}>
                <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="1 4 1 10 7 10"/><path d="M3.51 15a9 9 0 1 0 .49-3.44"/></svg>
              </Btn>
              {/* Play/Pause */}
              <Btn onClick={e => { e.stopPropagation(); togglePlay(); }} style={{ width: 54, height: 54, borderRadius: "50%", background: "rgba(255,255,255,0.18)", border: "2px solid rgba(255,255,255,0.55)", flexShrink: 0 }}>
                {playing
                  ? <svg width="22" height="22" viewBox="0 0 24 24" fill="currentColor"><rect x="6" y="4" width="4" height="16" rx="1"/><rect x="14" y="4" width="4" height="16" rx="1"/></svg>
                  : <svg width="22" height="22" viewBox="0 0 24 24" fill="currentColor"><polygon points="5,3 19,12 5,21"/></svg>}
              </Btn>
              {/* Forward */}
              <Btn onClick={e => { e.stopPropagation(); seek(10); }}>
                <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="23 4 23 10 17 10"/><path d="M20.49 15a9 9 0 1 1-.49-3.44"/></svg>
              </Btn>
              {/* Settings / engine */}
              <Btn onClick={e => { e.stopPropagation(); setShowEngine(true); }}>
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>
              </Btn>
              {/* Quality */}
              <Btn onClick={e => { e.stopPropagation(); setShowQuality(true); }} style={{ background: "rgba(0,0,0,0.45)", borderRadius: 6, padding: "4px 9px", fontSize: 11, fontWeight: 800, color: "#fff" }}>
                {currentLevel === -1 ? "AUTO" : levels[currentLevel]?.height ? `${levels[currentLevel].height}p` : "HD"}
              </Btn>
            </div>

            {/* ── BOTTOM: seek bar ── */}
            <div style={{ padding: "6px 14px 12px" }}>
              <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                <span style={{ fontSize: 11, color: "rgba(255,255,255,0.85)", fontWeight: 700, flexShrink: 0, fontFamily: "monospace", minWidth: 50 }}>{fmtTime(currentTime)}</span>
                <div
                  style={{ flex: 1, height: 3, background: "rgba(255,255,255,0.25)", borderRadius: 2, cursor: "pointer", position: "relative" }}
                  onClick={e => { const v = videoRef.current; if (!v) return; const r = e.currentTarget.getBoundingClientRect(); v.currentTime = ((e.clientX - r.left) / r.width) * (v.duration || 0); }}
                >
                  <div style={{ width: `${progress}%`, height: "100%", background: "#fff", borderRadius: 2 }} />
                  <div style={{ position: "absolute", top: "50%", left: `${progress}%`, transform: "translate(-50%,-50%)", width: 11, height: 11, borderRadius: "50%", background: "#fff" }} />
                </div>
                <span style={{ fontSize: 11, color: "rgba(255,255,255,0.85)", fontWeight: 700, flexShrink: 0, fontFamily: "monospace", minWidth: 50, textAlign: "right" }}>{fmtTime(duration)}</span>
              </div>
            </div>
          </div>
        )}

        {/* 🔒 LOCKED overlay */}
        {locked && !floating && (
          <div style={{ position: "absolute", inset: 0, display: "flex", alignItems: "center", justifyContent: "center", background: "rgba(0,0,0,0.25)" }}
            onClick={e => e.stopPropagation()}>
            <div style={{ background: "rgba(0,0,0,0.75)", backdropFilter: "blur(6px)", borderRadius: 14, padding: "16px 24px", display: "flex", flexDirection: "column", alignItems: "center", gap: 10 }}>
              <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#00d4e8" strokeWidth="2"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
              <span style={{ color: "#fff", fontSize: 12, fontWeight: 700 }}>Controls Locked</span>
              <button onClick={() => setLocked(false)} style={{ background: "#00d4e8", border: "none", borderRadius: 20, padding: "6px 18px", color: "#000", fontWeight: 800, fontSize: 12, cursor: "pointer" }}>Unlock</button>
            </div>
          </div>
        )}

        {/* Modals */}
        {showQuality && <QualityModal levels={levels} currentLevel={currentLevel} onSelect={setQuality} onClose={() => setShowQuality(false)} />}
        {showEngine  && <EngineModal current={engine} onSelect={e => { setEngine(e); setShowEngine(false); }} onClose={() => setShowEngine(false)} />}
      </div>

      {/* PiP close */}
      {floating && (
        <button onClick={() => setFloating(false)} style={{ position: "absolute", top: 4, right: 4, background: "rgba(0,0,0,0.7)", border: "none", borderRadius: "50%", width: 20, height: 20, cursor: "pointer", color: "#fff", fontSize: 11, zIndex: 10, display:"flex", alignItems:"center", justifyContent:"center" }}>✕</button>
      )}

      {/* ══ BELOW VIDEO (non-floating) ══ */}
      {!floating && (
        <>
          {/* ── BUF bar (thin) ── */}
          <div style={{ background: "#06080f", padding: "5px 14px", display: "flex", alignItems: "center", gap: 8, borderBottom: "1px solid #111827" }}>
            <span style={{ color: "#374151", fontSize: 9, fontWeight: 800, letterSpacing: 1, flexShrink: 0 }}>BUF</span>
            <div style={{ flex: 1, height: 3, background: "#111827", borderRadius: 2, overflow: "hidden" }}>
              <div style={{ width: `${bufferPct}%`, height: "100%", background: bufferPct > 60 ? "#22c55e" : bufferPct > 30 ? "#eab308" : "#ef4444", borderRadius: 2, transition: "width 0.5s" }} />
            </div>
            <span style={{ color: "#6b7280", fontSize: 9, fontWeight: 800, flexShrink: 0 }}>{bufferPct}%</span>
            {stallCount > 0 && <span style={{ color: "#facc15", fontSize: 9, fontWeight: 700 }}>⚠ {stallCount}</span>}
          </div>

          {/* ── Stream chips — horizontal scroll ── */}
          <div style={{ background: "#06080f", padding: "10px 12px 10px", display: "flex", overflowX: "auto", gap: 8, scrollbarWidth: "none", borderBottom: "1px solid #111827" }}>
            {DEMO_STREAMS.map(s => {
              const active = streamName === s.name;
              return (
                <button
                  key={s.name}
                  onClick={() => { setStreamUrl(s.url); setStreamName(s.name); }}
                  style={{
                    flexShrink: 0, padding: "7px 18px", borderRadius: 999,
                    border: `1.5px solid ${active ? "#fff" : "#1f2937"}`,
                    background: active ? "#fff" : "#111827",
                    color: active ? "#000" : "#6b7280",
                    cursor: "pointer", fontSize: 12, fontWeight: 700, whiteSpace: "nowrap",
                    display: "flex", alignItems: "center", gap: 6,
                    transition: "all 0.15s",
                  }}
                >
                  {active && (
                    <svg width="10" height="10" viewBox="0 0 24 24" fill="currentColor" style={{ flexShrink: 0 }}>
                      <polygon points="5,3 19,12 5,21"/>
                    </svg>
                  )}
                  {s.name}
                </button>
              );
            })}
          </div>

          {/* ── Match info card ── */}
          <div style={{ background: "#0a0d16", margin: "14px 14px 0", borderRadius: 14, border: "1px solid #1f2937", padding: "16px 16px" }}>
            <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
              {/* Home */}
              <div style={{ flex: 1, display: "flex", flexDirection: "column", alignItems: "center", gap: 8 }}>
                <TeamLogo src={match.homeFlag} name={match.homeTeam} size={44} />
                <span style={{ fontSize: 13, fontWeight: 700, color: "#e5e7eb", textAlign: "center" }}>{match.homeTeam}</span>
              </div>
              {/* VS center */}
              <div style={{ flexShrink: 0, textAlign: "center", padding: "0 16px" }}>
                {match.status === "live" ? (
                  <>
                    <div style={{ display: "flex", alignItems: "center", justifyContent: "center", gap: 5, marginBottom: 2 }}>
                      <div style={{ width: 7, height: 7, borderRadius: "50%", background: "#ef4444", animation: "pulse 1.5s ease-in-out infinite" }} />
                      <span style={{ fontSize: 10, color: "#ef4444", fontWeight: 800, letterSpacing: 1 }}>LIVE</span>
                    </div>
                    <span style={{ fontSize: 13, fontWeight: 700, color: "#fff", fontFamily: "monospace" }}>{match.time}</span>
                  </>
                ) : (
                  <span style={{ fontSize: 20, color: "#374151", fontWeight: 700 }}>VS</span>
                )}
              </div>
              {/* Away */}
              <div style={{ flex: 1, display: "flex", flexDirection: "column", alignItems: "center", gap: 8 }}>
                <TeamLogo src={match.awayFlag} name={match.awayTeam} size={44} />
                <span style={{ fontSize: 13, fontWeight: 700, color: "#e5e7eb", textAlign: "center" }}>{match.awayTeam}</span>
              </div>
            </div>
          </div>

          {/* ── Engine tabs (compact, below match card) ── */}
          <div style={{ display: "flex", overflowX: "auto", gap: 6, padding: "12px 14px", background: "#0a0d16", scrollbarWidth: "none" }}>
            {ENGINES.map(eng => (
              <button key={eng.id} onClick={() => setEngine(eng.id)} style={{
                flexShrink: 0, padding: "5px 13px", borderRadius: 999,
                background: engine === eng.id ? `${eng.color}18` : "transparent",
                border: `1.5px solid ${engine === eng.id ? eng.color : "#1f2937"}`,
                color: engine === eng.id ? eng.color : "#4b5563",
                cursor: "pointer", fontSize: 11, fontWeight: 700,
                display: "flex", alignItems: "center", gap: 5,
              }}>
                <span style={{ fontSize: 10 }}>{eng.icon}</span>
                <span>{eng.label}</span>
                {engine === eng.id && status === "ready" && <span style={{ width: 5, height: 5, borderRadius: "50%", background: eng.color }} />}
              </button>
            ))}
          </div>
        </>
      )}

      <style>{`
        @keyframes spin    { to { transform: rotate(360deg); } }
        @keyframes pulse   { 0%,100%{opacity:1} 50%{opacity:.35} }
        @keyframes slideUp { from{transform:translateY(100%)} to{transform:translateY(0)} }
        @keyframes fadeIn  { from{opacity:0;transform:scale(0.95)} to{opacity:1;transform:scale(1)} }
        * { -webkit-tap-highlight-color: transparent; }
        ::-webkit-scrollbar { display: none; }
      `}</style>
    </div>
  );
}
