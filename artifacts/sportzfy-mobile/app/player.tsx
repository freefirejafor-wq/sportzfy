import React, { useEffect, useRef, useState, useCallback } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  StatusBar,
  Animated,
  Dimensions,
  ActivityIndicator,
  BackHandler,
} from 'react-native';
import { useVideoPlayer, VideoView } from 'expo-video';
import * as ScreenOrientation from 'expo-screen-orientation';
import { useRouter } from 'expo-router';
import { usePlayer } from '../context/PlayerContext';
import { Colors } from '../constants/colors';
import StreamPickerModal from '../components/StreamPickerModal';
import { type Stream } from '../data/streams';

const { width: SCREEN_W, height: SCREEN_H } = Dimensions.get('window');

export default function PlayerScreen() {
  const router = useRouter();
  const { player, setFloating, closePlayer, playStream } = usePlayer();

  const [isPlaying, setIsPlaying] = useState(false);
  const [isBuffering, setIsBuffering] = useState(true);
  const [status, setStatus] = useState<'loading' | 'ready' | 'error'>('loading');
  const [showControls, setShowControls] = useState(true);
  const [isLocked, setIsLocked] = useState(false);
  const [showQuality, setShowQuality] = useState(false);
  const [showStreamPicker, setShowStreamPicker] = useState(false);
  const [currentStream, setCurrentStream] = useState(player.streamName);
  const [currentUrl, setCurrentUrl] = useState(player.streamUrl);

  const controlsOpacity = useRef(new Animated.Value(1)).current;
  const controlTimer = useRef<ReturnType<typeof setTimeout> | null>(null);

  // expo-video player (uses ExoPlayer on Android — full HLS/DASH support)
  const videoPlayer = useVideoPlayer(currentUrl, p => {
    p.loop = false;
    p.play();
  });

  // Lock to landscape on mount
  useEffect(() => {
    StatusBar.setHidden(true);
    ScreenOrientation.lockAsync(ScreenOrientation.OrientationLock.LANDSCAPE_LEFT);

    return () => {
      StatusBar.setHidden(false);
      ScreenOrientation.unlockAsync();
    };
  }, []);

  // Back button → minimize to floating
  useEffect(() => {
    const sub = BackHandler.addEventListener('hardwareBackPress', () => {
      handleMinimize();
      return true;
    });
    return () => sub.remove();
  }, []);

  // Video player status
  useEffect(() => {
    const sub = videoPlayer.addListener('statusChange', ({ status: s }) => {
      if (s === 'readyToPlay') {
        setIsBuffering(false);
        setStatus('ready');
      } else if (s === 'loading') {
        setIsBuffering(true);
      } else if (s === 'error') {
        setStatus('error');
        setIsBuffering(false);
      }
    });
    const playSub = videoPlayer.addListener('playingChange', ({ isPlaying: p }) => {
      setIsPlaying(p);
    });
    return () => { sub.remove(); playSub.remove(); };
  }, [videoPlayer]);

  // Auto-hide controls
  const resetControlsTimer = useCallback(() => {
    if (isLocked) return;
    setShowControls(true);
    Animated.timing(controlsOpacity, { toValue: 1, duration: 150, useNativeDriver: true }).start();
    if (controlTimer.current) clearTimeout(controlTimer.current);
    controlTimer.current = setTimeout(() => {
      Animated.timing(controlsOpacity, { toValue: 0, duration: 400, useNativeDriver: true }).start(() =>
        setShowControls(false));
    }, 4000);
  }, [isLocked]);

  useEffect(() => {
    resetControlsTimer();
    return () => { if (controlTimer.current) clearTimeout(controlTimer.current); };
  }, []);

  const handleTap = () => {
    if (isLocked) {
      // Only show lock button when locked
      Animated.sequence([
        Animated.timing(controlsOpacity, { toValue: 1, duration: 150, useNativeDriver: true }),
        Animated.delay(2000),
        Animated.timing(controlsOpacity, { toValue: 0, duration: 400, useNativeDriver: true }),
      ]).start();
      return;
    }
    if (showControls) {
      Animated.timing(controlsOpacity, { toValue: 0, duration: 300, useNativeDriver: true }).start(() =>
        setShowControls(false));
      if (controlTimer.current) clearTimeout(controlTimer.current);
    } else {
      resetControlsTimer();
    }
  };

  const handleMinimize = () => {
    setFloating(true);
    router.back();
  };

  const handleClose = () => {
    closePlayer();
    router.back();
  };

  const togglePlayPause = () => {
    if (isPlaying) videoPlayer.pause();
    else videoPlayer.play();
    resetControlsTimer();
  };

  const handleStreamSelect = (stream: Stream) => {
    setCurrentStream(stream.name);
    setCurrentUrl(stream.url);
    playStream(stream.url, stream.name, player.matchTitle);
    videoPlayer.replace(stream.url);
    setStatus('loading');
    setIsBuffering(true);
    resetControlsTimer();
  };

  return (
    <View style={styles.container}>
      <StatusBar hidden />

      {/* Video */}
      <TouchableOpacity
        style={StyleSheet.absoluteFill}
        onPress={handleTap}
        activeOpacity={1}
      >
        <VideoView
          player={videoPlayer}
          style={StyleSheet.absoluteFill}
          contentFit="contain"
          nativeControls={false}
        />
      </TouchableOpacity>

      {/* Buffering */}
      {isBuffering && (
        <View style={styles.bufferingOverlay}>
          <ActivityIndicator size="large" color={Colors.accent} />
          <Text style={styles.bufferingText}>
            {status === 'error' ? 'Stream error — try another' : 'Loading stream…'}
          </Text>
        </View>
      )}

      {/* Controls overlay */}
      {(showControls || isLocked) && (
        <Animated.View style={[styles.controlsOverlay, { opacity: controlsOpacity }]}>
          {/* Lock button (always on right when locked) */}
          <TouchableOpacity
            style={[styles.lockBtn, isLocked && styles.lockBtnActive]}
            onPress={() => { setIsLocked(l => !l); resetControlsTimer(); }}
          >
            <Text style={styles.lockIcon}>{isLocked ? '🔒' : '🔓'}</Text>
          </TouchableOpacity>

          {!isLocked && (
            <>
              {/* Top bar */}
              <View style={styles.topBar}>
                <TouchableOpacity style={styles.topBtn} onPress={handleMinimize}>
                  <Text style={styles.topBtnIcon}>⬇</Text>
                </TouchableOpacity>

                <View style={styles.topCenter}>
                  <Text style={styles.matchTitle} numberOfLines={1}>
                    {player.matchTitle}
                  </Text>
                  <Text style={styles.streamName} numberOfLines={1}>
                    📡 {currentStream}
                  </Text>
                </View>

                <TouchableOpacity style={styles.topBtn} onPress={handleClose}>
                  <Text style={styles.topBtnIcon}>✕</Text>
                </TouchableOpacity>
              </View>

              {/* Center play/pause */}
              <TouchableOpacity style={styles.playBtn} onPress={togglePlayPause}>
                <Text style={styles.playBtnIcon}>{isPlaying ? '⏸' : '▶'}</Text>
              </TouchableOpacity>

              {/* Bottom bar */}
              <View style={styles.bottomBar}>
                {/* Stream picker */}
                <TouchableOpacity
                  style={styles.bottomBtn}
                  onPress={() => setShowStreamPicker(true)}
                >
                  <Text style={styles.bottomBtnIcon}>📡</Text>
                  <Text style={styles.bottomBtnLabel}>Streams</Text>
                </TouchableOpacity>

                {/* Quality */}
                <TouchableOpacity
                  style={styles.bottomBtn}
                  onPress={() => setShowQuality(q => !q)}
                >
                  <Text style={styles.bottomBtnIcon}>🎯</Text>
                  <Text style={styles.bottomBtnLabel}>Quality</Text>
                </TouchableOpacity>

                {/* Floating */}
                <TouchableOpacity style={styles.bottomBtn} onPress={handleMinimize}>
                  <Text style={styles.bottomBtnIcon}>📌</Text>
                  <Text style={styles.bottomBtnLabel}>Mini</Text>
                </TouchableOpacity>

                {/* Lock */}
                <TouchableOpacity
                  style={styles.bottomBtn}
                  onPress={() => setIsLocked(true)}
                >
                  <Text style={styles.bottomBtnIcon}>🔒</Text>
                  <Text style={styles.bottomBtnLabel}>Lock</Text>
                </TouchableOpacity>
              </View>
            </>
          )}
        </Animated.View>
      )}

      {/* Quality info overlay */}
      {showQuality && !isLocked && (
        <View style={styles.qualityOverlay}>
          <Text style={styles.qualityTitle}>🎯 Quality</Text>
          <Text style={styles.qualityNote}>
            ExoPlayer automatically selects the best quality based on your network speed.
          </Text>
          {['FHD 1080p', 'HD 720p', 'SD 480p', 'Auto (Recommended)'].map(q => (
            <TouchableOpacity
              key={q}
              style={styles.qualityRow}
              onPress={() => setShowQuality(false)}
            >
              <Text style={styles.qualityItem}>{q.includes('Auto') ? '✅ ' : '   '}{q}</Text>
            </TouchableOpacity>
          ))}
          <TouchableOpacity onPress={() => setShowQuality(false)} style={styles.qualityClose}>
            <Text style={styles.qualityCloseText}>Close</Text>
          </TouchableOpacity>
        </View>
      )}

      {/* Stream Picker */}
      <StreamPickerModal
        visible={showStreamPicker}
        event={null}
        onSelect={handleStreamSelect}
        onClose={() => setShowStreamPicker(false)}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.playerBg,
  },
  bufferingOverlay: {
    ...StyleSheet.absoluteFillObject,
    alignItems: 'center',
    justifyContent: 'center',
    gap: 12,
    backgroundColor: 'rgba(0,0,0,0.5)',
  },
  bufferingText: {
    color: Colors.textMuted,
    fontSize: 13,
  },
  controlsOverlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(0,0,0,0.4)',
  },
  lockBtn: {
    position: 'absolute',
    top: 20,
    right: 20,
    width: 44,
    height: 44,
    borderRadius: 22,
    backgroundColor: 'rgba(0,0,0,0.6)',
    borderWidth: 1,
    borderColor: Colors.border,
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 10,
  },
  lockBtnActive: {
    borderColor: Colors.accent,
    backgroundColor: Colors.accentGlow,
  },
  lockIcon: { fontSize: 18 },
  topBar: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingTop: 16,
    paddingBottom: 16,
    gap: 12,
    backgroundColor: 'rgba(0,0,0,0.6)',
  },
  topBtn: {
    width: 38,
    height: 38,
    borderRadius: 10,
    backgroundColor: 'rgba(255,255,255,0.1)',
    alignItems: 'center',
    justifyContent: 'center',
  },
  topBtnIcon: { fontSize: 16, color: Colors.text },
  topCenter: { flex: 1, alignItems: 'center' },
  matchTitle: {
    fontSize: 14,
    fontWeight: '800',
    color: Colors.text,
    textAlign: 'center',
  },
  streamName: {
    fontSize: 11,
    color: Colors.accent,
    textAlign: 'center',
    marginTop: 2,
  },
  playBtn: {
    position: 'absolute',
    top: '50%',
    left: '50%',
    marginTop: -34,
    marginLeft: -34,
    width: 68,
    height: 68,
    borderRadius: 34,
    backgroundColor: 'rgba(0,212,232,0.2)',
    borderWidth: 2,
    borderColor: Colors.accent,
    alignItems: 'center',
    justifyContent: 'center',
  },
  playBtnIcon: { fontSize: 28, color: Colors.text },
  bottomBar: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-around',
    paddingVertical: 14,
    paddingHorizontal: 20,
    backgroundColor: 'rgba(0,0,0,0.7)',
    gap: 8,
  },
  bottomBtn: {
    alignItems: 'center',
    gap: 4,
    flex: 1,
  },
  bottomBtnIcon: { fontSize: 20 },
  bottomBtnLabel: {
    fontSize: 10,
    fontWeight: '700',
    color: Colors.textMuted,
  },
  qualityOverlay: {
    position: 'absolute',
    bottom: 70,
    left: 20,
    right: 20,
    backgroundColor: Colors.surface,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: Colors.accent,
    padding: 16,
    gap: 8,
  },
  qualityTitle: {
    fontSize: 15,
    fontWeight: '800',
    color: Colors.text,
    marginBottom: 4,
  },
  qualityNote: {
    fontSize: 11,
    color: Colors.textMuted,
    marginBottom: 8,
  },
  qualityRow: {
    paddingVertical: 8,
    borderBottomWidth: 1,
    borderBottomColor: Colors.border,
  },
  qualityItem: {
    fontSize: 14,
    color: Colors.text,
  },
  qualityClose: {
    alignItems: 'center',
    paddingTop: 8,
  },
  qualityCloseText: {
    color: Colors.accent,
    fontWeight: '700',
    fontSize: 14,
  },
});
