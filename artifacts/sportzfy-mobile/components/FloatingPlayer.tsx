import React, { useRef, useEffect } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Animated,
  PanResponder,
  Dimensions,
} from 'react-native';
import { usePlayer } from '../context/PlayerContext';
import { Colors } from '../constants/colors';
import { useRouter } from 'expo-router';

const { width: SCREEN_W, height: SCREEN_H } = Dimensions.get('window');
const PLAYER_W = 200;
const PLAYER_H = 115;
const PADDING = 12;

export default function FloatingPlayer() {
  const { player, closePlayer, setFloating } = usePlayer();
  const router = useRouter();

  const pan = useRef(new Animated.ValueXY({
    x: SCREEN_W - PLAYER_W - PADDING,
    y: SCREEN_H - PLAYER_H - 80,
  })).current;

  const scale = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    if (player.isFloating) {
      Animated.spring(scale, {
        toValue: 1,
        tension: 70,
        friction: 10,
        useNativeDriver: true,
      }).start();
    } else {
      Animated.timing(scale, {
        toValue: 0,
        duration: 150,
        useNativeDriver: true,
      }).start();
    }
  }, [player.isFloating]);

  const panResponder = useRef(
    PanResponder.create({
      // Only claim gesture on actual movement (>5px), so child buttons still work
      onStartShouldSetPanResponder: () => false,
      onMoveShouldSetPanResponder: (_, gs) =>
        Math.abs(gs.dx) > 5 || Math.abs(gs.dy) > 5,
      onPanResponderGrant: () => {
        pan.setOffset({ x: (pan.x as any)._value, y: (pan.y as any)._value });
        pan.setValue({ x: 0, y: 0 });
      },
      onPanResponderMove: Animated.event([null, { dx: pan.x, dy: pan.y }], {
        useNativeDriver: false,
      }),
      onPanResponderRelease: (_, { moveX, moveY }) => {
        pan.flattenOffset();
        // Snap to nearest edge
        const newX = moveX < SCREEN_W / 2
          ? PADDING
          : SCREEN_W - PLAYER_W - PADDING;
        const clampedY = Math.max(
          60,
          Math.min(moveY - PLAYER_H / 2, SCREEN_H - PLAYER_H - 80),
        );
        Animated.spring(pan, {
          toValue: { x: newX, y: clampedY },
          tension: 60,
          friction: 10,
          useNativeDriver: false,
        }).start();
      },
    }),
  ).current;

  if (!player.isActive || !player.isFloating) return null;

  return (
    <Animated.View
      style={[
        styles.container,
        { transform: [{ translateX: pan.x }, { translateY: pan.y }, { scale }] },
      ]}
      {...panResponder.panHandlers}
    >
      {/* Video placeholder (real player screen handles actual video) */}
      <View style={styles.videoArea}>
        <Text style={styles.playIcon}>▶</Text>
        <Text style={styles.streamName} numberOfLines={1}>
          {player.streamName}
        </Text>
      </View>

      {/* Controls */}
      <View style={styles.controls}>
        <Text style={styles.matchTitle} numberOfLines={1}>
          {player.matchTitle}
        </Text>
        <View style={styles.btnRow}>
          {/* Expand back to fullscreen */}
          <TouchableOpacity
            style={styles.iconBtn}
            onPress={() => {
              setFloating(false);
              router.push('/player');
            }}
          >
            <Text style={styles.iconBtnText}>⤢</Text>
          </TouchableOpacity>
          {/* Close */}
          <TouchableOpacity style={styles.iconBtn} onPress={closePlayer}>
            <Text style={styles.iconBtnText}>✕</Text>
          </TouchableOpacity>
        </View>
      </View>

      {/* Live dot */}
      <View style={styles.liveDot} />
    </Animated.View>
  );
}

const styles = StyleSheet.create({
  container: {
    position: 'absolute',
    width: PLAYER_W,
    height: PLAYER_H,
    backgroundColor: Colors.playerBg,
    borderRadius: 12,
    borderWidth: 2,
    borderColor: Colors.accent,
    overflow: 'hidden',
    elevation: 20,
    shadowColor: Colors.accent,
    shadowOffset: { width: 0, height: 0 },
    shadowOpacity: 0.5,
    shadowRadius: 12,
    zIndex: 9000,
  },
  videoArea: {
    flex: 1,
    backgroundColor: '#050a14',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 4,
  },
  playIcon: {
    fontSize: 24,
    color: Colors.accent,
  },
  streamName: {
    fontSize: 10,
    color: Colors.textMuted,
    paddingHorizontal: 8,
    textAlign: 'center',
  },
  controls: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: Colors.surface,
    paddingHorizontal: 8,
    paddingVertical: 5,
  },
  matchTitle: {
    flex: 1,
    fontSize: 9,
    color: Colors.text,
    fontWeight: '700',
  },
  btnRow: {
    flexDirection: 'row',
    gap: 4,
  },
  iconBtn: {
    width: 22,
    height: 22,
    borderRadius: 6,
    backgroundColor: Colors.card,
    alignItems: 'center',
    justifyContent: 'center',
  },
  iconBtnText: {
    fontSize: 11,
    color: Colors.accent,
  },
  liveDot: {
    position: 'absolute',
    top: 7,
    left: 7,
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: Colors.live,
  },
});
