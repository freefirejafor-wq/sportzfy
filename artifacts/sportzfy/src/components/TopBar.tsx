import { useState } from "react";
import { Menu, Star, RefreshCw, Search, X } from "lucide-react";

interface Props {
  title: string;
  onMenuOpen: () => void;
  searchQuery: string;
  onSearchChange: (q: string) => void;
}

export default function TopBar({ title, onMenuOpen, searchQuery, onSearchChange }: Props) {
  const [searching, setSearching] = useState(false);

  const openSearch = () => setSearching(true);
  const closeSearch = () => { setSearching(false); onSearchChange(""); };

  if (searching) {
    return (
      <div className="top-bar" style={{ gap: 8 }}>
        <button onClick={closeSearch} style={{ background: "none", border: "none", cursor: "pointer", flexShrink: 0 }}>
          <X size={22} color="#ccc" />
        </button>
        <input
          autoFocus
          value={searchQuery}
          onChange={(e) => onSearchChange(e.target.value)}
          placeholder="Search teams, leagues…"
          style={{
            flex: 1, background: "rgba(255,255,255,0.07)", border: "1.5px solid #2a3040",
            borderRadius: 10, padding: "7px 12px", color: "#fff", fontSize: 14,
            outline: "none",
          }}
        />
        {searchQuery && (
          <button onClick={() => onSearchChange("")} style={{ background: "none", border: "none", cursor: "pointer", flexShrink: 0 }}>
            <X size={16} color="#666" />
          </button>
        )}
      </div>
    );
  }

  return (
    <div className="top-bar">
      <button onClick={onMenuOpen} style={{ background: "none", border: "none", cursor: "pointer" }}>
        <Menu size={24} color="#ccc" />
      </button>
      <span className="top-bar-title">{title}</span>
      <div className="top-bar-icons">
        <Star size={20} />
        <RefreshCw size={20} />
        <button onClick={openSearch} style={{ background: "none", border: "none", cursor: "pointer", color: "#ccc", padding: 0, display: "flex" }}>
          <Search size={20} />
        </button>
      </div>
    </div>
  );
}
