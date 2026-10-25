package com.merve.meteoraweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.merve.meteoraweather.ui.screen.WeatherScreen
import com.merve.meteoraweather.ui.theme.MeteoraWeatherTheme

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