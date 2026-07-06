import React, {
  createContext,
  useContext,
  useState,
  useCallback,
  useRef,
} from 'react';

export interface PlayerState {
  isActive: boolean;
  streamUrl: string;
  streamName: string;
  matchTitle: string;
  isFloating: boolean;
}

interface PlayerContextType {
  player: PlayerState;
  playStream: (streamUrl: string, streamName: string, matchTitle: string) => void;
  setFloating: (floating: boolean) => void;
  closePlayer: () => void;
}

const DEFAULT: PlayerState = {
  isActive: false,
  streamUrl: '',
  streamName: '',
  matchTitle: '',
  isFloating: false,
};

const PlayerContext = createContext<PlayerContextType>({
  player: DEFAULT,
  playStream: () => {},
  setFloating: () => {},
  closePlayer: () => {},
});

export function PlayerProvider({ children }: { children: React.ReactNode }) {
  const [player, setPlayer] = useState<PlayerState>(DEFAULT);

  const playStream = useCallback(
    (streamUrl: string, streamName: string, matchTitle: string) => {
      setPlayer({
        isActive: true,
        streamUrl,
        streamName,
        matchTitle,
        isFloating: false,
      });
    },
    [],
  );

  const setFloating = useCallback((floating: boolean) => {
    setPlayer(prev => ({ ...prev, isFloating: floating }));
  }, []);

  const closePlayer = useCallback(() => {
    setPlayer(DEFAULT);
  }, []);

  return (
    <PlayerContext.Provider value={{ player, playStream, setFloating, closePlayer }}>
      {children}
    </PlayerContext.Provider>
  );
}

export function usePlayer() {
  return useContext(PlayerContext);
}
