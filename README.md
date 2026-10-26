# 🌤️ MeteoraWeather — Türkiye Hava Durumu

Jetpack Compose ile yazılmış, Türkiye'nin 81 ili için hava durumu uygulaması. KAMP+ Bilişim Kampı Android eğitimi kapsamında geliştirildi.

> **Durum:** Arayüz katmanı tamamlandı. Hava durumu verileri şu an **örnek (sabit) veridir**, gerçek API bağlantısı yol haritasındadır. Bkz. [Yol haritası](#️-yol-haritası).

| Ana sayfa | Hava detayı | Açık tema |
|---|---|---|
<img width="339" height="791" alt="Ekran Resmi 2026-10-26 09 30 12" src="https://github.com/user-attachments/assets/53f7db65-a965-4fe9-bcee-27e924c8265d" />
<img width="344" height="791" alt="image" src="https://github.com/user-attachments/assets/2b90bd09-1c90-4b08-b7f8-cce46119cb34" />
<img width="341" height="786" alt="image" src="https://github.com/user-attachments/assets/34b61b0b-30a5-4705-8c5e-a5338538d826" />
<img width="342" height="789" alt="image" src="https://github.com/user-attachments/assets/00420fbe-78d3-4795-8e26-ab8d48dfe3fe" />





## ✨ Özellikler

| Özellik | Açıklama |
|---|---|
| 🏙️ **81 il listesi** | Ana sayfada tüm iller Türkçe alfabe sırasıyla, `LazyColumn` ile listelenir |
| 🔍 **Şehir arama** | Yazdıkça liste süzülür. Klavyedeki "ara" tuşu ve 🔍 simgesi çalışır. Tek şehir kalınca kart vurgulanır ve Enter ile açılır |
| 🇹🇷 **Türkçe karakter desteği** | `izmir` → İzmir, `canakkale` → Çanakkale, `sanliurfa` → Şanlıurfa. Türkçe `İ/I` dönüşüm hatası giderilmiştir |
| 🌡️ **Anlık durum** | Sıcaklık, durum açıklaması, hissedilen sıcaklık, nem, rüzgâr, basınç |
| 📅 **5 günlük tahmin** | Günlük en yüksek / en düşük sıcaklık ve hava durumu |
| 🌤️ **Animasyonlu hava ikonları** | Dönen ışınlı güneş, süzülen bulutlar, yağmur damlalı bulut. İnternet gerektirmez, Compose `Canvas` ile çizilir |
| ✨ **Yapay zeka önerileri** | Kıyafet ve aktivite öneri kartları (şu an şehre göre şablon metin) |
| 🌙 **Açık / koyu tema** | Tek butonla değişir, ana sayfa ve detay sayfasında ortaktır |
| 📍 **Sık bakılan şehirler** | Detay sayfasında yana kaydırılan hızlı şehir şeridi |
| 🔄 **Ekran döndürme** | Seçili sayfa, arama metni ve tema `rememberSaveable` ile korunur |
| ↩️ **Geri tuşu** | Sistem geri tuşu ve "← Şehirler" bağlantısı detaydan ana sayfaya döner |
| 🧩 **Üç boş durum** | Veri var · "Veri henüz yok" (listede olan ama verisi eklenmemiş şehir) · "Şehir bulunamadı" |

---

## 🧱 Mimari

### Şu anki yapı

```
MainActivity
   └── WeatherScreen()            ← giriş noktası, sayfalar arası geçişi yönetir
         ├── CityListScreen()     ← ana sayfa: arama + LazyColumn şehir listesi
         └── WeatherDetailScreen()← detay: hava durumu, öneriler, tahmin
```

- **Tek giriş noktası:** `WeatherScreen()` hangi sayfanın açık olduğunu (`openedCity`) ve ortak temayı (`isDarkMode`) tutar.
- **State hoisting:** Kart bileşenleri (`FavoriteCityCard`, `WeatherDetailCard`, `AIRecommendationCard`, `DailyForecastRow`) durum tutmaz. Bilgiyi parametreyle alır, tıklamayı `onClick` ile yukarı bildirir.
- **Tek renk paleti:** `appPalette(isDark)` iki sayfanın renklerini tek yerden üretir.
- **Veri ayrımı:** Şehir **adları** (`allCities`, 81 il) ile hava **verisi** (`cityDatabase`) ayrı tutulur. Böylece listedeki her şehir görünür, verisi olmayanlar dürüstçe "Veri yakında" der.
- **Metinler:** Arayüz metinleri `strings.xml` içindedir.

### Hedef mimari

```
UI (Composable)  →  ViewModel (StateFlow)  →  Repository  →  Retrofit  →  Open-Meteo
```

| Katman | Sorumluluk |
|---|---|
| **UI** | Durumu çizer, kullanıcı olaylarını ViewModel'e iletir |
| **ViewModel** | Arama, seçili şehir ve `UiState` (Loading / Success / Empty / Error) tutar |
| **Repository** | Veri kaynağını soyutlar, API yanıtını uygulama modeline çevirir |
| **Remote** | Retrofit arayüzü ve JSON modelleri |

---

## 🛠️ Teknoloji

Kotlin · Jetpack Compose (Material 3) · Compose `Canvas` ve `rememberInfiniteTransition` · `LazyColumn` · `rememberSaveable` · `BackHandler`

Yol haritasındakiler: ViewModel + StateFlow · Coroutines · Retrofit 2 + Gson · DataStore

---

## 🔬 Teknik notlar

- **Türkçe harf dönüşümü:** `"izmir".replaceFirstChar { it.uppercase() }` sonucu "Izmir" olur, çünkü varsayılan locale Türkçe değildir. `Locale.forLanguageTag("tr-TR")` ile "İzmir" elde edilir.
- **Aksansız arama:** `normalizeForSearch()` metni küçük harfe çevirip `ç ğ ı ö ş ü â` karakterlerini sadeleştirir. İki taraf da aynı kuralla karşılaştırıldığı için "canakkale" ile "Çanakkale" eşleşir.
- **Alfabetik sıralama:** `Collator.getInstance(trLocale)` ile Ç, Ğ, İ, Ö, Ş, Ü doğru yere dizilir.
- **Hafif animasyon:** Hava ikonları üç `animateFloat` değerinden (dönme, süzülme, düşme) beslenir. Harici animasyon dosyası ya da kütüphane yoktur.
- **Sahte veri üretilmez:** Veritabanında olmayan bir şehir için rastgele hava durumu gösterilmez, açık bir boş durum gösterilir.

---

## 🚀 Çalıştırma

1. Repoyu klonla:
   ```bash
   git clone https://github.com/MerveTatvan/MeteoraWeather.git
   ```
2. Android Studio ile aç ve Gradle senkronizasyonunun bitmesini bekle.
3. Emülatör veya gerçek cihazda **Run ▶** de.

Şu an ağ bağlantısı gerekmez, tüm veri uygulama içindedir.

---

## 📁 Proje yapısı

```
app/src/main/
├── AndroidManifest.xml
├── java/com/merve/meteoraweather/
│   ├── MainActivity.kt
│   └── ui/screen/
│       ├── WeatherScreen.kt       Giriş noktası, detay sayfası, ortak yardımcılar, örnek veri
│       └── CityListScreen.kt      Ana sayfa (arama + şehir listesi)
└── res/values/
    └── strings.xml                Arayüz metinleri
docs/
└── screenshots/                   README görselleri
```

---

## 👩‍💻 Hazırlayan

**Merve Tatvan** · KAMP+ Bilişim Kampı
