import { useEffect, useState } from "react";

interface Props {
  onDone: () => void;
}

export default function SplashScreen({ onDone }: Props) {
  const [show, setShow] = useState(true);

  useEffect(() => {
    const t = setTimeout(() => {
      setShow(false);
      setTimeout(onDone, 300);
    }, 1200);
    return () => clearTimeout(t);
  }, [onDone]);

  return (
    <div
      className="splash"
      style={{
        opacity: show ? 1 : 0,
        transition: "opacity 0.4s ease",
        pointerEvents: show ? "auto" : "none",
      }}
    >
      <div className="splash-logo-wrapper">
        <svg width="72" height="72" viewBox="0 0 72 72" fill="none">
          <path
            d="M52 14C52 14 42 16 36 24C30 32 32 42 24 48C16 54 14 58 14 58"
            stroke="url(#sg)" strokeWidth="18" strokeLinecap="round"
          />
          <defs>
            <linearGradient id="sg" x1="14" y1="58" x2="52" y2="14" gradientUnits="userSpaceOnUse">
              <stop stopColor="#00d4e8" />
              <stop offset="0.5" stopColor="#00a8c4" />
              <stop offset="1" stopColor="#e53e3e" />
            </linearGradient>
          </defs>
        </svg>
      </div>

      <div className="splash-title">Sportzfy</div>

      <div className="splash-smile" style={{ bottom: 100 }}>
        <svg width="36" height="20" viewBox="0 0 36 20" fill="none">
          <circle cx="8" cy="8" r="5" fill="#00d4e8" />
          <circle cx="28" cy="8" r="5" fill="#00d4e8" />
          <path d="M4 12 Q18 24 32 12" stroke="#00d4e8" strokeWidth="2.5" fill="none" strokeLinecap="round" />
        </svg>
      </div>

      <div className="splash-version">Version: 6.0</div>
    </div>
  );
}
