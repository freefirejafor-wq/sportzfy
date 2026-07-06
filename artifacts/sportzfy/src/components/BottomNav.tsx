interface Props {
  active: "sports" | "live" | "categories";
  onNavigate: (tab: "sports" | "live" | "categories") => void;
}

export default function BottomNav({ active, onNavigate }: Props) {
  return (
    <div className="bottom-nav">
      <div
        className={`nav-item ${active === "sports" ? "active" : ""}`}
        onClick={() => onNavigate("sports")}
      >
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <circle cx="12" cy="12" r="10" />
          <path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z" />
          <path d="M2 12h20" />
        </svg>
        <span>Sports</span>
      </div>

      <div
        className={`nav-item ${active === "live" ? "active" : ""}`}
        onClick={() => onNavigate("live")}
      >
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <circle cx="12" cy="12" r="2" fill="currentColor" stroke="none" />
          <path d="M16.24 7.76a6 6 0 0 1 0 8.49" />
          <path d="M7.76 7.76a6 6 0 0 0 0 8.49" />
          <path d="M20.07 4.93a10 10 0 0 1 0 14.14" />
          <path d="M3.93 4.93a10 10 0 0 0 0 14.14" />
        </svg>
        <span>Live Events</span>
      </div>

      <div
        className={`nav-item ${active === "categories" ? "active" : ""}`}
        onClick={() => onNavigate("categories")}
      >
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <rect x="2" y="3" width="9" height="9" rx="1" />
          <rect x="13" y="3" width="9" height="9" rx="1" />
          <rect x="2" y="14" width="9" height="7" rx="1" />
          <rect x="13" y="14" width="9" height="7" rx="1" />
        </svg>
        <span>Categories</span>
      </div>
    </div>
  );
}
