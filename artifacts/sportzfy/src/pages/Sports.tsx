import { sportChannels } from "../data/channels";

function ChannelLogo({ src, name }: { src: string; name: string }) {
  return (
    <img
      src={src}
      alt={name}
      style={{ width: 58, height: 58, borderRadius: "50%", objectFit: "contain", background: "white" }}
      onError={(e) => {
        const t = e.currentTarget;
        t.style.display = "none";
        const parent = t.parentElement;
        if (parent) {
          const fallback = document.createElement("div");
          fallback.style.cssText =
            "width:58px;height:58px;border-radius:50%;background:#333;display:flex;align-items:center;justify-content:center;font-size:18px;font-weight:700;color:#00d4e8;";
          fallback.textContent = name.substring(0, 2).toUpperCase();
          parent.insertBefore(fallback, t);
        }
      }}
    />
  );
}

export default function Sports() {
  return (
    <div style={{ padding: "12px 10px 90px" }}>
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(4, 1fr)",
          gap: 8,
        }}
      >
        {sportChannels.map((ch) => (
          <div key={ch.id} className="channel-card">
            <div
              style={{
                width: 58, height: 58, borderRadius: "50%",
                background: "white",
                display: "flex", alignItems: "center", justifyContent: "center",
                overflow: "hidden", flexShrink: 0,
              }}
            >
              <img
                src={ch.logo}
                alt={ch.name}
                style={{ width: "100%", height: "100%", objectFit: "contain" }}
                onError={(e) => {
                  const t = e.currentTarget;
                  t.parentElement!.style.background = "#1a2540";
                  t.parentElement!.innerHTML = `<span style="font-size:11px;font-weight:800;color:#00d4e8;text-align:center;padding:4px">${ch.name.substring(0, 4)}</span>`;
                }}
              />
            </div>
            <span className="ch-name">{ch.name}</span>
          </div>
        ))}
      </div>
    </div>
  );
}
