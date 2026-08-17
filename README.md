# PR•Game Booster

اپلیکیشن Native Android (Kotlin + Jetpack Compose) برای مدیریت و نمایش وضعیت واقعی
دستگاه هنگام بازی‌های PUBG Mobile، Free Fire و Call of Duty Mobile.

## اصل بنیادین پروژه

**هیچ مقدار جعلی، Random یا Hard-coded به‌عنوان وضعیت واقعی دستگاه نمایش داده نمی‌شود.**
هر عددی (باتری، شبکه، تأخیر) مستقیماً از API واقعی اندروید خوانده می‌شود. اگر قابلیتی
(مثل FPS واقعی بازی یا Overclock سیستمی) بدون Root/دسترسی سیستمی ممکن نباشد، رابط
کاربری این را صادقانه با پیام «در دسترس نیست» نشان می‌دهد، نه با یک عدد ساختگی.

## ساختار پروژه

```
app/src/main/java/com/prgamebooster/
├── domain/         مدل‌های داده و اینترفیس‌های Repository (مستقل از Android)
├── data/           پیاده‌سازی واقعی Repositoryها با Jetpack DataStore
├── performance/     BatteryMonitor / NetworkMonitor / LatencyMonitor / PerformanceManager واقعی
├── games/          GameLauncher واقعی با PackageManager + Intent
├── overlay/        سرویس‌های Overlay واقعی (پنل شناور، Crosshair) با WindowManager
├── di/             ماژول‌های Hilt
├── navigation/      Navigation Compose + Bottom Navigation
└── presentation/    صفحات Compose (خانه، مانیتور، ابزارها، تنظیمات) + ViewModelها
```

## پیش‌نیازها برای Build

- Android Studio (Koala یا جدیدتر)
- JDK 17
- Android SDK با Platform 34 نصب‌شده
- اتصال اینترنت برای دانلود اولیه وابستگی‌های Gradle (این پروژه در محیطی بدون
  دسترسی شبکه ساخته شده و به همین دلیل هرگز واقعاً `./gradlew assembleDebug`
  در آن محیط اجرا نشده است — نتیجه Build را حتماً خودتان روی سیستم با SDK کامل
  تأیید کنید)

## Build کردن APK

```bash
git clone <این پروژه> pr-game-booster
cd pr-game-booster
./gradlew assembleDebug
```

فایل خروجی در مسیر زیر قرار می‌گیرد:

```
app/build/outputs/apk/debug/app-debug.apk
```

برای نسخه Release (نیازمند keystore امضا):

```bash
./gradlew assembleRelease
```

## اجرای تست‌ها

```bash
./gradlew test
```

## قابلیت‌های واقعی پیاده‌سازی‌شده

| قابلیت | منبع واقعی داده |
|---|---|
| باتری و وضعیت شارژ | `android.os.BatteryManager` + `ACTION_BATTERY_CHANGED` |
| نوع اتصال شبکه (Wi-Fi/4G/5G) | `ConnectivityManager` + `NetworkCapabilities` |
| سرعت لینک شبکه | `NetworkCapabilities.getLinkDownstreamBandwidthKbps()` |
| تست تأخیر (Ping) | اتصال TCP واقعی + اندازه‌گیری زمان با `System.nanoTime()` |
| بازخورد لرزشی | `Vibrator` / `VibratorManager` |
| اجرای بازی | `PackageManager` + `Intent` |
| پنل شناور و نشانه‌گر | `WindowManager` + `SYSTEM_ALERT_WINDOW` |
| ذخیره‌سازی تنظیمات و پروفایل هر بازی | Jetpack `DataStore` (Preferences) |
| جلوگیری از خواب صفحه حین بازی | `PowerManager.WakeLock` |

## محدودیت‌های صادقانه (طبق طراحی، نه حذف اشتباه)

این قابلیت‌ها **عمداً** پیاده‌سازی نشده‌اند چون بدون Root یا دسترسی
Privileged سیستم برای یک اپ معمولی اندروید ممکن نیستند؛ به‌جای شبیه‌سازی، در UI
با پیام «در دسترس نیست» یا از طریق برچسب‌گذاری دقیق («هدف FPS» به‌جای «FPS واقعی»)
نمایش داده می‌شوند:

- خواندن یا تغییر واقعی FPS بازی از داخل خود بازی
- Overclock واقعی CPU/GPU
- تغییر Governor پردازنده یا فرکانس GPU
- RAM Cleaning مستقیم حافظه بازی

## وضعیت این تحویل

این پروژه در یک محیط توسعه بدون دسترسی به اینترنت و بدون Android SDK نصب‌شده
تولید شده است، بنابراین **کامپایل و تولید APK واقعی روی همین سیستم انجام نشده**.
تمام فایل‌های Gradle/Manifest/Kotlin/Compose به شکل واقعی و مطابق APIهای رسمی
اندروید نوشته شده‌اند و برای Build باید در Android Studio با SDK کامل باز و اجرا شوند.
پیش از تحویل نهایی توصیه می‌شود `./gradlew assembleDebug` و `./gradlew test` را
روی محیط خودتان اجرا و هر خطای احتمالی وابستگی نسخه را برطرف کنید.
