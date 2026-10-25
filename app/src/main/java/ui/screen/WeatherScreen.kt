package com.merve.meteoraweather.ui.screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merve.meteoraweather.R
import java.text.Collator
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

// 5 günlük tahmin verisi için veri modeli
data class DailyForecast(val day: String, val temp: String, val condition: String)

// Şehir detay verisi için model (Animasyon türü eklendi)
data class CityWeatherInfo(
    val temp: String,
    val condition: String,
    val humidity: String,
    val wind: String,
    val pressure: String,
    val animationType: String, // "sun", "rain", veya "cloud"
    val forecasts: List<DailyForecast>
)

// Örnek şehir verisi. Uygulama açıldığında BİR KEZ oluşur (composable içinde değil, her çizimde yeniden kurulmaz).
// Gerçek API'ye geçince bu blok data katmanına (repository) taşınacak.
internal val cityDatabase: Map<String, CityWeatherInfo> = mapOf(
    "İstanbul" to CityWeatherInfo(
        temp = "19°",
        condition = "Kapalı / Bulutlu (Hissedilen 18°)",
        humidity = "%74",
        wind = "13.8 km/sa",
        pressure = "1015 hPa",
        animationType = "cloud",
        forecasts = listOf(
            DailyForecast("Pazartesi", "19° / 12°", "Bulutlu"),
            DailyForecast("Salı", "21° / 13°", "Güneşli"),
            DailyForecast("Çarşamba", "18° / 11°", "Yağmurlu"),
            DailyForecast("Perşembe", "20° / 12°", "Parçalı Bulutlu"),
            DailyForecast("Cuma", "22° / 14°", "Güneşli")
        )
    ),
    "Ankara" to CityWeatherInfo(
        temp = "16°",
        condition = "Parçalı Bulutlu (Hissedilen 15°)",
        humidity = "%65",
        wind = "10.2 km/sa",
        pressure = "1018 hPa",
        animationType = "cloud",
        forecasts = listOf(
            DailyForecast("Pazartesi", "16° / 8°", "Parçalı Bulutlu"),
            DailyForecast("Salı", "18° / 9°", "Güneşli"),
            DailyForecast("Çarşamba", "15° / 7°", "Yağmurlu"),
            DailyForecast("Perşembe", "17° / 8°", "Güneşli"),
            DailyForecast("Cuma", "19° / 10°", "Açık")
        )
    ),
    "İzmir" to CityWeatherInfo(
        temp = "24°",
        condition = "Güneşli (Hissedilen 23°)",
        humidity = "%50",
        wind = "15.0 km/sa",
        pressure = "1012 hPa",
        animationType = "sun",
        forecasts = listOf(
            DailyForecast("Pazartesi", "24° / 15°", "Güneşli"),
            DailyForecast("Salı", "26° / 16°", "Açık"),
            DailyForecast("Çarşamba", "23° / 14°", "Bulutlu"),
            DailyForecast("Perşembe", "25° / 15°", "Güneşli"),
            DailyForecast("Cuma", "27° / 17°", "Güneşli")
        )
    ),
    "Tekirdağ" to CityWeatherInfo(
        temp = "17°",
        condition = "Bulutlu (Hissedilen 16°)",
        humidity = "%72",
        wind = "18.0 km/sa",
        pressure = "1014 hPa",
        animationType = "cloud",
        forecasts = listOf(
            DailyForecast("Pazartesi", "17° / 10°", "Bulutlu"),
            DailyForecast("Salı", "19° / 11°", "Güneşli"),
            DailyForecast("Çarşamba", "16° / 9°", "Yağmurlu"),
            DailyForecast("Perşembe", "18° / 10°", "Parçalı Bulutlu"),
            DailyForecast("Cuma", "20° / 12°", "Güneşli")
        )
    ),
    "Bursa" to CityWeatherInfo(
        temp = "18°",
        condition = "Bulutlu (Hissedilen 17°)",
        humidity = "%70",
        wind = "8.5 km/sa",
        pressure = "1014 hPa",
        animationType = "rain",
        forecasts = listOf(
            DailyForecast("Pazartesi", "18° / 10°", "Bulutlu"),
            DailyForecast("Salı", "20° / 11°", "Güneşli"),
            DailyForecast("Çarşamba", "17° / 9°", "Yağmurlu"),
            DailyForecast("Perşembe", "19° / 10°", "Parçalı Bulutlu"),
            DailyForecast("Cuma", "21° / 12°", "Güneşli")
        )
    ),
    "Antalya" to CityWeatherInfo(
        temp = "27°",
        condition = "Açık / Güneşli (Hissedilen 26°)",
        humidity = "%45",
        wind = "12.0 km/sa",
        pressure = "1010 hPa",
        animationType = "sun",
        forecasts = listOf(
            DailyForecast("Pazartesi", "27° / 18°", "Açık"),
            DailyForecast("Salı", "28° / 19°", "Güneşli"),
            DailyForecast("Çarşamba", "26° / 17°", "Güneşli"),
            DailyForecast("Perşembe", "29° / 20°", "Açık"),
            DailyForecast("Cuma", "30° / 21°", "Güneşli")
        )
    ),
    "Trabzon" to CityWeatherInfo(
        temp = "17°",
        condition = "Yağmurlu (Hissedilen 16°)",
        humidity = "%80",
        wind = "14.0 km/sa",
        pressure = "1011 hPa",
        animationType = "rain",
        forecasts = listOf(
            DailyForecast("Pazartesi", "17° / 11°", "Yağmurlu"),
            DailyForecast("Salı", "18° / 12°", "Bulutlu"),
            DailyForecast("Çarşamba", "16° / 10°", "Yağmurlu"),
            DailyForecast("Perşembe", "19° / 12°", "Parçalı Bulutlu"),
            DailyForecast("Cuma", "20° / 13°", "Güneşli")
        )
    ),
    "Konya" to CityWeatherInfo(
        temp = "14°",
        condition = "Açık (Hissedilen 13°)",
        humidity = "%48",
        wind = "9.0 km/sa",
        pressure = "1020 hPa",
        animationType = "sun",
        forecasts = listOf(
            DailyForecast("Pazartesi", "14° / 5°", "Açık"),
            DailyForecast("Salı", "16° / 6°", "Güneşli"),
            DailyForecast("Çarşamba", "15° / 5°", "Parçalı Bulutlu"),
            DailyForecast("Perşembe", "17° / 7°", "Güneşli"),
            DailyForecast("Cuma", "18° / 8°", "Açık")
        )
    ),
    "Adana" to CityWeatherInfo(
        temp = "26°",
        condition = "Güneşli (Hissedilen 27°)",
        humidity = "%58",
        wind = "11.0 km/sa",
        pressure = "1011 hPa",
        animationType = "sun",
        forecasts = listOf(
            DailyForecast("Pazartesi", "26° / 17°", "Güneşli"),
            DailyForecast("Salı", "27° / 18°", "Açık"),
            DailyForecast("Çarşamba", "25° / 16°", "Parçalı Bulutlu"),
            DailyForecast("Perşembe", "28° / 18°", "Güneşli"),
            DailyForecast("Cuma", "29° / 19°", "Güneşli")
        )
    ),
    "Gaziantep" to CityWeatherInfo(
        temp = "20°",
        condition = "Parçalı Bulutlu (Hissedilen 19°)",
        humidity = "%52",
        wind = "10.5 km/sa",
        pressure = "1016 hPa",
        animationType = "cloud",
        forecasts = listOf(
            DailyForecast("Pazartesi", "20° / 9°", "Parçalı Bulutlu"),
            DailyForecast("Salı", "22° / 10°", "Güneşli"),
            DailyForecast("Çarşamba", "19° / 8°", "Bulutlu"),
            DailyForecast("Perşembe", "21° / 10°", "Güneşli"),
            DailyForecast("Cuma", "23° / 11°", "Açık")
        )
    ),
    "Samsun" to CityWeatherInfo(
        temp = "16°",
        condition = "Yağmurlu (Hissedilen 15°)",
        humidity = "%78",
        wind = "16.0 km/sa",
        pressure = "1013 hPa",
        animationType = "rain",
        forecasts = listOf(
            DailyForecast("Pazartesi", "16° / 11°", "Yağmurlu"),
            DailyForecast("Salı", "17° / 12°", "Bulutlu"),
            DailyForecast("Çarşamba", "15° / 10°", "Yağmurlu"),
            DailyForecast("Perşembe", "18° / 12°", "Parçalı Bulutlu"),
            DailyForecast("Cuma", "19° / 13°", "Güneşli")
        )
    ),
    "Eskişehir" to CityWeatherInfo(
        temp = "15°",
        condition = "Bulutlu (Hissedilen 14°)",
        humidity = "%62",
        wind = "12.0 km/sa",
        pressure = "1017 hPa",
        animationType = "cloud",
        forecasts = listOf(
            DailyForecast("Pazartesi", "15° / 6°", "Bulutlu"),
            DailyForecast("Salı", "17° / 7°", "Güneşli"),
            DailyForecast("Çarşamba", "14° / 5°", "Yağmurlu"),
            DailyForecast("Perşembe", "16° / 6°", "Parçalı Bulutlu"),
            DailyForecast("Cuma", "18° / 8°", "Güneşli")
        )
    )
)

// ---------- Ortak yardımcılar (ana sayfa ve detay sayfası birlikte kullanır) ----------

internal class AppPalette(
    val background: Color,
    val card: Color,
    val aiCard: Color,
    val text: Color,
    val secondary: Color,
    val accent: Color
)

// Tema Renk Paletleri (tek yerden)
internal fun appPalette(isDark: Boolean): AppPalette =
    if (isDark) {
        AppPalette(
            background = Color(0xFF101C35),
            card = Color(0xFF1A2A4A),
            aiCard = Color(0xFF162544),
            text = Color.White,
            secondary = Color.LightGray,
            accent = Color(0xFF64B5F6)
        )
    } else {
        AppPalette(
            background = Color(0xFFF0F4F8),
            card = Color(0xFFFFFFFF),
            aiCard = Color(0xFFE2E8F0),
            text = Color(0xFF1E293B),
            secondary = Color(0xFF64748B),
            accent = Color(0xFF0284C7)
        )
    }

private val trLocale: Locale = Locale.forLanguageTag("tr-TR")

// "izmir", "  ANKARA ", "afyon karahisar" -> "İzmir", "Ankara", "Afyon Karahisar"
internal fun toCityName(input: String): String =
    input.trim()
        .lowercase(trLocale)
        .split(" ")
        .filter { it.isNotBlank() }
        .joinToString(" ") { word ->
            word.replaceFirstChar { it.uppercase(trLocale) }
        }

// Türkiye'nin 81 ili (sadece ad listesi). Hava verisi ayrı tutulur (cityDatabase),
// verisi olmayan şehirler listede görünür ama detayda "Veri henüz yok" der.
internal val allCities: List<String> = listOf(
    "Adana",
    "Adıyaman",
    "Afyonkarahisar",
    "Ağrı",
    "Aksaray",
    "Amasya",
    "Ankara",
    "Antalya",
    "Ardahan",
    "Artvin",
    "Aydın",
    "Balıkesir",
    "Bartın",
    "Batman",
    "Bayburt",
    "Bilecik",
    "Bingöl",
    "Bitlis",
    "Bolu",
    "Burdur",
    "Bursa",
    "Çanakkale",
    "Çankırı",
    "Çorum",
    "Denizli",
    "Diyarbakır",
    "Düzce",
    "Edirne",
    "Elazığ",
    "Erzincan",
    "Erzurum",
    "Eskişehir",
    "Gaziantep",
    "Giresun",
    "Gümüşhane",
    "Hakkari",
    "Hatay",
    "Iğdır",
    "Isparta",
    "İstanbul",
    "İzmir",
    "Kahramanmaraş",
    "Karabük",
    "Karaman",
    "Kars",
    "Kastamonu",
    "Kayseri",
    "Kilis",
    "Kırıkkale",
    "Kırklareli",
    "Kırşehir",
    "Kocaeli",
    "Konya",
    "Kütahya",
    "Malatya",
    "Manisa",
    "Mardin",
    "Mersin",
    "Muğla",
    "Muş",
    "Nevşehir",
    "Niğde",
    "Ordu",
    "Osmaniye",
    "Rize",
    "Sakarya",
    "Samsun",
    "Şanlıurfa",
    "Siirt",
    "Sinop",
    "Sivas",
    "Şırnak",
    "Tekirdağ",
    "Tokat",
    "Trabzon",
    "Tunceli",
    "Uşak",
    "Van",
    "Yalova",
    "Yozgat",
    "Zonguldak"
).sortedWith(Collator.getInstance(trLocale))

// Arama için: küçük harf + Türkçe karakterleri sadeleştir ("canakkale" -> "Çanakkale" ile eşleşsin)
internal fun normalizeForSearch(text: String): String =
    text.trim().lowercase(trLocale)
        .replace('ç', 'c').replace('ğ', 'g').replace('ı', 'i')
        .replace('ö', 'o').replace('ş', 's').replace('ü', 'u').replace('â', 'a')

internal fun matchesQuery(cityName: String, query: String): Boolean =
    normalizeForSearch(cityName).startsWith(normalizeForSearch(query))

// Yazılan metni listedeki resmi şehir adına çevirir ("canakkale" -> "Çanakkale"), bulamazsa düzeltilmiş hâlini döner
internal fun resolveCityName(input: String): String {
    val typed = toCityName(input)
    return allCities.firstOrNull { normalizeForSearch(it) == normalizeForSearch(typed) } ?: typed
}

// Uygulamanın giriş noktası (MainActivity hâlâ WeatherScreen() çağırıyor, değişiklik gerekmiyor).
// Ana sayfa (şehir listesi) ile detay sayfası arasında geçişi yönetir.
@Composable
fun WeatherScreen() {
    // Tema iki sayfada ortak olduğu için burada tutulur
    var isDarkMode by rememberSaveable { mutableStateOf(true) }
    // null -> ana sayfa, dolu -> o şehrin detay sayfası
    var openedCity by rememberSaveable { mutableStateOf<String?>(null) }

    val city = openedCity
    if (city == null) {
        CityListScreen(
            isDarkMode = isDarkMode,
            onToggleTheme = { isDarkMode = !isDarkMode },
            onCityClick = { openedCity = it }
        )
    } else {
        // Sistem geri tuşu da detaydan ana sayfaya döndürür
        BackHandler { openedCity = null }
        WeatherDetailScreen(
            initialCity = city,
            isDarkMode = isDarkMode,
            onToggleTheme = { isDarkMode = !isDarkMode },
            onBack = { openedCity = null }
        )
    }
}

// Detay sayfası: önceki WeatherScreen'in aynısı (sadece başlangıç şehri, tema ve geri parametreleri eklendi)
@Composable
fun WeatherDetailScreen(
    initialCity: String,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // Tema renkleri (ortak paletten)
    val palette = appPalette(isDarkMode)
    val backgroundColor = palette.background
    val cardBackgroundColor = palette.card
    val aiCardBackgroundColor = palette.aiCard
    val textColor = palette.text
    val secondaryTextColor = palette.secondary
    val accentColor = palette.accent

    var selectedCity by rememberSaveable { mutableStateOf(initialCity) }

    val keyboardController = LocalSoftwareKeyboardController.current

    // Klavyeden Ara/Enter veya 🔍 ile şehri seç
    fun submitSearch() {
        val name = resolveCityName(searchQuery)
        if (name.isNotBlank()) {
            selectedCity = name
            searchQuery = ""
            keyboardController?.hide()
        }
    }

    val formattedQuery = toCityName(searchQuery)

    // Ekranda gösterilen şehir: sadece Ara'ya basınca veya karta tıklayınca değişir
    val activeCityName = selectedCity
    // Veritabanında yoksa null -> ekranda "Şehir bulunamadı" durumu gösterilir (sahte veri yok)
    val activeWeatherInfo: CityWeatherInfo? = cityDatabase[activeCityName]

    // Alttaki şehir listesi yazdıkça canlı filtrelenmeye devam eder
    val filteredCities = if (formattedQuery.isBlank()) {
        cityDatabase.keys.toList()
    } else {
        cityDatabase.keys.filter { matchesQuery(it, formattedQuery) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Ana sayfaya dönüş
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.back_to_cities),
                color = accentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(vertical = 8.dp, horizontal = 4.dp)
            )
        }

        // Üst Kısım: Arama Kutusu ve Tema Değiştirme Butonu Yan Yana
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(stringResource(R.string.search_placeholder), color = secondaryTextColor, fontSize = 13.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { submitSearch() }),
                trailingIcon = {
                    Text(
                        text = "🔍",
                        fontSize = 18.sp,
                        modifier = Modifier
                            .clickable { submitSearch() }
                            .padding(8.dp)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = cardBackgroundColor,
                    unfocusedContainerColor = cardBackgroundColor,
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                ),
                modifier = Modifier.weight(1f)
            )

            // Sağ üst köşe Tema Geçiş Butonu (☀️ / 🌙)
            Button(
                onClick = onToggleTheme,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = cardBackgroundColor),
                modifier = Modifier.height(56.dp).width(60.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (isDarkMode) "☀️" else "🌙",
                    fontSize = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Şehir Adı
        Text(
            text = activeCityName,
            color = textColor,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        if (activeWeatherInfo != null) {
            Spacer(modifier = Modifier.height(10.dp))

            // Hareketli Hava Durumu İkonu (internet gerektirmez)
            WeatherAnimatedIcon(animationType = activeWeatherInfo.animationType)

            Spacer(modifier = Modifier.height(8.dp))

            // Sıcaklık
            Text(
                text = activeWeatherInfo.temp,
                color = textColor,
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold
            )

            // Hava Durumu Açıklaması
            Text(
                text = activeWeatherInfo.condition,
                color = secondaryTextColor,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Detay Kartları (Nem, Rüzgar, Basınç)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailCard(title = stringResource(R.string.label_humidity), value = activeWeatherInfo.humidity, cardColor = cardBackgroundColor, textColor = textColor, secondaryColor = secondaryTextColor)
                WeatherDetailCard(title = stringResource(R.string.label_wind), value = activeWeatherInfo.wind, cardColor = cardBackgroundColor, textColor = textColor, secondaryColor = secondaryTextColor)
                WeatherDetailCard(title = stringResource(R.string.label_pressure), value = activeWeatherInfo.pressure, cardColor = cardBackgroundColor, textColor = textColor, secondaryColor = secondaryTextColor)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.ai_section_title),
                    color = accentColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            AIRecommendationCard(
                title = stringResource(R.string.ai_clothing_title),
                content = stringResource(R.string.ai_clothing_content, activeCityName),
                cardColor = aiCardBackgroundColor,
                textColor = textColor,
                accentColor = accentColor
            )

            Spacer(modifier = Modifier.height(10.dp))

            AIRecommendationCard(
                title = stringResource(R.string.ai_activity_title),
                content = stringResource(R.string.ai_activity_content, activeCityName),
                cardColor = aiCardBackgroundColor,
                textColor = textColor,
                accentColor = accentColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 5 Günlük Hava Durumu Tahmini
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.forecast_title, activeCityName),
                    color = accentColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    activeWeatherInfo.forecasts.forEach { forecast ->
                        DailyForecastRow(day = forecast.day, temp = forecast.temp, condition = forecast.condition, textColor = textColor, secondaryColor = secondaryTextColor, accentColor = accentColor)
                    }
                }
            }
        } else {
            // Verisi yok: şehir listede varsa "veri henüz yok", hiç yoksa "şehir bulunamadı"
            val hasNoData = activeCityName in allCities
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = if (hasNoData) "⏳" else "🔎", fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(if (hasNoData) R.string.city_no_data_title else R.string.city_not_found_title),
                        color = accentColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(if (hasNoData) R.string.city_no_data_message else R.string.city_not_found_message, activeCityName),
                        color = secondaryTextColor,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Filtrelenmiş Şehirler / Sık Bakılan Şehirler Bölümü
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (formattedQuery.isNotBlank()) stringResource(R.string.search_results_title) else stringResource(R.string.favorite_cities_title),
                color = accentColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (filteredCities.isEmpty()) {
            Text(
                text = stringResource(R.string.no_city_match),
                color = secondaryTextColor,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filteredCities.forEach { cityName ->
                    val cityInfo = cityDatabase[cityName]!!
                    FavoriteCityCard(
                        cityName = cityName,
                        temp = cityInfo.temp,
                        condition = cityInfo.condition.substringBefore(" ("),
                        cardColor = cardBackgroundColor,
                        textColor = textColor,
                        secondaryColor = secondaryTextColor,
                        accentColor = accentColor
                    ) { clickedCity ->
                        Log.d("Hava", "Tıklandı: $clickedCity")
                        searchQuery = "" // Tıklanınca aramayı sıfırla ve seçilen şehre odaklan
                        selectedCity = clickedCity
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

// Compose Canvas ile çizilen, internet gerektirmeyen animasyonlu hava durumu ikonu
@Composable
fun WeatherAnimatedIcon(animationType: String, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "weatherIcon")

    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing)),
        label = "rotation"
    )
    val drift by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "drift"
    )
    val fall by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing)),
        label = "fall"
    )

    Canvas(modifier = modifier.size(130.dp)) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        when (animationType) {
            "sun" -> drawSun(cx, cy, size.minDimension * 0.22f, rotation)
            "rain" -> {
                drawCloud(cx + drift * size.width * 0.03f, cy - size.height * 0.12f, size.width * 0.75f, Color(0xFF90A4AE))
                drawRain(cx, cy + size.height * 0.08f, size.width, size.height, fall)
            }
            else -> {
                // Arkadaki küçük bulut
                drawCloud(cx - drift * size.width * 0.06f + size.width * 0.12f, cy - size.height * 0.18f, size.width * 0.5f, Color(0xFFCFD8DC))
                // Öndeki büyük bulut
                drawCloud(cx + drift * size.width * 0.05f - size.width * 0.04f, cy + size.height * 0.05f, size.width * 0.78f, Color(0xFFB0BEC5))
            }
        }
    }
}

private fun DrawScope.drawSun(cx: Float, cy: Float, radius: Float, rotation: Float) {
    val sunColor = Color(0xFFFFC107)
    // Dış hale
    drawCircle(color = sunColor.copy(alpha = 0.25f), radius = radius * 1.5f, center = Offset(cx, cy))
    // Dönen ışınlar
    for (i in 0 until 8) {
        val angle = Math.toRadians((rotation + i * 45f).toDouble())
        val startR = radius * 1.35f
        val endR = radius * 1.9f
        drawLine(
            color = sunColor,
            start = Offset(cx + (cos(angle) * startR).toFloat(), cy + (sin(angle) * startR).toFloat()),
            end = Offset(cx + (cos(angle) * endR).toFloat(), cy + (sin(angle) * endR).toFloat()),
            strokeWidth = radius * 0.22f,
            cap = StrokeCap.Round
        )
    }
    // Güneşin gövdesi
    drawCircle(color = sunColor, radius = radius, center = Offset(cx, cy))
}

private fun DrawScope.drawCloud(cx: Float, cy: Float, w: Float, color: Color) {
    val h = w * 0.5f
    drawCircle(color, radius = h * 0.55f, center = Offset(cx - w * 0.22f, cy + h * 0.1f))
    drawCircle(color, radius = h * 0.75f, center = Offset(cx + w * 0.02f, cy - h * 0.1f))
    drawCircle(color, radius = h * 0.55f, center = Offset(cx + w * 0.27f, cy + h * 0.12f))
    drawRoundRect(
        color = color,
        topLeft = Offset(cx - w * 0.35f, cy + h * 0.1f),
        size = Size(w * 0.7f, h * 0.55f),
        cornerRadius = CornerRadius(h * 0.25f, h * 0.25f)
    )
}

private fun DrawScope.drawRain(cx: Float, topY: Float, w: Float, h: Float, progress: Float) {
    val dropColor = Color(0xFF4FC3F7)
    val fallDistance = h * 0.32f
    for (i in 0 until 4) {
        val p = (progress + i * 0.25f) % 1f
        val x = cx - w * 0.21f + i * (w * 0.14f)
        val y = topY + p * fallDistance
        drawLine(
            color = dropColor.copy(alpha = 1f - p),
            start = Offset(x, y),
            end = Offset(x - w * 0.03f, y + h * 0.08f),
            strokeWidth = w * 0.03f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun WeatherDetailCard(title: String, value: String, cardColor: Color, textColor: Color, secondaryColor: Color) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier.width(105.dp).height(80.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, color = secondaryColor, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AIRecommendationCard(title: String, content: String, cardColor: Color, textColor: Color, accentColor: Color) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Text(text = title, color = accentColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = content, color = textColor.copy(alpha = 0.9f), fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
fun DailyForecastRow(day: String, temp: String, condition: String, textColor: Color, secondaryColor: Color, accentColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = day, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Text(text = condition, color = secondaryColor, fontSize = 12.sp)
        Text(text = temp, color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FavoriteCityCard(
    cityName: String,
    temp: String,
    condition: String,
    cardColor: Color,
    textColor: Color,
    secondaryColor: Color,
    accentColor: Color,
    onClick: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier
            .width(130.dp)
            .height(90.dp)
            .clickable { onClick(cityName) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = cityName, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = temp, color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Text(text = condition, color = secondaryColor, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}