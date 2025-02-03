package com.example.golf

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.golf.databinding.ActivityMainBinding
import com.example.golf.weather.WeatherCache
import org.chromium.net.CronetEngine
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.location.LocationServices
import android.Manifest
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Priority

class MainActivity : AppCompatActivity() {
    private lateinit var cronetEngine: CronetEngine
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val cancellationTokenSource = CancellationTokenSource()
    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher. You can use either a val, as shown in this snippet,
    // or a lateinit var in your onAttach() or onCreate() method.
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                    Log.d("Location", "Precise location access granted.")
                    getLastLocation()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                    Log.d("Location", "Only approximate location access granted.")
                    getLastLocation()
                } else -> {
                // No location access granted.
                Log.d("Location", "No location access granted.")
            }
            }
        }
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        requestLocationPermissions()
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

    private fun requestLocationPermissions() {
        // Check if permissions are already granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions are not granted, request them
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            // Permissions are already granted, get the location
            getLastLocation()
        }
    }

    fun createCurrentLocationRequest(): CurrentLocationRequest {
        val priority = Priority.PRIORITY_HIGH_ACCURACY
        return CurrentLocationRequest.Builder()
            .setPriority(priority)
            .setMaxUpdateAgeMillis(10000) // Optional: Set the maximum acceptable age of the location
            .setDurationMillis(5000) // Optional: Set the duration for which to request the location
            .build()
    }

    private fun getLastLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions are not granted, return
            return
        }

        fusedLocationClient.getCurrentLocation(createCurrentLocationRequest(), cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    // Use the location data
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Log.d("Location", "Latitude: $latitude, Longitude: $longitude")
                } else {
                    Log.d("Location", "Location is null")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Location", "Error getting location: ${e.message}")
            }
    }
    override fun onDestroy() {
        super.onDestroy()
        cancellationTokenSource.cancel()
    }
}