package com.merve.meteoraweather.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 5 günlük tahmin verisi için veri modeli
data class DailyForecast(val day: String, val temp: String, val condition: String)

// Şehir detay verisi için model
data class CityWeatherInfo(
    val temp: String,
    val condition: String,
    val humidity: String,
    val wind: String,
    val pressure: String,
    val forecasts: List<DailyForecast>
)

@Composable
fun WeatherScreen() {
    val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }

    // Tema Durumu: true ise Dark Mod, false ise Light Mod
    var isDarkMode by remember { mutableStateOf(true) }

    // Tema Renk Paletleri
    val backgroundColor = if (isDarkMode) Color(0xFF101C35) else Color(0xFFF0F4F8)
    val cardBackgroundColor = if (isDarkMode) Color(0xFF1A2A4A) else Color(0xFFFFFFFF)
    val aiCardBackgroundColor = if (isDarkMode) Color(0xFF162544) else Color(0xFFE2E8F0)
    val textColor = if (isDarkMode) Color.White else Color(0xFF1E293B)
    val secondaryTextColor = if (isDarkMode) Color.LightGray else Color(0xFF64748B)
    val accentColor = if (isDarkMode) Color(0xFF64B5F6) else Color(0xFF0284C7)

    // Şehirlere özel detaylı hava durumu ve 5 günlük tahmin veritabanı
    val cityDatabase = mapOf(
        "İstanbul" to CityWeatherInfo(
            temp = "19°",
            condition = "Kapalı / Bulutlu (Hissedilen 18°)",
            humidity = "%74",
            wind = "13.8 km/sa",
            pressure = "1015 hPa",
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
            forecasts = listOf(
                DailyForecast("Pazartesi", "24° / 15°", "Güneşli"),
                DailyForecast("Salı", "26° / 16°", "Açık"),
                DailyForecast("Çarşamba", "23° / 14°", "Bulutlu"),
                DailyForecast("Perşembe", "25° / 15°", "Güneşli"),
                DailyForecast("Cuma", "27° / 17°", "Güneşli")
            )
        ),
        "Bursa" to CityWeatherInfo(
            temp = "18°",
            condition = "Bulutlu (Hissedilen 17°)",
            humidity = "%70",
            wind = "8.5 km/sa",
            pressure = "1014 hPa",
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
            forecasts = listOf(
                DailyForecast("Pazartesi", "27° / 18°", "Açık"),
                DailyForecast("Salı", "28° / 19°", "Güneşli"),
                DailyForecast("Çarşamba", "26° / 17°", "Güneşli"),
                DailyForecast("Perşembe", "29° / 20°", "Açık"),
                DailyForecast("Cuma", "30° / 21°", "Güneşli")
            )
        )
    )

    var selectedCity by remember { mutableStateOf("İstanbul") }

    val trimmedQuery = searchQuery.trim()
    val activeCityName = if (trimmedQuery.isNotBlank()) {
        trimmedQuery.replaceFirstChar { it.uppercase() }
    } else {
        selectedCity
    }

    val activeWeatherInfo = if (cityDatabase.containsKey(activeCityName)) {
        cityDatabase[activeCityName]!!
    } else if (trimmedQuery.isNotBlank()) {
        CityWeatherInfo(
            temp = "21°",
            condition = "Parçalı Bulutlu (Hissedilen 20°)",
            humidity = "%60",
            wind = "11.5 km/sa",
            pressure = "1016 hPa",
            forecasts = listOf(
                DailyForecast("Pazartesi", "21° / 13°", "Parçalı Bulutlu"),
                DailyForecast("Salı", "23° / 14°", "Güneşli"),
                DailyForecast("Çarşamba", "20° / 12°", "Açık"),
                DailyForecast("Perşembe", "22° / 13°", "Güneşli"),
                DailyForecast("Cuma", "24° / 15°", "Güneşli")
            )
        )
    } else {
        cityDatabase[selectedCity]!!
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
                placeholder = { Text("Şehir adı yazın...", color = secondaryTextColor, fontSize = 13.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
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

        Spacer(modifier = Modifier.height(24.dp))

        // Şehir Adı
        Text(
            text = activeCityName,
            color = textColor,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

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

        // Sık Bakılan Şehirler
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "📍 Sık Bakılan Şehirler",
                color = accentColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FavoriteCityCard(cityName = "İstanbul", temp = "19°", condition = "Bulutlu", cardColor = cardBackgroundColor, textColor = textColor, secondaryColor = secondaryTextColor, accentColor = accentColor) {
                searchQuery = ""; selectedCity = it
            }
            FavoriteCityCard(cityName = "Ankara", temp = "16°", condition = "Parçalı Bulutlu", cardColor = cardBackgroundColor, textColor = textColor, secondaryColor = secondaryTextColor, accentColor = accentColor) {
                searchQuery = ""; selectedCity = it
            }
            FavoriteCityCard(cityName = "İzmir", temp = "24°", condition = "Güneşli", cardColor = cardBackgroundColor, textColor = textColor, secondaryColor = secondaryTextColor, accentColor = accentColor) {
                searchQuery = ""; selectedCity = it
            }
            FavoriteCityCard(cityName = "Bursa", temp = "18°", condition = "Bulutlu", cardColor = cardBackgroundColor, textColor = textColor, secondaryColor = secondaryTextColor, accentColor = accentColor) {
                searchQuery = ""; selectedCity = it
            }
            FavoriteCityCard(cityName = "Antalya", temp = "27°", condition = "Açık", cardColor = cardBackgroundColor, textColor = textColor, secondaryColor = secondaryTextColor, accentColor = accentColor) {
                searchQuery = ""; selectedCity = it
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
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
            .width(120.dp)
            .height(90.dp)
            .clickable { onClick(cityName) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = cityName, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = temp, color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Text(text = condition, color = secondaryColor, fontSize = 11.sp)
        }
    }
}