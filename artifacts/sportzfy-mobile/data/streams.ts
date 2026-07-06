export type Quality = 'FHD' | 'HD' | 'SD';

export interface Stream {
  id: string;
  name: string;
  quality: Quality;
  url: string;
  category: string;
}

// All verified-active streams (July 2026) — direct URLs, no CORS proxy needed for native app
export const ALL_STREAMS: Stream[] = [
  // ── FIFA / World Cup ──────────────────────────────────────────────
  {
    id: 'fifa-wc-eng-master',
    name: 'FIFA WC ENG (Master)',
    quality: 'FHD',
    url: 'https://hls.livekhelatv.website/mks/lktv/fifa-worldcup-eng/master.m3u8',
    category: 'FIFA',
  },
  {
    id: 'fifa-wc-eng-1080p',
    name: 'FIFA WC ENG (1080p)',
    quality: 'FHD',
    url: 'https://hls.livekhelatv.website/mks/lktv/s/fifa-worldcup-eng/playlist/1080p_FHD/livekhela.m3u8',
    category: 'FIFA',
  },
  {
    id: 'caze-tv-4k',
    name: 'Caze TV (FIFA 4K)',
    quality: 'FHD',
    url: 'https://dfr80qz435crc.cloudfront.net/MNOP/Amagi/Caze/Caze_TV_BR/1080p-vtt/index.m3u8',
    category: 'FIFA',
  },
  {
    id: 'fifa-plus-english',
    name: 'FIFA+ English',
    quality: 'FHD',
    url: 'https://a62dad94.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/UmFrdXRlblRWLWV1X0ZJRkFQbHVzRW5nbGlzaF9ITFM/playlist.m3u8',
    category: 'FIFA',
  },
  {
    id: 'fifa-plus-usa',
    name: 'FIFA+ USA',
    quality: 'FHD',
    url: 'https://d2w9q46ikgrcwx.cloudfront.net/v1/master/3722c60a815c199d9c0ef36c5b73da68a62b09d1/cc-of5cbk3sav3w5/v1/sysdata_s_p_a_fifa_7/samsungheadend_us/latest/main/hls/playlist.m3u8',
    category: 'FIFA',
  },
  {
    id: 'fifa-plus-espanol',
    name: 'FIFA+ Español',
    quality: 'FHD',
    url: 'https://d63fabad.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/UmFrdXRlblRWLWVzX0ZJRkFQbHVzU3BhbmlzaF9ITFM/playlist.m3u8',
    category: 'FIFA',
  },
  {
    id: 'fifa-plus-brasil',
    name: 'FIFA+ Brasil',
    quality: 'FHD',
    url: 'https://e3be9ac5.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/TEctYnJfRklGQVBsdXNQb3J0dWd1ZXNlX0hMUw/playlist.m3u8',
    category: 'FIFA',
  },
  {
    id: 'fifa-plus-deutschland',
    name: 'FIFA+ Deutschland',
    quality: 'FHD',
    url: 'https://4397879b.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/UmFrdXRlblRWLWRlX0ZJRkFQbHVzR2VybWFuX0hMUw/playlist.m3u8',
    category: 'FIFA',
  },
  {
    id: 'fifa-plus-argentina',
    name: 'FIFA+ Argentina',
    quality: 'FHD',
    url: 'https://6c849fb3.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/TEctbXhfRklGQVBsdXNTcGFuaXNoLTFfSExT/playlist.m3u8',
    category: 'FIFA',
  },
  {
    id: 'fifa-plus-italia',
    name: 'FIFA+ Italia',
    quality: 'FHD',
    url: 'https://5d95f7d7.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/UmFrdXRlblRWLWl0X0ZJRkFQbHVzSXRhbGlhbl9ITFM/playlist.m3u8',
    category: 'FIFA',
  },
  {
    id: 'fifa-plus-france',
    name: 'FIFA+ France',
    quality: 'FHD',
    url: 'https://37b4c228.wurl.com/master/f36d25e7e52f1ba8d7e56eb859c636563214f541/UmFrdXRlblRWLWZyX0ZJRkFQbHVzRnJlbmNoX0hMUw/playlist.m3u8',
    category: 'FIFA',
  },
  {
    id: 'telemundo-fifa',
    name: 'Telemundo (FIFA)',
    quality: 'HD',
    url: 'https://nbculocallive.akamaized.net/hls/live/2037499/puertorico/stream1/master.m3u8',
    category: 'FIFA',
  },
  {
    id: 'fifa-stream-a',
    name: 'FIFA Stream A',
    quality: 'HD',
    url: 'https://1nyaler.streamhostingcdn.top/stream/23/index.m3u8',
    category: 'FIFA',
  },
  {
    id: 'world-cup-s2',
    name: 'World Cup S2',
    quality: 'HD',
    url: 'https://andro.226503.xyz/checklist/androstreamlivebs1.m3u8',
    category: 'FIFA',
  },
  {
    id: 'win-sports-wc',
    name: 'Win Sports WC',
    quality: 'HD',
    url: 'https://1nyaler.streamhostingcdn.top/stream/32/index.m3u8',
    category: 'FIFA',
  },
  {
    id: '2tv-sports-wc',
    name: '2TV Sports WC',
    quality: 'HD',
    url: 'https://tv.cdn.xsg.ge/gpb-2tv/index.m3u8',
    category: 'FIFA',
  },

  // ── beIN Sports ──────────────────────────────────────────────────
  {
    id: 'bein-1-uhd',
    name: 'beIN Sports 1 UHD',
    quality: 'FHD',
    url: 'http://proxpanel.cc/h1wqD6CY/byxHYgX/707929',
    category: 'beIN',
  },
  {
    id: 'bein-1-amagi',
    name: 'beIN Sports 1 (Amagi)',
    quality: 'FHD',
    url: 'https://cdn-uw2-prod.tsv2.amagi.tv/linear/amg02873-kravemedia-mtrspt1-distrotv/playlist.m3u8',
    category: 'beIN',
  },
  {
    id: 'bein-2',
    name: 'beIN Sports 2',
    quality: 'HD',
    url: 'https://andro.226503.xyz/checklist/androstreamlivebs2.m3u8',
    category: 'beIN',
  },
  {
    id: 'bein-3',
    name: 'beIN Sports 3',
    quality: 'HD',
    url: 'https://andro.226503.xyz/checklist/androstreamlivebs3.m3u8',
    category: 'beIN',
  },
  {
    id: 'bein-4',
    name: 'beIN Sports 4',
    quality: 'HD',
    url: 'https://andro.226503.xyz/checklist/androstreamlivebs4.m3u8',
    category: 'beIN',
  },
  {
    id: 'bein-xtra',
    name: 'beIN Sports Xtra',
    quality: 'HD',
    url: 'https://bein-esp-xumo.amagi.tv/playlistR720P.m3u8',
    category: 'beIN',
  },

  // ── Other sports channels ────────────────────────────────────────
  {
    id: 'espn-2',
    name: 'ESPN 2',
    quality: 'HD',
    url: 'https://tv.topmediatv.net:25463/live/TopMediaWeb/bOteTR8ED1/108.m3u8',
    category: 'Sports',
  },
  {
    id: 'tyc-sports',
    name: 'TyC Sports Argentina',
    quality: 'HD',
    url: 'https://amg26268-amg26268c14-freelivesports-emea-10267.playouts.now.amagi.tv/ts-us-e2-n2/playlist/amg26268-sportsstudio-tycsports-freelivesportsemea/playlist.m3u8',
    category: 'Sports',
  },
  {
    id: 'euro-tv',
    name: 'Euro TV',
    quality: 'HD',
    url: 'https://stream.ottplus.bd/live/euro_sports_hd_abr/live/euro_sports_hd/chunks.m3u8',
    category: 'Sports',
  },
  {
    id: 'cricket-gold',
    name: 'Cricket Gold',
    quality: 'HD',
    url: 'https://streams2.sofast.tv/ptnr-yupptv/title-cricketgold/v1/manifest/611d79b11b77e2f571934fd80ca1413453772ac7/b2048bb8-1686-4432-aa50-647245383e0c/bfc6a36e-c250-4afe-b6c9-2bc57855bb7d/4.m3u8',
    category: 'Cricket',
  },
];

export const STREAM_CATEGORIES = ['All', 'FIFA', 'beIN', 'Sports', 'Cricket'];

export function getStreamsByCategory(category: string): Stream[] {
  if (category === 'All') return ALL_STREAMS;
  return ALL_STREAMS.filter(s => s.category === category);
}
