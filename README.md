# ⚡ Sportzfy — Live Sports Streaming App

> Watch FIFA World Cup, beIN Sports, Cricket Gold, ESPN & 30+ HLS streams — on Web and Android.

[![Build APK](https://github.com/freefirejafor-wq/sportzfy/actions/workflows/eas-build-apk.yml/badge.svg)](https://github.com/freefirejafor-wq/sportzfy/actions/workflows/eas-build-apk.yml)

---

## 📱 Android APK — How to Build

### Step 1 — Add your Expo Token to GitHub Secrets

1. Go to [expo.dev](https://expo.dev) → Create a free account
2. Go to **Account Settings → Access Tokens** → Create token
3. Go to your GitHub repo → **Settings → Secrets → Actions**
4. Add a new secret: **`EXPO_TOKEN`** = paste your token

### Step 2 — Trigger APK Build

- **Auto**: Push any code to `main` branch → APK builds automatically
- **Manual**: Go to **Actions tab** → `Build Sportzfy APK (EAS)` → **Run workflow** → choose `preview` → Run

### Step 3 — Download APK

After ~15-20 minutes, go to **Actions → your build → Artifacts** → download `sportzfy-apk.zip`

---

## 🏗️ Project Structure

```
sportzfy/
├── artifacts/
│   ├── sportzfy/            ← Web App (React + Vite)
│   ├── sportzfy-mobile/     ← Android App (Expo + React Native)
│   └── api-server/          ← Backend API (Express)
└── .github/
    └── workflows/
        └── eas-build-apk.yml  ← GitHub Actions APK build
```

---

## 📱 Mobile App Features

| Feature | Details |
|---------|---------|
| 🎬 **Video Player** | `expo-video` — uses **ExoPlayer** on Android |
| 📡 **HLS / DASH** | Native Android codec support via ExoPlayer |
| 📌 **Floating Player** | Draggable mini player — browse while watching |
| 🔒 **Lock Screen** | Lock player controls to prevent accidental taps |
| 📺 **Fullscreen** | Landscape mode with `expo-screen-orientation` |
| 🎯 **Quality Select** | Auto ABR + manual quality override |
| 🔴 **Live Events** | FIFA, Cricket, Football — ESPN API data |
| ⏰ **30s Polling** | Auto-refresh match scores every 30 seconds |
| 🌐 **30+ Streams** | FIFA+, beIN Sports, ESPN, TyC Sports, Cricket Gold… |

---

## 🌐 Web App Features

- SplashScreen with animations
- HLS.js / DASH.js / Shaka / Video.js / Plyr / DPlayer engines
- Live match cards with live clock
- Stream picker (30+ streams with quality badges)
- CORS proxy for HLS streams

---

## 🚀 Local Development

```bash
# Install dependencies
pnpm install

# Start all services
pnpm --filter @workspace/api-server run dev      # API on :3001
pnpm --filter @workspace/sportzfy run dev        # Web on :5173
pnpm --filter @workspace/sportzfy-mobile run dev # Expo
```

---

## 🔧 EAS Build Profiles

| Profile | Output | Use |
|---------|--------|-----|
| `preview` | **APK** | Testing on device |
| `production` | AAB | Play Store upload |
| `development` | APK | Development client |

```bash
# Build APK locally (needs Expo account)
npx eas-cli login
npx eas-cli build -p android --profile preview
```

---

## 📡 Live Streams Included

- **FIFA+**: English, USA, Español, Brasil, Deutschland, Argentina, Italia, France
- **beIN Sports**: 1 UHD, 1 Amagi, 2, 3, 4, Xtra
- **ESPN 2**, **TyC Sports**, **Euro TV**, **Cricket Gold**
- **Caze TV 4K**, **Telemundo**, **Win Sports**, and more…

---

*Built with ❤️ using Expo SDK 54 + ExoPlayer + React Native*
