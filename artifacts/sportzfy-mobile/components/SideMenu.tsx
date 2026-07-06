import React, { useEffect, useRef } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Animated,
  Dimensions,
  ScrollView,
  Pressable,
} from 'react-native';
import { Colors } from '../constants/colors';

const { width } = Dimensions.get('window');
const MENU_WIDTH = width * 0.78;

interface MenuItem {
  icon: string;
  label: string;
  badge?: string;
}

const MENU_ITEMS: MenuItem[] = [
  { icon: '🏠', label: 'Home' },
  { icon: '🔴', label: 'Live Now', badge: 'LIVE' },
  { icon: '📅', label: 'Schedule' },
  { icon: '⭐', label: 'Favourites' },
  { icon: '⚽', label: 'Football' },
  { icon: '🏏', label: 'Cricket' },
  { icon: '🏀', label: 'Basketball' },
  { icon: '🎾', label: 'Tennis' },
  { icon: '🏆', label: 'Tournaments' },
  { icon: '📺', label: 'All Channels' },
  { icon: '⚙️', label: 'Settings' },
];

interface Props {
  visible: boolean;
  onClose: () => void;
}

export default function SideMenu({ visible, onClose }: Props) {
  const translateX = useRef(new Animated.Value(-MENU_WIDTH)).current;
  const overlayOpacity = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    if (visible) {
      Animated.parallel([
        Animated.spring(translateX, {
          toValue: 0,
          tension: 65,
          friction: 11,
          useNativeDriver: true,
        }),
        Animated.timing(overlayOpacity, {
          toValue: 1,
          duration: 250,
          useNativeDriver: true,
        }),
      ]).start();
    } else {
      Animated.parallel([
        Animated.timing(translateX, {
          toValue: -MENU_WIDTH,
          duration: 220,
          useNativeDriver: true,
        }),
        Animated.timing(overlayOpacity, {
          toValue: 0,
          duration: 200,
          useNativeDriver: true,
        }),
      ]).start();
    }
  }, [visible]);

  if (!visible && translateX.__getValue() === -MENU_WIDTH) return null;

  return (
    <View style={StyleSheet.absoluteFill} pointerEvents={visible ? 'auto' : 'none'}>
      {/* Overlay */}
      <Animated.View style={[styles.overlay, { opacity: overlayOpacity }]}>
        <Pressable style={StyleSheet.absoluteFill} onPress={onClose} />
      </Animated.View>

      {/* Drawer */}
      <Animated.View style={[styles.drawer, { transform: [{ translateX }] }]}>
        {/* Header */}
        <View style={styles.header}>
          <View style={styles.logoRow}>
            <View style={styles.logoCircle}>
              <Text style={styles.logoEmoji}>⚡</Text>
            </View>
            <View>
              <Text style={styles.appName}>SPORTZFY</Text>
              <Text style={styles.appTagline}>Live Sports Streaming</Text>
            </View>
          </View>
          <TouchableOpacity style={styles.closeBtn} onPress={onClose}>
            <Text style={styles.closeIcon}>✕</Text>
          </TouchableOpacity>
        </View>

        {/* Live indicator */}
        <View style={styles.liveBar}>
          <View style={styles.liveDot} />
          <Text style={styles.liveText}>Streaming Now — FIFA World Cup 2026</Text>
        </View>

        {/* Menu items */}
        <ScrollView style={styles.menuList} showsVerticalScrollIndicator={false}>
          {MENU_ITEMS.map((item, i) => (
            <TouchableOpacity
              key={i}
              style={styles.menuItem}
              onPress={onClose}
              activeOpacity={0.7}
            >
              <Text style={styles.menuIcon}>{item.icon}</Text>
              <Text style={styles.menuLabel}>{item.label}</Text>
              {item.badge && (
                <View style={styles.badge}>
                  <Text style={styles.badgeText}>{item.badge}</Text>
                </View>
              )}
            </TouchableOpacity>
          ))}
        </ScrollView>

        {/* Footer */}
        <View style={styles.footer}>
          <Text style={styles.footerText}>Sportzfy v1.0.0</Text>
          <Text style={styles.footerSub}>ExoPlayer • HLS • DASH</Text>
        </View>
      </Animated.View>
    </View>
  );
}

const styles = StyleSheet.create({
  overlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(0,0,0,0.7)',
  },
  drawer: {
    position: 'absolute',
    top: 0,
    left: 0,
    bottom: 0,
    width: MENU_WIDTH,
    backgroundColor: Colors.surface,
    borderRightWidth: 1.5,
    borderRightColor: Colors.accent,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingTop: 56,
    paddingHorizontal: 20,
    paddingBottom: 20,
    borderBottomWidth: 1,
    borderBottomColor: Colors.border,
  },
  logoRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
  },
  logoCircle: {
    width: 46,
    height: 46,
    borderRadius: 23,
    backgroundColor: Colors.card,
    borderWidth: 2,
    borderColor: Colors.accent,
    alignItems: 'center',
    justifyContent: 'center',
  },
  logoEmoji: { fontSize: 22 },
  appName: {
    fontSize: 18,
    fontWeight: '900',
    color: Colors.accent,
    letterSpacing: 3,
  },
  appTagline: {
    fontSize: 11,
    color: Colors.textMuted,
    marginTop: 1,
  },
  closeBtn: {
    width: 32,
    height: 32,
    borderRadius: 8,
    backgroundColor: Colors.card,
    alignItems: 'center',
    justifyContent: 'center',
  },
  closeIcon: { color: Colors.textMuted, fontSize: 14 },
  liveBar: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    margin: 16,
    paddingHorizontal: 14,
    paddingVertical: 10,
    backgroundColor: Colors.liveGlow,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: 'rgba(255,59,59,0.4)',
  },
  liveDot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: Colors.live,
  },
  liveText: {
    fontSize: 12,
    color: Colors.live,
    fontWeight: '700',
    flex: 1,
  },
  menuList: { flex: 1 },
  menuItem: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 14,
    paddingHorizontal: 20,
    gap: 14,
    borderBottomWidth: 1,
    borderBottomColor: Colors.border,
  },
  menuIcon: { fontSize: 20, width: 28 },
  menuLabel: {
    flex: 1,
    fontSize: 15,
    fontWeight: '600',
    color: Colors.text,
  },
  badge: {
    backgroundColor: Colors.live,
    paddingHorizontal: 8,
    paddingVertical: 3,
    borderRadius: 8,
  },
  badgeText: {
    fontSize: 10,
    fontWeight: '800',
    color: '#fff',
    letterSpacing: 0.5,
  },
  footer: {
    padding: 20,
    borderTopWidth: 1,
    borderTopColor: Colors.border,
    alignItems: 'center',
  },
  footerText: { fontSize: 12, color: Colors.textMuted, fontWeight: '600' },
  footerSub: { fontSize: 11, color: Colors.textDim, marginTop: 2 },
});
