package com.merve.meteoraweather.ui.screen

import android.util.Log
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun WeatherScreen() {
    val scrollState = rememberScrollState()
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // Tema Durumu: true ise Dark Mod, false ise Light Mod
    var isDarkMode by rememberSaveable { mutableStateOf(true) }

    // Tema Renk Paletleri
    val backgroundColor = if (isDarkMode) Color(0xFF101C35) else Color(0xFFF0F4F8)
    val cardBackgroundColor = if (isDarkMode) Color(0xFF1A2A4A) else Color(0xFFFFFFFF)
    val aiCardBackgroundColor = if (isDarkMode) Color(0xFF162544) else Color(0xFFE2E8F0)
    val textColor = if (isDarkMode) Color.White else Color(0xFF1E293B)
    val secondaryTextColor = if (isDarkMode) Color.LightGray else Color(0xFF64748B)
    val accentColor = if (isDarkMode) Color(0xFF64B5F6) else Color(0xFF0284C7)

    // Şehirlere özel detaylı hava durumu, animasyon türü ve 5 günlük tahmin veritabanı
    val cityDatabase = mapOf(
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

    var selectedCity by rememberSaveable { mutableStateOf("İstanbul") }

    val trLocale = remember { Locale.forLanguageTag("tr-TR") }
    val keyboardController = LocalSoftwareKeyboardController.current

    // "izmir", "  ANKARA ", "afyon karahisar" -> "İzmir", "Ankara", "Afyon Karahisar"
    fun toCityName(input: String): String =
        input.trim()
            .lowercase(trLocale)
            .split(" ")
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                word.replaceFirstChar { it.uppercase(trLocale) }
            }

    // Klavyeden Ara/Enter veya 🔍 ile şehri seç
    fun submitSearch() {
        val name = toCityName(searchQuery)
        if (name.isNotBlank()) {
            selectedCity = name
            searchQuery = ""
            keyboardController?.hide()
        }
    }

    val formattedQuery = toCityName(searchQuery)

    // Ekranda gösterilen şehir: sadece Ara'ya basınca veya karta tıklayınca değişir
    val activeCityName = selectedCity
    val isKnownCity = cityDatabase.containsKey(activeCityName)

    val activeWeatherInfo = cityDatabase[activeCityName] ?: CityWeatherInfo(
        temp = "21°",
        condition = "Parçalı Bulutlu (Hissedilen 20°)",
        humidity = "%60",
        wind = "11.5 km/sa",
        pressure = "1016 hPa",
        animationType = "sun",
        forecasts = listOf(
            DailyForecast("Pazartesi", "21° / 13°", "Parçalı Bulutlu"),
            DailyForecast("Salı", "23° / 14°", "Güneşli"),
            DailyForecast("Çarşamba", "20° / 12°", "Açık"),
            DailyForecast("Perşembe", "22° / 13°", "Güneşli"),
            DailyForecast("Cuma", "24° / 15°", "Güneşli")
        )
    )

    // Alttaki şehir listesi yazdıkça canlı filtrelenmeye devam eder
    val filteredCities = if (formattedQuery.isBlank()) {
        cityDatabase.keys.toList()
    } else {
        cityDatabase.keys.filter { it.startsWith(formattedQuery, ignoreCase = true) }
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

        // Üst Kısım: Arama Kutusu ve Tema Değiştirme Butonu Yan Yana
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Şehir adı yazın ve arayın...", color = secondaryTextColor, fontSize = 13.sp) },
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
                onClick = { isDarkMode = !isDarkMode },
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

        // Veritabanında olmayan şehirler için bilgi notu
        if (!isKnownCity) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "ℹ️ Bu şehir için örnek veri gösteriliyor",
                color = secondaryTextColor,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Detay Kartları (Nem, Rüzgar, Basınç)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailCard(title = "Nem", value = activeWeatherInfo.humidity, cardColor = cardBackgroundColor, textColor = textColor, secondaryColor = secondaryTextColor)
            WeatherDetailCard(title = "Rüzgar", value = activeWeatherInfo.wind, cardColor = cardBackgroundColor, textColor = textColor, secondaryColor = secondaryTextColor)
            WeatherDetailCard(title = "Basınç", value = activeWeatherInfo.pressure, cardColor = cardBackgroundColor, textColor = textColor, secondaryColor = secondaryTextColor)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "✨ Yapay Zeka Asistanı Önerileri",
                color = accentColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        AIRecommendationCard(
            title = "🧥 Kıyafet Önerisi",
            content = "$activeCityName için anlık hava koşullarına uygun katmanlı giysiler ve mevsimlik dış giyim tercih edebilirsin.",
            cardColor = aiCardBackgroundColor,
            textColor = textColor,
            accentColor = accentColor
        )

        Spacer(modifier = Modifier.height(10.dp))

        AIRecommendationCard(
            title = "🚶‍♂️ Aktivite Önerisi",
            content = "$activeCityName genelinde dışarıda vakit geçirmek ve plan yapmak için havanın durumunu değerlendirebilirsin.",
            cardColor = aiCardBackgroundColor,
            textColor = textColor,
            accentColor = accentColor
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 5 Günlük Hava Durumu Tahmini
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "📅 5 Günlük Hava Durumu ($activeCityName)",
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

        Spacer(modifier = Modifier.height(24.dp))

        // Filtrelenmiş Şehirler / Sık Bakılan Şehirler Bölümü
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (formattedQuery.isNotBlank()) "🔍 Arama Sonuçları" else "📍 Sık Bakılan Şehirler",
                color = accentColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (filteredCities.isEmpty()) {
            Text(
                text = "Aradığınız kritere uygun şehir bulunamadı.",
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