# 🌦️ Hava Durumu (MeteoraWeather)

Turkcell Bilişim Kampı (Android) kapsamında geliştirilmiş; **Jetpack Compose** ile yazılmış, **Yapay Zeka (AI)** ve hava durumu katmanlarının modüler olarak ayrıldığı modern bir hava durumu uygulamasıdır. Kullanıcı şehir arayabilir, favorilerini kaydedebilir, bulunduğun yerin ve herhangi bir şehrin saatlik/günlük tahminini görebilir.

---

## 📱 <img width="346" height="788" alt="image" src="https://github.com/user-attachments/assets/85fdb812-d424-4dd8-8835-a798656b1c09" />
<img width="350" height="787" alt="image" src="https://github.com/user-attachments/assets/de5df869-0672-4f7a-8e4f-949eb594ce80" />
<img width="342" height="788" alt="image" src="https://github.com/user-attachments/assets/8e23a63f-87a8-434e-bf72-e0cf5619ae4a" />
<img width="338" height="793" alt="image" src="https://github.com/user-attachments/assets/a077f888-c966-4587-8b85-dee93ee23e49" />


| Ana Ekran | Arama | Şehir Detayı |
| :---: | :---: | :---: |
| ![Ana Ekran](docs/screenshots/ana_ekran.png) | ![Arama](docs/screenshots/arama.png) | ![Şehir Detayı](docs/screenshots/sehir_detayi.png) |

---

## ✨ Özellikler

* **Yazdıkça Arama:** 400 ms debounce ile 1-2 harfte 81 il içinde anlık filtreleme.
* **Modüler Mimari:** `feature-ai` ve `feature-weather` paketleri ile yapay zeka ve hava durumu katmanlarının birbirinden tamamen ayrılması.
* **Favoriler & Konum:** Favori şehirleri kaydetme ve detaylı saatlik/günlük tahmin ekranları.
* **Modern State Yönetimi:** `StateFlow` ve `rememberSaveable` ile güvenli state akışı ve ekran döndürme dayanıklılığı.

---

## 🏗️ Mimari ve Katmanlı Yapı

Proje, çoklu modül ve Clean Architecture prensipleri benimsenerek katmanlarına ayrılmıştır:

```text
MeteoraWeather/
│
├── app/                    # Ana uygulama modülü ve navigation
├── feature-weather/        # Hava durumu servisleri, repository ve UI bileşenleri
└── feature-ai/             # Yapay zeka entegrasyonu ve akıllı öneri katmanı
