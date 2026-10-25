package com.merve.meteoraweather.ui.screen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merve.meteoraweather.R

// Ana sayfa: arama kutusu + LazyColumn ile şehir listesi.
// Bir şehre dokununca onCityClick çağrılır, detay sayfasını WeatherScreen() açar.
@Composable
fun CityListScreen(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onCityClick: (String) -> Unit
) {
    val palette = appPalette(isDarkMode)
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val query = toCityName(searchQuery)
    // Liste: 81 ilin tamamı (alfabetik). Yazdıkça süzülür, Türkçe karaktersiz yazım da çalışır.
    val filteredCities = if (query.isBlank()) {
        allCities
    } else {
        allCities.filter { matchesQuery(it, query) }
    }

    fun openCity(cityName: String) {
        Log.d("Hava", "Tıklandı: $cityName")
        keyboardController?.hide()
        onCityClick(cityName)
    }

    // Klavyeden Ara/Enter veya 🔍: tam eşleşme ya da tek sonuç varsa onu aç,
    // aksi halde yazılan ismi aç (detay sayfası "Şehir bulunamadı" gösterir)
    fun submitSearch() {
        if (query.isBlank()) return
        val resolved = resolveCityName(searchQuery)
        openCity(if (resolved in allCities) resolved else filteredCities.singleOrNull() ?: resolved)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Arama kutusu + tema butonu
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(stringResource(R.string.search_placeholder), color = palette.secondary, fontSize = 13.sp) },
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
                    focusedContainerColor = palette.card,
                    unfocusedContainerColor = palette.card,
                    focusedBorderColor = palette.accent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = palette.text,
                    unfocusedTextColor = palette.text
                ),
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = onToggleTheme,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = palette.card),
                modifier = Modifier.height(56.dp).width(60.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(text = if (isDarkMode) "☀️" else "🌙", fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Başlık + sonuç sayısı
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.home_title),
                color = palette.text,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.city_count, filteredCities.size),
                color = palette.secondary,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredCities.isEmpty()) {
            Text(
                text = stringResource(R.string.no_city_match),
                color = palette.secondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(items = filteredCities, key = { it }) { cityName ->
                    CityListItem(
                        cityName = cityName,
                        info = cityDatabase[cityName],
                        palette = palette,
                        // Süzme sonunda tek şehir kaldıysa vurgula (Enter ile açılır)
                        highlighted = query.isNotBlank() && filteredCities.size == 1,
                        onClick = { openCity(cityName) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CityListItem(
    cityName: String,
    info: CityWeatherInfo?,
    palette: AppPalette,
    highlighted: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = palette.card),
        border = if (highlighted) BorderStroke(2.dp, palette.accent) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = if (info != null) weatherEmoji(info.animationType) else "🏙️", fontSize = 28.sp)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = cityName, color = palette.text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = info?.condition?.substringBefore(" (") ?: stringResource(R.string.city_no_data_short),
                    color = palette.secondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(text = info?.temp ?: "–", color = if (info != null) palette.accent else palette.secondary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

private fun weatherEmoji(animationType: String): String = when (animationType) {
    "sun" -> "☀️"
    "rain" -> "🌧️"
    else -> "☁️"
}