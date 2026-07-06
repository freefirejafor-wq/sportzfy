import React, { useState, useRef, useEffect } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
  Animated,
  Modal,
  Dimensions,
} from 'react-native';
import { Colors } from '../constants/colors';
import { ALL_STREAMS, STREAM_CATEGORIES, type Stream } from '../data/streams';
import type { SportEvent } from '../hooks/useSportsData';

const { height } = Dimensions.get('window');

const QUALITY_COLORS: Record<string, string> = {
  FHD: Colors.qualityFHD,
  HD: Colors.qualityHD,
  SD: Colors.qualitySD,
};

interface Props {
  visible: boolean;
  event: SportEvent | null;
  onSelect: (stream: Stream) => void;
  onClose: () => void;
}

export default function StreamPickerModal({ visible, event, onSelect, onClose }: Props) {
  const translateY = useRef(new Animated.Value(height)).current;
  const [activeCategory, setActiveCategory] = useState('All');

  useEffect(() => {
    if (visible) {
      Animated.spring(translateY, {
        toValue: 0,
        tension: 65,
        friction: 12,
        useNativeDriver: true,
      }).start();
    } else {
      Animated.timing(translateY, {
        toValue: height,
        duration: 250,
        useNativeDriver: true,
      }).start();
    }
  }, [visible]);

  const streams = activeCategory === 'All'
    ? ALL_STREAMS
    : ALL_STREAMS.filter(s => s.category === activeCategory);

  if (!visible && !event) return null;

  return (
    <Modal transparent visible={visible} onRequestClose={onClose} animationType="none">
      <View style={styles.backdrop}>
        <TouchableOpacity style={StyleSheet.absoluteFill} onPress={onClose} activeOpacity={1} />

        <Animated.View style={[styles.sheet, { transform: [{ translateY }] }]}>
          {/* Handle */}
          <View style={styles.handle} />

          {/* Header */}
          <View style={styles.header}>
            <View style={styles.headerIcon}>
              <Text style={{ fontSize: 22 }}>📡</Text>
            </View>
            <View style={{ flex: 1 }}>
              <Text style={styles.headerTitle}>Multiple Streams</Text>
              <Text style={styles.headerSub}>
                {event ? `${event.homeName} vs ${event.awayName}` : 'Choose a stream'}
              </Text>
            </View>
            <TouchableOpacity style={styles.closeBtn} onPress={onClose}>
              <Text style={styles.closeText}>✕</Text>
            </TouchableOpacity>
          </View>

          {/* Category tabs */}
          <ScrollView
            horizontal
            showsHorizontalScrollIndicator={false}
            contentContainerStyle={styles.categories}
          >
            {STREAM_CATEGORIES.map(cat => (
              <TouchableOpacity
                key={cat}
                style={[styles.catPill, activeCategory === cat && styles.catPillActive]}
                onPress={() => setActiveCategory(cat)}
              >
                <Text style={[styles.catText, activeCategory === cat && styles.catTextActive]}>
                  {cat}
                </Text>
              </TouchableOpacity>
            ))}
          </ScrollView>

          {/* Stream list */}
          <ScrollView style={styles.list} showsVerticalScrollIndicator={false}>
            {streams.map((stream, i) => (
              <TouchableOpacity
                key={stream.id}
                style={[styles.streamRow, i === streams.length - 1 && { borderBottomWidth: 0 }]}
                onPress={() => { onSelect(stream); onClose(); }}
                activeOpacity={0.7}
              >
                <View style={styles.streamLeft}>
                  <View style={styles.streamDot} />
                  <View>
                    <Text style={styles.streamName}>{stream.name}</Text>
                    <Text style={styles.streamCategory}>{stream.category}</Text>
                  </View>
                </View>
                <View style={[styles.qualityBadge, { borderColor: QUALITY_COLORS[stream.quality] }]}>
                  <Text style={[styles.qualityText, { color: QUALITY_COLORS[stream.quality] }]}>
                    {stream.quality}
                  </Text>
                </View>
              </TouchableOpacity>
            ))}
          </ScrollView>

          {/* Cancel */}
          <View style={styles.cancelRow}>
            <TouchableOpacity style={styles.cancelBtn} onPress={onClose}>
              <Text style={styles.cancelText}>✕  Cancel</Text>
            </TouchableOpacity>
          </View>
        </Animated.View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  backdrop: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.75)',
    justifyContent: 'flex-end',
  },
  sheet: {
    backgroundColor: Colors.surface,
    borderTopLeftRadius: 24,
    borderTopRightRadius: 24,
    borderTopWidth: 1.5,
    borderLeftWidth: 1.5,
    borderRightWidth: 1.5,
    borderColor: Colors.accent,
    maxHeight: height * 0.87,
  },
  handle: {
    width: 40,
    height: 4,
    borderRadius: 2,
    backgroundColor: Colors.border,
    alignSelf: 'center',
    marginTop: 12,
    marginBottom: 4,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
    paddingHorizontal: 20,
    paddingVertical: 16,
    borderBottomWidth: 1,
    borderBottomColor: Colors.border,
  },
  headerIcon: {
    width: 48,
    height: 48,
    borderRadius: 24,
    backgroundColor: Colors.card,
    borderWidth: 2,
    borderColor: Colors.accent,
    alignItems: 'center',
    justifyContent: 'center',
  },
  headerTitle: {
    fontSize: 17,
    fontWeight: '800',
    color: Colors.text,
  },
  headerSub: {
    fontSize: 12,
    color: Colors.textMuted,
    marginTop: 2,
  },
  closeBtn: {
    width: 32,
    height: 32,
    borderRadius: 8,
    backgroundColor: Colors.card,
    alignItems: 'center',
    justifyContent: 'center',
  },
  closeText: { color: Colors.textMuted, fontSize: 14 },
  categories: {
    paddingHorizontal: 16,
    paddingVertical: 12,
    gap: 8,
    flexDirection: 'row',
  },
  catPill: {
    paddingHorizontal: 14,
    paddingVertical: 6,
    borderRadius: 999,
    backgroundColor: Colors.card,
    borderWidth: 1,
    borderColor: Colors.border,
  },
  catPillActive: {
    backgroundColor: Colors.accentGlow,
    borderColor: Colors.accent,
  },
  catText: {
    fontSize: 12,
    fontWeight: '700',
    color: Colors.textMuted,
  },
  catTextActive: { color: Colors.accent },
  list: { flex: 1 },
  streamRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: 14,
    paddingHorizontal: 20,
    borderBottomWidth: 1,
    borderBottomColor: Colors.border,
  },
  streamLeft: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 14,
    flex: 1,
  },
  streamDot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: Colors.accent,
  },
  streamName: {
    fontSize: 14,
    fontWeight: '700',
    color: Colors.accent,
  },
  streamCategory: {
    fontSize: 11,
    color: Colors.textMuted,
    marginTop: 2,
  },
  qualityBadge: {
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 999,
    borderWidth: 1,
    backgroundColor: Colors.card,
  },
  qualityText: {
    fontSize: 11,
    fontWeight: '800',
  },
  cancelRow: {
    padding: 16,
    paddingBottom: 32,
  },
  cancelBtn: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 14,
    borderRadius: 12,
    backgroundColor: Colors.card,
    borderWidth: 1,
    borderColor: Colors.border,
  },
  cancelText: {
    fontSize: 15,
    fontWeight: '700',
    color: Colors.textMuted,
  },
});
