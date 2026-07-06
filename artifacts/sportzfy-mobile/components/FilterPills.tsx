import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet, ScrollView } from 'react-native';
import { Colors } from '../constants/colors';

export type StatusFilter = 'all' | 'live' | 'upcoming' | 'finished';

interface Pill {
  id: StatusFilter;
  label: string;
  icon: string;
  activeColor: string;
  activeBg: string;
}

const PILLS: Pill[] = [
  { id: 'all', label: 'All', icon: '🌐', activeColor: Colors.accent, activeBg: Colors.accentGlow },
  { id: 'live', label: 'Live', icon: '🔴', activeColor: Colors.live, activeBg: Colors.liveGlow },
  { id: 'upcoming', label: 'Upcoming', icon: '⏰', activeColor: Colors.upcoming, activeBg: Colors.upcomingGlow },
  { id: 'finished', label: 'Finished', icon: '✅', activeColor: Colors.finished, activeBg: Colors.finishedGlow },
];

interface Props {
  active: StatusFilter;
  onChange: (filter: StatusFilter) => void;
  counts?: Record<StatusFilter, number>;
}

export default function FilterPills({ active, onChange, counts }: Props) {
  return (
    <ScrollView
      horizontal
      showsHorizontalScrollIndicator={false}
      contentContainerStyle={styles.container}
    >
      {PILLS.map(pill => {
        const isActive = active === pill.id;
        return (
          <TouchableOpacity
            key={pill.id}
            style={[
              styles.pill,
              isActive && {
                backgroundColor: pill.activeBg,
                borderColor: pill.activeColor,
              },
            ]}
            onPress={() => onChange(pill.id)}
            activeOpacity={0.7}
          >
            <Text style={styles.pillIcon}>{pill.icon}</Text>
            <Text style={[styles.pillLabel, isActive && { color: pill.activeColor }]}>
              {pill.label}
            </Text>
            {counts && counts[pill.id] > 0 && (
              <View style={[styles.countBadge, { backgroundColor: pill.activeColor }]}>
                <Text style={styles.countText}>{counts[pill.id]}</Text>
              </View>
            )}
          </TouchableOpacity>
        );
      })}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    paddingHorizontal: 14,
    paddingVertical: 10,
    gap: 8,
    flexDirection: 'row',
  },
  pill: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
    paddingHorizontal: 14,
    paddingVertical: 8,
    borderRadius: 999,
    backgroundColor: Colors.card,
    borderWidth: 1.5,
    borderColor: Colors.border,
  },
  pillIcon: { fontSize: 13 },
  pillLabel: {
    fontSize: 13,
    fontWeight: '700',
    color: Colors.textMuted,
  },
  countBadge: {
    minWidth: 18,
    height: 18,
    borderRadius: 9,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 4,
  },
  countText: {
    fontSize: 10,
    fontWeight: '800',
    color: '#fff',
  },
});
