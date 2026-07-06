import React, { useEffect, useRef, useState } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Image,
  Animated,
} from 'react-native';
import { Colors } from '../constants/colors';
import type { SportEvent } from '../hooks/useSportsData';

interface Props {
  event: SportEvent;
  onWatchPress: (event: SportEvent) => void;
}

function TeamLogo({ uri, name, size = 40 }: { uri?: string; name: string; size?: number }) {
  const [error, setError] = useState(false);
  const isUrl = uri && (uri.startsWith('http') || uri.startsWith('/'));

  if (isUrl && !error) {
    return (
      <Image
        source={{ uri }}
        style={{ width: size, height: size, borderRadius: size / 2, backgroundColor: '#fff' }}
        onError={() => setError(true)}
        resizeMode="contain"
      />
    );
  }
  // Emoji flag or initials
  if (uri && !isUrl) {
    return <Text style={{ fontSize: size * 0.7 }}>{uri}</Text>;
  }
  return (
    <View
      style={{
        width: size, height: size, borderRadius: size / 2,
        backgroundColor: Colors.cardBorder,
        alignItems: 'center', justifyContent: 'center',
      }}
    >
      <Text style={{ fontSize: size * 0.3, fontWeight: '800', color: Colors.accent }}>
        {name.slice(0, 2).toUpperCase()}
      </Text>
    </View>
  );
}

function LiveClock({ clock, period }: { clock?: string; period?: string }) {
  const pulse = useRef(new Animated.Value(1)).current;
  useEffect(() => {
    Animated.loop(
      Animated.sequence([
        Animated.timing(pulse, { toValue: 1.3, duration: 600, useNativeDriver: true }),
        Animated.timing(pulse, { toValue: 1, duration: 600, useNativeDriver: true }),
      ]),
    ).start();
  }, []);

  return (
    <View style={clockStyles.row}>
      <Animated.View style={[clockStyles.dot, { transform: [{ scale: pulse }] }]} />
      <Text style={clockStyles.text}>
        {clock || 'LIVE'}{period ? ` · ${period}` : ''}
      </Text>
    </View>
  );
}

const clockStyles = StyleSheet.create({
  row: { flexDirection: 'row', alignItems: 'center', gap: 5 },
  dot: { width: 6, height: 6, borderRadius: 3, backgroundColor: Colors.live },
  text: { fontSize: 11, fontWeight: '800', color: Colors.live, letterSpacing: 0.5 },
});

export default function MatchCard({ event, onWatchPress }: Props) {
  const isLive = event.status === 'live';
  const isUpcoming = event.status === 'upcoming';
  const isFinished = event.status === 'finished';

  const statusColor = isLive ? Colors.live : isUpcoming ? Colors.upcoming : Colors.finished;
  const statusBg = isLive ? Colors.liveGlow : isUpcoming ? Colors.upcomingGlow : Colors.finishedGlow;
  const statusLabel = isLive ? 'LIVE' : isUpcoming ? 'UPCOMING' : 'FT';

  return (
    <View style={styles.card}>
      {/* Header row */}
      <View style={styles.header}>
        <View style={styles.leagueRow}>
          <Text style={styles.sport}>{event.sport}</Text>
          <Text style={styles.dot}>·</Text>
          <Text style={styles.league}>{event.league}</Text>
        </View>
        <View style={[styles.statusBadge, { backgroundColor: statusBg, borderColor: statusColor }]}>
          {isLive && <LiveClock clock={event.clock} period={event.period} />}
          {!isLive && <Text style={[styles.statusText, { color: statusColor }]}>{statusLabel}</Text>}
        </View>
      </View>

      {/* Teams row */}
      <View style={styles.teamsRow}>
        {/* Home */}
        <View style={styles.teamCol}>
          <TeamLogo uri={event.homeLogo || event.homeFlag} name={event.homeName} />
          <Text style={styles.teamName} numberOfLines={2}>{event.homeName}</Text>
        </View>

        {/* Score / VS */}
        <View style={styles.scoreCol}>
          {isLive || isFinished ? (
            <>
              <Text style={styles.score}>
                {event.homeScore} – {event.awayScore}
              </Text>
              {event.cricketDetail && (
                <Text style={styles.cricketDetail} numberOfLines={2}>
                  {event.cricketDetail}
                </Text>
              )}
            </>
          ) : (
            <>
              <Text style={styles.vs}>VS</Text>
              <Text style={styles.time}>{event.displayTime}</Text>
            </>
          )}
        </View>

        {/* Away */}
        <View style={styles.teamCol}>
          <TeamLogo uri={event.awayLogo || event.awayFlag} name={event.awayName} />
          <Text style={styles.teamName} numberOfLines={2}>{event.awayName}</Text>
        </View>
      </View>

      {/* Venue */}
      {event.venue && (
        <Text style={styles.venue} numberOfLines={1}>📍 {event.venue}</Text>
      )}

      {/* Watch button */}
      {!isFinished && (
        <TouchableOpacity
          style={[styles.watchBtn, isLive && styles.watchBtnLive]}
          onPress={() => onWatchPress(event)}
          activeOpacity={0.8}
        >
          <Text style={styles.watchIcon}>{isLive ? '▶' : '📺'}</Text>
          <Text style={styles.watchText}>{isLive ? 'Watch Live' : 'Set Reminder'}</Text>
        </TouchableOpacity>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: Colors.card,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: Colors.cardBorder,
    marginHorizontal: 14,
    marginBottom: 12,
    padding: 14,
    gap: 12,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  leagueRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
    flex: 1,
  },
  sport: {
    fontSize: 11,
    fontWeight: '700',
    color: Colors.accent,
    textTransform: 'uppercase',
    letterSpacing: 0.5,
  },
  dot: { color: Colors.textDim, fontSize: 11 },
  league: {
    fontSize: 11,
    color: Colors.textMuted,
    flex: 1,
  },
  statusBadge: {
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 999,
    borderWidth: 1,
  },
  statusText: {
    fontSize: 11,
    fontWeight: '800',
    letterSpacing: 0.5,
  },
  teamsRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  teamCol: {
    flex: 1,
    alignItems: 'center',
    gap: 8,
  },
  teamName: {
    fontSize: 12,
    fontWeight: '700',
    color: Colors.text,
    textAlign: 'center',
  },
  scoreCol: {
    alignItems: 'center',
    paddingHorizontal: 12,
    minWidth: 80,
  },
  score: {
    fontSize: 26,
    fontWeight: '900',
    color: Colors.text,
    letterSpacing: 2,
  },
  cricketDetail: {
    fontSize: 10,
    color: Colors.textMuted,
    textAlign: 'center',
    marginTop: 2,
  },
  vs: {
    fontSize: 20,
    fontWeight: '900',
    color: Colors.textDim,
  },
  time: {
    fontSize: 11,
    color: Colors.accent,
    fontWeight: '600',
    marginTop: 2,
  },
  venue: {
    fontSize: 11,
    color: Colors.textDim,
  },
  watchBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 8,
    backgroundColor: Colors.accentGlow,
    borderWidth: 1.5,
    borderColor: Colors.accent,
    borderRadius: 10,
    paddingVertical: 10,
  },
  watchBtnLive: {
    backgroundColor: Colors.liveGlow,
    borderColor: Colors.live,
  },
  watchIcon: { fontSize: 14 },
  watchText: {
    fontSize: 14,
    fontWeight: '800',
    color: Colors.text,
  },
});
