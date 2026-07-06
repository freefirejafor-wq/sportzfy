# 📱 Sportzfy APK Build Guide

## ✅ Quick Steps to Get Your APK

### 1. GitHub-এ `EXPO_TOKEN` সিক্রেট যোগ করো

```
GitHub Repo → Settings → Secrets and variables → Actions → New repository secret
Name:  EXPO_TOKEN
Value: (expo.dev থেকে তোমার token)
```

**Expo Token কোথায় পাবে:**
1. [expo.dev](https://expo.dev) → Sign Up (ফ্রি)
2. Profile → Account Settings → Access Tokens
3. "Create Token" → Copy করো

### 2. APK Build ট্রিগার করো

**Option A — Auto (Push করলেই build হবে):**
```bash
git push origin main
```

**Option B — Manual:**
GitHub → Actions → "Build Sportzfy APK (EAS)" → "Run workflow" → "preview" → Run

### 3. APK Download করো

Actions → Build সম্পন্ন হলে → Artifacts section → `sportzfy-apk` download

---

## 📲 APK Install করো

1. Android ফোনে `sportzfy.apk` transfer করো
2. Settings → Unknown Sources → Allow
3. APK install করো
4. Sportzfy চালু করো — ExoPlayer দিয়ে HLS stream চলবে!

---

## 🔧 প্রয়োজনীয় Config

`artifacts/sportzfy-mobile/eas.json` এ সব profile আছে।
`app.json` এ package name: `com.sportzfy.app`

---

## ❓ সমস্যা হলে

- **EXPO_TOKEN error**: Token expire হয়ে থাকলে নতুন token তৈরি করো
- **Build fail**: Actions log দেখো, সাধারণত dependency issue
- **APK install হচ্ছে না**: Unknown sources allow করো
