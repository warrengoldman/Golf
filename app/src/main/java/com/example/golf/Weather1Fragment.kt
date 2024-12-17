package com.example.golf

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.golf.databinding.FragmentWeather1Binding
import com.example.golf.weather.WeatherCache
import kotlinx.datetime.*
import kotlin.concurrent.thread


class Weather1Fragment(val weatherCache: WeatherCache, val textViewSunset: TextView) : Fragment() {
    private lateinit var binding: FragmentWeather1Binding
    private val weather1Adapter = Weather1Adapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeather1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = weather1Adapter
        fetchAllTasks()
    }

    fun fetchAllTasks() {
        thread {
            val weatherEntries = weatherCache.getWeatherEntries()
            requireActivity().runOnUiThread {
                weather1Adapter.setEntries(filterWeatherEntries(weatherEntries!!))
                var sunsetTime = "No information available"
                if (weatherEntries.isNotEmpty()) {
                    sunsetTime = weatherEntries[0].sunsetFormattedTime
                }
                textViewSunset.text = getString(R.string.sunset, sunsetTime)
            }
        }
    }

    private fun filterWeatherEntries(entries: List<WeatherEntry>): List<WeatherEntry>? {
        val filteredEntries = mutableListOf<WeatherEntry>()
        val thursday1pm : Long = getNextThursdayCutoff()
        for (weatherEntry in entries) {
            if (weatherEntry.dt > thursday1pm) {
                filteredEntries.add(weatherEntry)
            }
            if (filteredEntries.size > 2) {
                break
            }
        }
        return filteredEntries
    }

    private fun getNextThursdayCutoff(): Long {
        val dayOfWeek = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DayOfWeek.THURSDAY
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val epoch = getEpochTimeForDayOfWeek(dayOfWeek, 12, 0, TimeZone.of("America/Chicago"))/1000
        return epoch
    }
    fun getEpochTimeForDayOfWeek(
        dayOfWeek: DayOfWeek,
        hour: Int,
        minute: Int,
        timeZone: TimeZone
    ): Long {
        val today = Clock.System.todayIn(timeZone)
        var date = today
        while (date.dayOfWeek != dayOfWeek) {
            date = date.minus(DatePeriod(days = -1))
        }

        // Combine the date with the desired time to create a LocalDateTime.
        val localDateTime = LocalDateTime(date, LocalTime(hour, minute, 0))

        // Convert the LocalDateTime to an Instant in the specified time zone.
        val zonedLocalDateTime = localDateTime.toInstant(timeZone)

        // Return the epoch time in milliseconds.
        return zonedLocalDateTime.toEpochMilliseconds()
    }
}