import { useState, useEffect, useCallback, useRef } from 'react';

export type EventStatus = 'live' | 'upcoming' | 'finished';

export interface SportEvent {
  id: string;
  sport: string;
  league: string;
  homeName: string;
  awayName: string;
  homeFlag: string;
  awayFlag: string;
  homeLogo: string;
  awayLogo: string;
  homeScore: string;
  awayScore: string;
  homeColor: string;
  awayColor: string;
  status: EventStatus;
  displayTime: string;
  clock?: string;
  period?: string;
  venue: string;
  cricketDetail: string | null;
  date: string;
}

const FETCH_TIMEOUT_MS = 8000;

export function useSportsData(statusFilter = 'all') {
  const [events, setEvents] = useState<SportEvent[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [lastUpdated, setLastUpdated] = useState<Date | null>(null);
  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const fetchData = useCallback(async () => {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), FETCH_TIMEOUT_MS);

    try {
      // Use EXPO_PUBLIC_API_URL for deployed APK
      // Falls back to the Replit dev domain in development
      const domain = process.env.EXPO_PUBLIC_DOMAIN || process.env.EXPO_PUBLIC_API_URL || '';
      const baseUrl = domain ? `https://${domain}` : '';
      const params = new URLSearchParams();
      if (statusFilter !== 'all') params.set('status', statusFilter);

      const res = await fetch(`${baseUrl}/api/sports/scores?${params.toString()}`, {
        signal: controller.signal,
        headers: { 'Accept': 'application/json' },
      });

      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const json = await res.json();
      setEvents(json.events || []);
      setLastUpdated(new Date());
      setError(null);
    } catch (e: any) {
      if (e.name === 'AbortError') {
        setError('Connection timeout — retrying…');
      } else {
        setError(e.message || 'Failed to load');
      }
    } finally {
      clearTimeout(timeoutId);
      setLoading(false);
    }
  }, [statusFilter]);

  useEffect(() => {
    setLoading(true);
    fetchData();
    timerRef.current = setInterval(fetchData, 30_000);
    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
    };
  }, [fetchData]);

  const refresh = useCallback(() => {
    setLoading(true);
    fetchData();
  }, [fetchData]);

  return { events, loading, error, lastUpdated, refresh };
}
