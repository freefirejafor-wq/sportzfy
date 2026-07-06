import { useState } from "react";
import SplashScreen from "./components/SplashScreen";
import TopBar from "./components/TopBar";
import SideMenu from "./components/SideMenu";
import StreamPicker from "./components/StreamPicker";
import VideoPlayer from "./components/VideoPlayer";
import LiveEvents from "./pages/LiveEvents";
import type { Match } from "./data/matches";

function App() {
  const [splashDone, setSplashDone] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);
  const [pickingMatch, setPickingMatch] = useState<Match | null>(null);
  const [watchingMatch, setWatchingMatch] = useState<{ match: Match; streamUrl: string; streamName: string } | null>(null);
  const [searchQuery, setSearchQuery] = useState("");

  if (watchingMatch) {
    return (
      <VideoPlayer
        match={watchingMatch.match}
        streamUrl={watchingMatch.streamUrl}
        streamName={watchingMatch.streamName}
        onClose={() => setWatchingMatch(null)}
      />
    );
  }

  return (
    <>
      {!splashDone && <SplashScreen onDone={() => setSplashDone(true)} />}

      {pickingMatch && (
        <StreamPicker
          match={pickingMatch}
          onSelect={(url, name) => {
            setWatchingMatch({ match: pickingMatch, streamUrl: url, streamName: name });
            setPickingMatch(null);
          }}
          onClose={() => setPickingMatch(null)}
        />
      )}

      <div style={{ minHeight: "100vh", display: "flex", flexDirection: "column" }}>
        <TopBar
          title="Sportzfy"
          onMenuOpen={() => setMenuOpen(true)}
          searchQuery={searchQuery}
          onSearchChange={setSearchQuery}
        />
        <SideMenu open={menuOpen} onClose={() => setMenuOpen(false)} />

        <div style={{ flex: 1 }}>
          <LiveEvents onWatch={setPickingMatch} searchQuery={searchQuery} />
        </div>
      </div>
    </>
  );
}

export default App;
