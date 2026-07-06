import React, { useEffect, useRef } from 'react';
import { View, Text, StyleSheet, Animated, Dimensions } from 'react-native';
import { Colors } from '../constants/colors';

const { width, height } = Dimensions.get('window');

interface Props {
  onFinish: () => void;
}

export default function SplashScreen({ onFinish }: Props) {
  const opacity = useRef(new Animated.Value(0)).current;
  const scale = useRef(new Animated.Value(0.7)).current;
  const exitOpacity = useRef(new Animated.Value(1)).current;
  const glowAnim = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    // Enter animation
    Animated.parallel([
      Animated.spring(scale, {
        toValue: 1,
        tension: 60,
        friction: 8,
        useNativeDriver: true,
      }),
      Animated.timing(opacity, {
        toValue: 1,
        duration: 600,
        useNativeDriver: true,
      }),
    ]).start();

    // Glow pulse
    Animated.loop(
      Animated.sequence([
        Animated.timing(glowAnim, { toValue: 1, duration: 800, useNativeDriver: true }),
        Animated.timing(glowAnim, { toValue: 0, duration: 800, useNativeDriver: true }),
      ]),
    ).start();

    // Exit after 2.5s
    const timer = setTimeout(() => {
      Animated.timing(exitOpacity, {
        toValue: 0,
        duration: 400,
        useNativeDriver: true,
      }).start(() => onFinish());
    }, 2500);

    return () => clearTimeout(timer);
  }, []);

  const glowOpacity = glowAnim.interpolate({ inputRange: [0, 1], outputRange: [0.3, 0.8] });

  return (
    <Animated.View style={[styles.container, { opacity: exitOpacity }]}>
      <Animated.View style={[styles.content, { opacity, transform: [{ scale }] }]}>
        {/* Glow ring */}
        <Animated.View style={[styles.glowRing, { opacity: glowOpacity }]} />

        {/* Logo circle */}
        <View style={styles.logoCircle}>
          <Text style={styles.logoIcon}>⚡</Text>
        </View>

        <Text style={styles.appName}>SPORTZFY</Text>
        <Text style={styles.tagline}>Live Sports · Anytime · Anywhere</Text>

        {/* Loading dots */}
        <View style={styles.dots}>
          {[0, 1, 2].map(i => (
            <LoadingDot key={i} delay={i * 200} />
          ))}
        </View>
      </Animated.View>

      {/* Bottom branding */}
      <Text style={styles.version}>v1.0.0  •  ExoPlayer + HLS</Text>
    </Animated.View>
  );
}

function LoadingDot({ delay }: { delay: number }) {
  const anim = useRef(new Animated.Value(0.3)).current;
  useEffect(() => {
    setTimeout(() => {
      Animated.loop(
        Animated.sequence([
          Animated.timing(anim, { toValue: 1, duration: 500, useNativeDriver: true }),
          Animated.timing(anim, { toValue: 0.3, duration: 500, useNativeDriver: true }),
        ]),
      ).start();
    }, delay);
  }, []);
  return <Animated.View style={[styles.dot, { opacity: anim }]} />;
}

const styles = StyleSheet.create({
  container: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: Colors.bg,
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 9999,
  },
  content: {
    alignItems: 'center',
  },
  glowRing: {
    position: 'absolute',
    width: 160,
    height: 160,
    borderRadius: 80,
    backgroundColor: Colors.accentGlow,
    borderWidth: 2,
    borderColor: Colors.accent,
  },
  logoCircle: {
    width: 100,
    height: 100,
    borderRadius: 50,
    backgroundColor: Colors.card,
    borderWidth: 3,
    borderColor: Colors.accent,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 24,
    shadowColor: Colors.accent,
    shadowOffset: { width: 0, height: 0 },
    shadowOpacity: 0.8,
    shadowRadius: 20,
    elevation: 20,
  },
  logoIcon: {
    fontSize: 44,
  },
  appName: {
    fontSize: 36,
    fontWeight: '900',
    color: Colors.text,
    letterSpacing: 6,
    marginBottom: 8,
  },
  tagline: {
    fontSize: 13,
    color: Colors.accent,
    letterSpacing: 1,
    marginBottom: 40,
  },
  dots: {
    flexDirection: 'row',
    gap: 8,
  },
  dot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: Colors.accent,
  },
  version: {
    position: 'absolute',
    bottom: 40,
    fontSize: 11,
    color: Colors.textDim,
    letterSpacing: 1,
  },
});
