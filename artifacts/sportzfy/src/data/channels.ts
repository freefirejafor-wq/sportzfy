export interface Channel {
  id: number;
  name: string;
  logo: string;
  bg?: string;
}

export const sportChannels: Channel[] = [
  { id: 1, name: "TNT Sports", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/6/66/TNT_Sports_logo_2023.png/240px-TNT_Sports_logo_2023.png", bg: "#fff" },
  { id: 2, name: "beIN Sports", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b8/BeIN_Sports_logo.svg/240px-BeIN_Sports_logo.svg.png", bg: "#fff" },
  { id: 3, name: "Eleven Sports", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c5/Eleven_Sports_logo.png/240px-Eleven_Sports_logo.png", bg: "#fff" },
  { id: 4, name: "FIFA+", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/a/aa/FIFA_logo_without_slogan.svg/240px-FIFA_logo_without_slogan.svg.png", bg: "#fff" },
  { id: 5, name: "Fox Sports", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0f/FOX_Sports_logo.svg/240px-FOX_Sports_logo.svg.png", bg: "#fff" },
  { id: 6, name: "DAZN", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/c/ce/DAZN_logo.svg/240px-DAZN_logo.svg.png", bg: "#fff" },
  { id: 7, name: "Star Sports", logo: "https://upload.wikimedia.org/wikipedia/en/thumb/d/db/Star_Sports.svg/240px-Star_Sports.svg.png", bg: "#fff" },
  { id: 8, name: "Fancode", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/FanCode_Logo.png/240px-FanCode_Logo.png", bg: "#fff" },
  { id: 9, name: "PTV Sports", logo: "https://upload.wikimedia.org/wikipedia/en/thumb/e/e3/PTV_Sports_logo.png/240px-PTV_Sports_logo.png", bg: "#fff" },
  { id: 10, name: "A Sports", logo: "https://upload.wikimedia.org/wikipedia/en/thumb/5/5e/A_Sports_Official_Logo.png/240px-A_Sports_Official_Logo.png", bg: "#fff" },
  { id: 11, name: "TEN Sports", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Ten_Sports_new_logo.png/240px-Ten_Sports_new_logo.png", bg: "#fff" },
  { id: 12, name: "Willow TV", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Willow_tv.png/240px-Willow_tv.png", bg: "#fff" },
  { id: 13, name: "Criclife", logo: "https://play-lh.googleusercontent.com/sUgF3dknbPOSg8VJbRH6sZIBPXTrAz3_WsODXvPrDpzQDYSz5W2T_jAJUMFOhbIFiQ=w240-h480", bg: "#fff" },
  { id: 14, name: "Astro Cricket", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8f/Astro_SuperSport_logo_%28old%29.png/240px-Astro_SuperSport_logo_%28old%29.png", bg: "#fff" },
  { id: 15, name: "T Sports", logo: "https://upload.wikimedia.org/wikipedia/en/thumb/2/20/T_Sports_TV_logo.png/240px-T_Sports_TV_logo.png", bg: "#fff" },
  { id: 16, name: "RTA Sport", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a3/RTA_Sport_HD_Arabic.png/240px-RTA_Sport_HD_Arabic.png", bg: "#fff" },
  { id: 17, name: "Sky Sports", logo: "https://upload.wikimedia.org/wikipedia/en/thumb/a/a3/Sky_Sports_logo_2020.svg/240px-Sky_Sports_logo_2020.svg.png", bg: "#fff" },
  { id: 18, name: "Fox Cricket", logo: "https://upload.wikimedia.org/wikipedia/en/thumb/5/5d/Fox_Cricket_logo.png/240px-Fox_Cricket_logo.png", bg: "#fff" },
  { id: 19, name: "SPOTV", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0e/SPOTV_logo.png/240px-SPOTV_logo.png", bg: "#fff" },
  { id: 20, name: "Ziggo Sport", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/Ziggo_Sport_logo.svg/240px-Ziggo_Sport_logo.svg.png", bg: "#fff" },
  { id: 21, name: "Super Sport", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/SuperSport_%28TV_channel%29_logo.svg/240px-SuperSport_%28TV_channel%29_logo.svg.png", bg: "#fff" },
  { id: 22, name: "Nova Sports", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/7/71/Nova_Sports_logo.png/240px-Nova_Sports_logo.png", bg: "#fff" },
  { id: 23, name: "Cosmote Sport", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/5/50/Cosmote_TV_logo.png/240px-Cosmote_TV_logo.png", bg: "#fff" },
  { id: 24, name: "Premier Sports", logo: "https://upload.wikimedia.org/wikipedia/en/thumb/6/61/Premier_Sports_UK_logo.png/240px-Premier_Sports_UK_logo.png", bg: "#fff" },
];

export const categories: Channel[] = [
  { id: 1, name: "Fancode IND", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/FanCode_Logo.png/240px-FanCode_Logo.png" },
  { id: 2, name: "Fancode IND 2", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/FanCode_Logo.png/240px-FanCode_Logo.png" },
  { id: 3, name: "Fancode BD", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/FanCode_Logo.png/240px-FanCode_Logo.png" },
  { id: 4, name: "ICC TV", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/ICC_logo.png/240px-ICC_logo.png" },
  { id: 5, name: "Sony LIV LIVE", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/7/74/Sony_liv_logo.svg/240px-Sony_liv_logo.svg.png" },
  { id: 6, name: "Sony LIV LIVE 2", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/7/74/Sony_liv_logo.svg/240px-Sony_liv_logo.svg.png" },
  { id: 7, name: "Sony LIV", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/7/74/Sony_liv_logo.svg/240px-Sony_liv_logo.svg.png" },
  { id: 8, name: "CricHD", logo: "https://play-lh.googleusercontent.com/KT2kqJ3bMUkMOFjP7Ff6Q6R6SqMW7kq7qhbEiT8jX7OL2f0Hzm0WqM4SQ3XF9v-2wg=w240-h480" },
  { id: 9, name: "CricHD 2", logo: "https://play-lh.googleusercontent.com/KT2kqJ3bMUkMOFjP7Ff6Q6R6SqMW7kq7qhbEiT8jX7OL2f0Hzm0WqM4SQ3XF9v-2wg=w240-h480" },
  { id: 10, name: "Pluto TV", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/0/09/Pluto_TV_Logo.svg/240px-Pluto_TV_Logo.svg.png" },
  { id: 11, name: "Tubi TV", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/Tubi_logo.svg/240px-Tubi_logo.svg.png" },
  { id: 12, name: "Roku", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cd/Roku_logo.svg/240px-Roku_logo.svg.png" },
  { id: 13, name: "Free TV", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/1/19/FreeTVAustralia_logo.png/240px-FreeTVAustralia_logo.png" },
  { id: 14, name: "Fire TV", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f0/Amazon_fire_tv_logo.svg/240px-Amazon_fire_tv_logo.svg.png" },
  { id: 15, name: "Pakistan", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/3/32/Flag_of_Pakistan.svg/240px-Flag_of_Pakistan.svg.png" },
  { id: 16, name: "News", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c3/Cnn_logo.svg/240px-Cnn_logo.svg.png" },
  { id: 17, name: "Islamic", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f2/Mosque_icon.svg/240px-Mosque_icon.svg.png" },
  { id: 18, name: "Kids", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f1/Cartoon_Network_2010_logo.svg/240px-Cartoon_Network_2010_logo.svg.png" },
  { id: 19, name: "Discovery", logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/Discovery_Channel_logo_2019.svg/240px-Discovery_Channel_logo_2019.svg.png" },
  { id: 20, name: "Sun Direct", logo: "https://upload.wikimedia.org/wikipedia/en/thumb/5/5a/Sun_Direct_Logo.svg/240px-Sun_Direct_Logo.svg.png" },
];
