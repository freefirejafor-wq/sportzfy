import React, { useState, useMemo } from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  RefreshControl,
  TouchableOpacity,
  ActivityIndicator,
} from 'react-native';
import { useRouter } from 'expo-router';
import { Colors } from '../constants/colors';
import TopBar from '../components/TopBar';
import SideMenu from '../components/SideMenu';
import FilterPills, { type StatusFilter } from '../components/FilterPills';
import MatchCard from '../components/MatchCard';
import StreamPickerModal from '../components/StreamPickerModal';
import AppSplashScreen from '../components/SplashScreen';
import { usePlayer } from '../context/PlayerContext';
import { useSportsData, type SportEvent } from '../hooks/useSportsData';
import { type Stream } from '../data/streams';

export default function HomeScreen() {
  const router = useRouter();
  const { playStream } = usePlayer();

  const [splashDone, setSplashDone] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<StatusFilter>('all');
  const [selectedEvent, setSelectedEvent] = useState<SportEvent | null>(null);
  const [streamPickerOpen, setStreamPickerOpen] = useState(false);

  const { events, loading, error, lastUpdated, refresh } = useSportsData(statusFilter);

  // Filter by search query
  const filteredEvents = useMemo(() => {
    if (!searchQuery.trim()) return events;
    const q = searchQuery.toLowerCase();
    return events.filter(
      e =>
        e.homeName.toLowerCase().includes(q) ||
        e.awayName.toLowerCase().includes(q) ||
        e.league.toLowerCase().includes(q) ||
        e.sport.toLowerCase().includes(q),
    );
  }, [events, searchQuery]);

  // Counts per status
  const counts = useMemo(() => ({
    all: events.length,
    live: events.filter(e => e.status === 'live').length,
    upcoming: events.filter(e => e.status === 'upcoming').length,
    finished: events.filter(e => e.status === 'finished').length,
  }), [events]);

  const handleWatchPress = (event: SportEvent) => {
    setSelectedEvent(event);
    setStreamPickerOpen(true);
  };

  const handleStreamSelect = (stream: Stream) => {
    if (!selectedEvent) return;
    const title = `${selectedEvent.homeName} vs ${selectedEvent.awayName}`;
    playStream(stream.url, stream.name, title);
    setStreamPickerOpen(false);
    router.push('/player');
  };

  if (!splashDone) {
    return <AppSplashScreen onFinish={() => setSplashDone(true)} />;
  }

  const renderHeader = () => (
    <>
      <FilterPills
        active={statusFilter}
        onChange={setStatusFilter}
        counts={counts}
      />

      {/* Last updated */}
      {lastUpdated && (
        <Text style={styles.lastUpdated}>
          Updated {lastUpdated.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
          {' · '}Auto-refresh every 30s
        </Text>
      )}

      {/* Error banner */}
      {error && (
        <View style={styles.errorBanner}>
          <Text style={styles.errorText}>⚠️ {error}</Text>
          <TouchableOpacity onPress={refresh} style={styles.retryBtn}>
            <Text style={styles.retryText}>Retry</Text>
          </TouchableOpacity>
        </View>
      )}

      {/* Section heading */}
      <View style={styles.sectionHeader}>
        <Text style={styles.sectionTitle}>
          {statusFilter === 'all' ? '🌐 All Events' :
           statusFilter === 'live' ? '🔴 Live Now' :
           statusFilter === 'upcoming' ? '⏰ Upcoming' : '✅ Finished'}
        </Text>
        <Text style={styles.sectionCount}>{filteredEvents.length} matches</Text>
      </View>
    </>
  );

  const renderEmpty = () => {
    if (loading) {
      return (
        <View style={styles.emptyState}>
          <ActivityIndicator size="large" color={Colors.accent} />
          <Text style={styles.emptyText}>Loading matches…</Text>
        </View>
      );
    }
    return (
      <View style={styles.emptyState}>
        <Text style={styles.emptyIcon}>📭</Text>
        <Text style={styles.emptyTitle}>No matches found</Text>
        <Text style={styles.emptyText}>
          {searchQuery ? `No results for "${searchQuery}"` : 'No events in this category'}
        </Text>
        <TouchableOpacity style={styles.refreshBtn} onPress={refresh}>
          <Text style={styles.refreshBtnText}>🔄 Refresh</Text>
        </TouchableOpacity>
      </View>
    );
  };

  return (
    <View style={styles.container}>
      <TopBar onMenuPress={() => setMenuOpen(true)} onSearch={setSearchQuery} />

      <FlatList
        data={filteredEvents}
        keyExtractor={item => item.id}
        renderItem={({ item }) => (
          <MatchCard event={item} onWatchPress={handleWatchPress} />
        )}
        ListHeaderComponent={renderHeader}
        ListEmptyComponent={renderEmpty}
        contentContainerStyle={[
          styles.listContent,
          filteredEvents.length === 0 && styles.listContentEmpty,
        ]}
        refreshControl={
          <RefreshControl
            refreshing={loading}
            onRefresh={refresh}
            tintColor={Colors.accent}
            colors={[Colors.accent]}
          />
        }
        showsVerticalScrollIndicator={false}
      />

      {/* Side menu */}
      <SideMenu visible={menuOpen} onClose={() => setMenuOpen(false)} />

      {/* Stream picker */}
      <StreamPickerModal
        visible={streamPickerOpen}
        event={selectedEvent}
        onSelect={handleStreamSelect}
        onClose={() => setStreamPickerOpen(false)}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.bg,
  },
  listContent: {
    paddingBottom: 24,
  },
  listContentEmpty: {
    flex: 1,
  },
  lastUpdated: {
    fontSize: 11,
    color: Colors.textDim,
    textAlign: 'center',
    marginBottom: 4,
    marginTop: -4,
  },
  errorBanner: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginHorizontal: 14,
    marginBottom: 8,
    paddingHorizontal: 14,
    paddingVertical: 10,
    backgroundColor: 'rgba(255,59,59,0.1)',
    borderRadius: 10,
    borderWidth: 1,
    borderColor: 'rgba(255,59,59,0.3)',
  },
  errorText: { fontSize: 12, color: Colors.live, flex: 1 },
  retryBtn: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    backgroundColor: Colors.live,
    borderRadius: 8,
  },
  retryText: { fontSize: 12, fontWeight: '700', color: '#fff' },
  sectionHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 14,
    paddingBottom: 10,
    paddingTop: 4,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: '800',
    color: Colors.text,
  },
  sectionCount: {
    fontSize: 12,
    color: Colors.textMuted,
    backgroundColor: Colors.card,
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 999,
  },
  emptyState: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    paddingTop: 60,
    gap: 12,
  },
  emptyIcon: { fontSize: 48 },
  emptyTitle: {
    fontSize: 18,
    fontWeight: '800',
    color: Colors.text,
  },
  emptyText: {
    fontSize: 13,
    color: Colors.textMuted,
    textAlign: 'center',
    paddingHorizontal: 40,
  },
  refreshBtn: {
    marginTop: 8,
    paddingHorizontal: 24,
    paddingVertical: 12,
    backgroundColor: Colors.accentGlow,
    borderRadius: 12,
    borderWidth: 1.5,
    borderColor: Colors.accent,
  },
  refreshBtnText: {
    fontSize: 14,
    fontWeight: '700',
    color: Colors.accent,
  },
});
