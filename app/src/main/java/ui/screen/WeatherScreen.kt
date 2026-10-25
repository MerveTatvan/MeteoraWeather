package com.merve.meteoraweather.ui.screen

import androidx.compose.foundation.background
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

@Composable
fun WeatherScreen() {
    val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101C35))
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Arama Kutusu
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Şehir adı yazın...", color = Color.Gray, fontSize = 13.sp) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1A2A4A),
                unfocusedContainerColor = Color(0xFF1A2A4A),
                focusedBorderColor = Color(0xFF64B5F6),
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (searchQuery.isBlank()) "İstanbul" else searchQuery,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "19°",
            color = Color.White,
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Kapalı / Bulutlu (Hissedilen 18°)",
            color = Color.LightGray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailCard(title = "Nem", value = "%74")
            WeatherDetailCard(title = "Rüzgar", value = "13.8 km/sa")
            WeatherDetailCard(title = "Basınç", value = "1015 hPa")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "✨ Yapay Zeka Asistanı Önerileri",
                color = Color(0xFF64B5F6),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        AIRecommendationCard(
            title = "🧥 Detaylı Kıyafet Önerisi",
            content = "Hava oldukça keyifli ve ılıman. Üzerine ince bir ceket giyebilirsin."
        )

        Spacer(modifier = Modifier.height(10.dp))

        AIRecommendationCard(
            title = "🚶‍♂️ Aktivite Önerisi",
            content = "Yürüyüş yapmak için harika bir zaman! Açık havada kısa bir tur atabilirsin."
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 5 Günlük Hava Durumu (Yapay Zeka Önerilerinin Altında)
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "📅 5 Günlük Hava Durumu Tahmini",
                color = Color(0xFF64B5F6),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A4A)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DailyForecastRow(day = "Pazartesi", temp = "19° / 12°", condition = "Bulutlu")
                DailyForecastRow(day = "Salı", temp = "21° / 13°", condition = "Güneşli")
                DailyForecastRow(day = "Çarşamba", temp = "18° / 11°", condition = "Yağmurlu")
                DailyForecastRow(day = "Perşembe", temp = "20° / 12°", condition = "Parçalı Bulutlu")
                DailyForecastRow(day = "Cuma", temp = "22° / 14°", condition = "Güneşli")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sık Bakılan Şehirler (En Altta)
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "📍 Sık Bakılan Şehirler",
                color = Color(0xFF64B5F6),
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
            FavoriteCityCard(cityName = "Ankara", temp = "16°", condition = "Parçalı Bulutlu")
            FavoriteCityCard(cityName = "İzmir", temp = "24°", condition = "Güneşli")
            FavoriteCityCard(cityName = "Bursa", temp = "18°", condition = "Bulutlu")
            FavoriteCityCard(cityName = "Antalya", temp = "27°", condition = "Açık")
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun WeatherDetailCard(title: String, value: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A4A)),
        modifier = Modifier.width(105.dp).height(80.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AIRecommendationCard(title: String, content: String) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162544)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Text(text = title, color = Color(0xFF90CAF9), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = content, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
fun DailyForecastRow(day: String, temp: String, condition: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = day, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Text(text = condition, color = Color.Gray, fontSize = 12.sp)
        Text(text = temp, color = Color(0xFF90CAF9), fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FavoriteCityCard(cityName: String, temp: String, condition: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A4A)),
        modifier = Modifier.width(120.dp).height(90.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = cityName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = temp, color = Color(0xFF90CAF9), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Text(text = condition, color = Color.Gray, fontSize = 11.sp)
        }
    }
}