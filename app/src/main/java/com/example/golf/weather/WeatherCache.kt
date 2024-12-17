package com.example.golf.weather

import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import com.example.golf.HttpCallback
import com.example.golf.WeatherEntry
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.chromium.net.CronetEngine
import org.chromium.net.UrlRequest
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class WeatherCache(var time: Long, val cronetEngine: CronetEngine, val url: String) {
    private val TEN_MINUTES : Long = 600000
    var cachedWeatherEntries : List<WeatherEntry>? = listOf()
    var cacheExpired : Boolean = true
    var retryCnt : Int = 0
    fun getWeatherEntries(): List<WeatherEntry>? {
        if ((Calendar.getInstance().time.time - time) > TEN_MINUTES) {
            cacheExpired = true
        }
        if (cacheExpired) {
            val executor: Executor = Executors.newSingleThreadExecutor()
            val callback = HttpCallback()
            val requestBuilder = cronetEngine.newUrlRequestBuilder(
                url,
                callback,
                executor
            )

            val request: UrlRequest = requestBuilder.build()
            request.start()
            while (!callback.isCompleted) {
                Thread.sleep(1000)
            }
            if (callback.weatherApiResponse != null) {
                cachedWeatherEntries = convertToWeatherEntries(callback.weatherApiResponse)
                cacheExpired = false
                retryCnt = 0
            } else if (retryCnt < 2) {
                retryCnt++
                return getWeatherEntries()
            } else {
                Log.i("WeatherCache", "retry count exceeded, failing.")
            }
        }
        return cachedWeatherEntries
    }

    private fun convertToWeatherEntries(response: WeatherApiResponse?): List<WeatherEntry> {
        val sunsetFormattedTime = getFormattedTime(response!!.city!!.sunset!!)
        val weatherEntries = mutableListOf<WeatherEntry>()
        val dtf: DateTimeFormatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a", Locale.ROOT)
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        for (weatherApiListElement in response.list!!) {
            val main = weatherApiListElement.main!!
            val temp = main.temp!!.toInt()
            val dtInCst = weatherApiListElement.dt!!
            val formattedDate = getFormatted( dtInCst, dtf)
            val weather = weatherApiListElement.weather!!
            val weatherMain = weather[0].main!!
            val weatherDescription = weather[0].description!!
            val weatherIcon = weather[0].icon!!
            val wind = weatherApiListElement.wind!!
            val windSpeed = wind.speed!!
            val windGust = wind.gust!!
            val windDirection = getWindDirection(wind.deg!!)
            weatherEntries.add(
                WeatherEntry(
                    sunsetFormattedTime,
                    temp,
                    formattedDate,
                    dtInCst,
                    weatherMain,
                    weatherDescription,
                    weatherIcon,
                    windSpeed,
                    windGust,
                    windDirection
                )
            )
        }
        return weatherEntries
    }

    private fun getFormattedTime(secondsFromEpoch: Long): String {
        val dtf: DateTimeFormatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("hh:mm a", Locale.ROOT)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        return getFormatted(secondsFromEpoch, dtf)
    }

    private fun getFormatted(secondsFromEpoch: Long, dtf: DateTimeFormatter): String {
        val cstDateTime: LocalDateTime = Instant.fromEpochSeconds(secondsFromEpoch)
            .toLocalDateTime(TimeZone.of("America/Chicago"))
        //(year: Int, monthNumber: Int, dayOfMonth: Int, hour: Int, minute: Int, second: Int, nanosecond: Int)
        val dfe: java.time.LocalDateTime =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                java.time.LocalDateTime.of(
                    cstDateTime.year,
                    cstDateTime.monthNumber,
                    cstDateTime.dayOfMonth,
                    cstDateTime.hour,
                    cstDateTime.minute,
                    cstDateTime.second,
                    cstDateTime.nanosecond
                )
            } else {
                TODO("VERSION.SDK_INT < O")
            }
        return dfe.format(dtf)
    }

    private fun getWindDirection(degree: Int): String {
        var windDir: String = "N"
        if (degree > 12 && degree < 34) {
            windDir = "NNE"
        } else if (degree >= 34 && degree < 56) {
            windDir = "NE"
        } else if (degree >= 56 && degree < 78) {
            windDir = "ENE"
        } else if (degree >= 78 && degree < 102) {
            windDir = "E"
        } else if (degree >= 102 && degree < 124) {
            windDir = "ESE"
        } else if (degree >= 124 && degree < 146) {
            windDir = "SE"
        } else if (degree >= 146 && degree < 168) {
            windDir = "SSE"
        } else if (degree >= 168 && degree < 192) {
            windDir = "S"
        } else if (degree >= 192 && degree < 214) {
            windDir = "SSW"
        } else if (degree >= 214 && degree < 236) {
            windDir = "SW"
        } else if (degree >= 236 && degree < 258) {
            windDir = "WSW"
        } else if (degree >= 258 && degree < 282) {
            windDir = "W"
        } else if (degree >= 282 && degree < 304) {
            windDir = "WNW"
        } else if (degree >= 304 && degree < 326) {
            windDir = "NW"
        } else if (degree >= 326 && degree < 348) {
            windDir = "NNW"
        }
        return windDir
    }
}