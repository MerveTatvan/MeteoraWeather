package com.merve.meteoraweather.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WeatherScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101C35)) // MainActivity'deki koyu tema rengiyle uyumlu arkaplan
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Şehir Adı
        Text(
            text = "İstanbul",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sıcaklık
        Text(
            text = "20°",
            color = Color.White,
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold
        )

        // Hava Durumu Açıklaması
        Text(
            text = "Kapalı / Bulutlu",
            color = Color.LightGray,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Nem, Rüzgar ve Yeni 3. Kutu (Basınç) İçin Yatay Yerleşim
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailCard(title = "Nem", value = "%67")
            WeatherDetailCard(title = "Rüzgar", value = "10.7 km/s")
            WeatherDetailCard(title = "Basınç", value = "1015 hPa") // Eklenen 3. kutu
        }
    }
}

// Detay Kartları İçin Yardımcı Composable Fonksiyonu
@Composable
fun WeatherDetailCard(title: String, value: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A4A)),
        modifier = Modifier
            .width(105.dp)
            .height(80.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}