package com.example.golf

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.golf.databinding.ActivityMainBinding
import com.example.golf.weather.WeatherCache
import org.chromium.net.CronetEngine


class MainActivity : AppCompatActivity() {
    private lateinit var cronetEngine: CronetEngine
    private val weatherCache: WeatherCache by lazy {
        setWeatherApiUrl()
        setGolfUrl()
        WeatherCache(0, getCronetEngine(), url, golfUrl)
    }
    private lateinit var url: String
    private lateinit var golfUrl: String
    private lateinit var binding: ActivityMainBinding
    private lateinit var weather1Fragment: Weather1Fragment
    fun getCronetEngine(): CronetEngine {
        cronetEngine = CronetEngine.Builder(this).build()
        return cronetEngine
    }

    fun setWeatherApiUrl(): String {
        url =
            "${getString(R.string.weather_api_url)}?lat=${getString(R.string.ridges_latitude)}&lon=${
                getString(R.string.ridges_longitude)
            }&appid=${getString(R.string.weather_api_key)}&units=imperial"
        return url
    }

    fun setGolfUrl(): String {
        golfUrl = getString(R.string.golf_url)
        return golfUrl
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        loadGolfPage()
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        weather1Fragment = Weather1Fragment(weatherCache, binding.textViewSunset)
        binding.pager.adapter = PagerAdapter(this)
//        TeeUpdateCallback(cronetEngine, golfUrl).setTeeTimes()
    }

    inner class PagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun createFragment(position: Int): Fragment = weather1Fragment

        override fun getItemCount(): Int = 1
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadGolfPage() {
        val webView = binding.webview
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.setWebViewClient(WebViewClient())
        webView.loadUrl(getString(R.string.golf_website_url))
    }
}