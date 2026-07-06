import { categories } from "../data/channels";

export default function Categories() {
  return (
    <div style={{ padding: "12px 10px 90px" }}>
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(2, 1fr)",
          gap: 8,
        }}
      >
        {categories.map((cat) => (
          <div key={cat.id} className="category-row">
            <div
              style={{
                width: 38, height: 38, borderRadius: "50%",
                background: "white",
                display: "flex", alignItems: "center", justifyContent: "center",
                overflow: "hidden", flexShrink: 0,
              }}
            >
              <img
                src={cat.logo}
                alt={cat.name}
                style={{ width: "100%", height: "100%", objectFit: "contain" }}
                onError={(e) => {
                  const t = e.currentTarget;
                  t.parentElement!.style.background = "#1a2540";
                  t.parentElement!.innerHTML = `<span style="font-size:10px;font-weight:800;color:#00d4e8;text-align:center">${cat.name.substring(0, 3)}</span>`;
                }}
              />
            </div>
            <span className="cat-name">{cat.name}</span>
          </div>
        ))}
      </div>
    </div>
  );
}
