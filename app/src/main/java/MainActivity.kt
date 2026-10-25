package com.merve.meteoraweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merve.meteoraweather.ui.theme.MeteoraWeatherTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

private val Navy = Color(0xFF101C35)
private val CardBlue = Color(0xFF243451)
private val PaleBlue = Color(0xFFC6D5EC)
private val SunYellow = Color(0xFFFFD477)

private val provinces = listOf(
    "Adana", "Adıyaman", "Afyonkarahisar", "Ağrı", "Amasya",
    "Ankara", "Antalya", "Artvin", "Aydın", "Balıkesir",
    "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur",
    "Bursa", "Çanakkale", "Çankırı", "Çorum", "Denizli",
    "Diyarbakır", "Edirne", "Elazığ", "Erzincan", "Erzurum",
    "Eskişehir", "Gaziantep", "Giresun", "Gümüşhane", "Hakkari",
    "Hatay", "Isparta", "Mersin", "İstanbul", "İzmir",
    "Kars", "Kastamonu", "Kayseri", "Kırklareli", "Kırşehir",
    "Kocaeli", "Konya", "Kütahya", "Malatya", "Manisa",
    "Kahramanmaraş", "Mardin", "Muğla", "Muş", "Nevşehir",
    "Niğde", "Ordu", "Rize", "Sakarya", "Samsun",
    "Siirt", "Sinop", "Sivas", "Tekirdağ", "Tokat",
    "Trabzon", "Tunceli", "Şanlıurfa", "Uşak", "Van",
    "Yozgat", "Zonguldak", "Aksaray", "Bayburt", "Karaman",
    "Kırıkkale", "Batman", "Şırnak", "Bartın", "Ardahan",
    "Iğdır", "Yalova", "Karabük", "Kilis", "Osmaniye",
    "Düzce"
)

data class ForecastDay(
    val date: String,
    val description: String,
    val max: Int,
    val min: Int
)

data class WeatherData(
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val wind: Double,
    val description: String,
    val days: List<ForecastDay>
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.d("MeteoraLifecycle", "onCreate çağrıldı")

        window.statusBarColor = android.graphics.Color.rgb(16, 28, 53)
        window.navigationBarColor = android.graphics.Color.rgb(16, 28, 53)

        setContent {
            MeteoraWeatherTheme {
                WeatherScreen()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        android.util.Log.d("MeteoraLifecycle", "onStart çağrıldı")
    }

    override fun onResume() {
        super.onResume()
        android.util.Log.d("MeteoraLifecycle", "onResume çağrıldı")
    }

    override fun onPause() {
        super.onPause()
        android.util.Log.d("MeteoraLifecycle", "onPause çağrıldı")
    }

    override fun onStop() {
        super.onStop()
        android.util.Log.d("MeteoraLifecycle", "onStop çağrıldı")
    }
}

@Composable
fun WeatherScreen() {
    var selectedCity by remember { mutableStateOf("İstanbul") }
    var query by remember { mutableStateOf("İstanbul") }
    var weather by remember { mutableStateOf<WeatherData?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var retryKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(selectedCity, retryKey) {
        loading = true
        error = null
        weather = null

        try {
            weather = fetchWeather(selectedCity)
        } catch (e: Exception) {
            android.util.Log.e("MeteoraWeather", "Hava durumu verisi alınamadı", e)
            error = "Hata: ${e.javaClass.simpleName}: ${e.message ?: "Ayrıntı bulunamadı"}"
        } finally {
            loading = false
        }
    }

    val suggestions = if (query != selectedCity) {
        provinces.filter {
            it.contains(query.trim(), ignoreCase = true)
        }.take(6)
    } else {
        emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Text(
            text = "METEORA",
            color = SunYellow,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(14.dp))

        Text(
            text = "Hava durumunu keşfet",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(14.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Türkiye'de il ara") },
            placeholder = { Text("Örn. Ankara, İzmir, Tekirdağ") },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedLabelColor = SunYellow,
                unfocusedLabelColor = PaleBlue,
                cursorColor = SunYellow,
                focusedBorderColor = SunYellow,
                unfocusedBorderColor = PaleBlue
            )
        )

        if (suggestions.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBlue, RoundedCornerShape(12.dp))
            ) {
                suggestions.forEach { city ->
                    Text(
                        text = city,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedCity = city
                                query = city
                            }
                            .padding(14.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = SunYellow)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Hava durumu yükleniyor...",
                        color = PaleBlue
                    )
                }
            }
        } else if (error != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = error!!,
                    color = Color(0xFFFFB4AB)
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { retryKey++ },
                    colors = ButtonDefaults.buttonColors(containerColor = SunYellow)
                ) {
                    Text("Tekrar dene", color = Navy)
                }
            }
        } else {
            val data = weather

            if (data != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    CardBlue,
                                    RoundedCornerShape(24.dp)
                                )
                                .padding(22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = selectedCity,
                                color = Color.White,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = weatherEmoji(data.description),
                                fontSize = 54.sp
                            )

                            Text(
                                text = "${data.temperature.toInt()}°",
                                color = Color.White,
                                fontSize = 72.sp,
                                fontWeight = FontWeight.Light
                            )

                            Text(
                                text = data.description,
                                color = PaleBlue,
                                fontSize = 17.sp
                            )

                            Text(
                                text = "Hissedilen ${data.feelsLike.toInt()}°",
                                color = PaleBlue,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            WeatherInfoCard(
                                title = "Nem",
                                value = "${data.humidity}%",
                                modifier = Modifier.weight(1f)
                            )

                            WeatherInfoCard(
                                title = "Rüzgâr",
                                value = "${data.wind} km/sa",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Text(
                            text = "5 günlük tahmin",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(data.days) { day ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    CardBlue,
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = day.date,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = day.description,
                                    color = PaleBlue,
                                    fontSize = 13.sp
                                )
                            }

                            Text(
                                text = "${day.max}° / ${day.min}°",
                                color = SunYellow,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherInfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(CardBlue, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            color = PaleBlue,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = value,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp
        )
    }
}

// Güvenli ve Zaman Aşımı Destekli Ağ İstek Fonksiyonu
private fun readUrlContent(urlString: String): String {
    val url = URL(urlString)
    val connection = url.openConnection() as HttpURLConnection
    connection.connectTimeout = 10000
    connection.readTimeout = 10000
    connection.requestMethod = "GET"
    return connection.inputStream.bufferedReader().use { it.readText() }
}

private suspend fun fetchWeather(city: String): WeatherData =
    withContext(Dispatchers.IO) {
        val encodedCity = URLEncoder.encode(city, "UTF-8")

        val geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=$encodedCity&count=10&language=tr&format=json"
        val geoJsonStr = readUrlContent(geoUrl)
        val geoJson = JSONObject(geoJsonStr)
        val results = geoJson.optJSONArray("results")
            ?: throw Exception("Şehir bulunamadı")

        var latitude: Double? = null
        var longitude: Double? = null

        for (i in 0 until results.length()) {
            val result = results.getJSONObject(i)

            if (result.optString("country_code") == "TR") {
                latitude = result.getDouble("latitude")
                longitude = result.getDouble("longitude")
                break
            }
        }

        val lat = latitude ?: throw Exception("Türkiye'de şehir bulunamadı")
        val lon = longitude ?: throw Exception("Koordinat bulunamadı")

        val forecastUrl = "https://api.open-meteo.com/v1/forecast" +
                "?latitude=$lat&longitude=$lon" +
                "&current=temperature_2m,apparent_temperature," +
                "relative_humidity_2m,wind_speed_10m,weather_code" +
                "&daily=weather_code,temperature_2m_max,temperature_2m_min" +
                "&forecast_days=5&timezone=auto"

        val forecastJsonStr = readUrlContent(forecastUrl)
        val json = JSONObject(forecastJsonStr)
        val current = json.getJSONObject("current")
        val daily = json.getJSONObject("daily")

        val dates = daily.getJSONArray("time")
        val codes = daily.getJSONArray("weather_code")
        val highs = daily.getJSONArray("temperature_2m_max")
        val lows = daily.getJSONArray("temperature_2m_min")

        val days = (0 until dates.length()).map { i ->
            ForecastDay(
                date = dates.getString(i),
                description = weatherDescription(codes.getInt(i)),
                max = highs.getDouble(i).toInt(),
                min = lows.getDouble(i).toInt()
            )
        }

        WeatherData(
            temperature = current.getDouble("temperature_2m"),
            feelsLike = current.getDouble("apparent_temperature"),
            humidity = current.getInt("relative_humidity_2m"),
            wind = current.getDouble("wind_speed_10m"),
            description = weatherDescription(
                current.getInt("weather_code")
            ),
            days = days
        )
    }

private fun weatherDescription(code: Int): String = when (code) {
    0 -> "Açık"
    1 -> "Çoğunlukla açık"
    2 -> "Parçalı bulutlu"
    3 -> "Kapalı"
    45, 48 -> "Sisli"
    51, 53, 55 -> "Çisenti"
    56, 57 -> "Donan çisenti"
    61, 63, 65 -> "Yağmurlu"
    66, 67 -> "Donan yağmur"
    71, 73, 75, 77 -> "Karlı"
    80, 81, 82 -> "Sağanak yağış"
    85, 86 -> "Kar sağanağış"
    95, 96, 99 -> "Gök gürültülü fırtına"
    else -> "Durum bilinmiyor"
}

private fun weatherEmoji(description: String): String = when {
    description.contains("kar", ignoreCase = true) -> "❄️"
    description.contains("yağmur", ignoreCase = true) ||
            description.contains("sağanak", ignoreCase = true) -> "🌧️"
    description.contains("sis", ignoreCase = true) -> "🌫️"
    description.contains("bulut", ignoreCase = true) ||
            description == "Kapalı" -> "☁️"
    description.contains("fırtına", ignoreCase = true) -> "⛈️"
    else -> "☀️"
}
fun getAiOutfitRecommendation(temp: Double, description: String): String {
    val descLower = description.lowercase()
    return when {
        descLower.contains("kar") || temp < 5 ->
            "❄️ Hava oldukça soğuk! Sıkı giyin; kaban, bere, atkı ve eldiven almayı unutma."
        descLower.contains("yağmur") || descLower.contains("sağanak") ->
            "🌧️ Yağış var! Kesinlikle şemsiyeni al ve su geçirmez bir mont tercih et."
        temp in 5.0..15.0 ->
            "🧥 Hava serin. Hafif bir mont, trençkot veya hırka giymek için ideal bir gün."
        temp in 16.0..24.0 ->
            "🌤️ Hava oldukça keyifli ve ılıman. Üzerine ince bir ceket veya uzun kollu tişört giyebilirsin."
        temp > 24.0 ->
            "☀️ Güneşli ve sıcak bir gün! Tişört, güneş gözlüğü ve şapka tercih edebilirsin."
        else -> "✨ Hava koşullarına uygun rahat bir kıyafet seçebilirsin."
    }
}