import { useState, useEffect, useCallback, useRef } from "react";

export type EventStatus = "live" | "upcoming" | "finished";

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

const BASE = "/api/sports/scores";
const FETCH_TIMEOUT_MS = 8000; // 8 s — gives up on slow connections quickly

export function useSportsData(sportFilter = "all", statusFilter = "all") {
  const [events, setEvents]           = useState<SportEvent[]>([]);
  const [loading, setLoading]         = useState(true);
  const [error, setError]             = useState<string | null>(null);
  const [lastUpdated, setLastUpdated] = useState<Date | null>(null);
  const timer = useRef<ReturnType<typeof setInterval> | null>(null);

  const fetch_ = useCallback(async () => {
    const controller = new AbortController();
    const timeoutId  = setTimeout(() => controller.abort(), FETCH_TIMEOUT_MS);
    try {
      const params = new URLSearchParams();
      if (sportFilter !== "all") params.set("sport", sportFilter);
      if (statusFilter !== "all") params.set("status", statusFilter);
      const res = await fetch(`${BASE}?${params.toString()}`, { signal: controller.signal });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const json = await res.json();
      setEvents(json.events || []);
      setLastUpdated(new Date());
      setError(null);
    } catch (e: any) {
      if (e.name === "AbortError") {
        setError("Connection timeout — retrying…");
      } else {
        setError(e.message || "Failed to load");
      }
    } finally {
      clearTimeout(timeoutId);
      setLoading(false);
    }
  }, [sportFilter, statusFilter]);

  useEffect(() => {
    setLoading(true);
    fetch_();
    timer.current = setInterval(fetch_, 30_000);
    return () => { if (timer.current) clearInterval(timer.current); };
  }, [fetch_]);

  const refresh = useCallback(() => {
    setLoading(true);
    fetch_();
  }, [fetch_]);

  return { events, loading, error, lastUpdated, refresh };
}
