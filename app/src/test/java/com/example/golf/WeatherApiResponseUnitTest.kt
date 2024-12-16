package com.example.golf

import com.example.golf.weather.WeatherApiResponse
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class WeatherApiResponseUnitTest {
    @Test
    fun weatherApi_deserialization() {
        val jsonStr = weatherApiResponse(getMainListEntry(), getMainListEntry())
        val obj = Json.decodeFromString<WeatherApiResponse>(jsonStr)
        assertNotNull(obj)
        assertEquals(2, obj.list!!.size)
        assertEquals("200", obj.cod)
        assertEquals(0, obj.message)
        assertEquals(2, obj.cnt)
        val city = obj.city!!
        assertEquals(5032106L, city.id)
        assertEquals("Jordan", city.name)
        assertEquals(44.6341, city.coord!!.lat)
        assertEquals(-93.6127, city.coord.lon)
        assertEquals("US", city.country)
        assertEquals(-21600, city.timezone)
        assertEquals(1733838063L, city.sunrise)
        assertEquals(1733870054L, city.sunset)
        val actualMainListEntry = obj.list[0]
        val main = actualMainListEntry.main!!
        val weather = actualMainListEntry.weather!!
        val clouds = actualMainListEntry.clouds!!
        val wind = actualMainListEntry.wind!!
        val sys = actualMainListEntry.sys!!
        val snow = actualMainListEntry.snow!!
        assertEquals(1733864400L, actualMainListEntry.dt)
        assertEquals(268.99, main.temp)
        assertEquals(263.23, main.feelsLike)
        assertEquals(268.99, main.tempMin)
        assertEquals(270.93, main.tempMax)
        assertEquals(1016, main.pressure)
        assertEquals(1016, main.seaLevel)
        assertEquals(980, main.grndLevel)
        assertEquals(74, main.humidity)
        assertEquals(-1.94, main.tempKf)
        assertEquals(804, weather[0].id)
        assertEquals("Clouds", weather[0].main)
        assertEquals("overcast clouds", weather[0].description)
        assertEquals("04d", weather[0].icon)
        assertEquals(94, clouds.all)
        assertEquals(4.7, wind.speed)
        assertEquals(294, wind.deg)
        assertEquals(6.04, wind.gust)
        assertEquals(10000, actualMainListEntry.visibility)
        assertEquals(0.2, actualMainListEntry.pop)
        assertEquals("d", sys.pod)
        assertEquals("2024-12-10 21:00:00", actualMainListEntry.dtTxt)
        assertEquals(0.14, snow.snowDepth)
    }

    @Test
    fun check() {
        println(Instant.fromEpochSeconds(1734129269)).toString()
        val cstDateTime: LocalDateTime = Instant.fromEpochSeconds(1734129269).toLocalDateTime(TimeZone.of("America/Chicago"))
        var hour = cstDateTime.hour
        var amPm = " AM"
        if (hour > 12) {
            hour = hour - 12
            amPm = " PM"
        }
        val formattedTime = hour.toString() + ":" + cstDateTime.minute.toString() + amPm

    }

    private fun weatherApiResponse(mainListEntry1: String, mainListEntry2: String): String =
        """{"cod": "200", "message":0, "cnt": 2, 
    |"list": [
        | $mainListEntry1, 
        | $mainListEntry2
    |], 
    |"city": {"id": 5032106, "name": "Jordan", 
    |   "coord":{"lat": 44.6341, "lon": -93.6127},
    |   "country": "US",
    |   "timezone":-21600,
    |   "sunrise":1733838063,
    |   "sunset":1733870054
    |}
    |}""".trimMargin()

    private fun getMainListEntry() =
        """{
        |   "dt": 1733864400,
            "main": {
                "temp": 268.99,
                "feels_like": 263.23,
                "temp_min": 268.99,
                "temp_max": 270.93,
                "pressure": 1016,
                "sea_level": 1016,
                "grnd_level": 980,
                "humidity": 74,
                "temp_kf": -1.94
            },
            "weather": [
                {
                    "id": 804,
                    "main": "Clouds",
                    "description": "overcast clouds",
                    "icon": "04d"
                }
            ],
            "clouds": {
                "all": 94
            },
            "wind": {
                "speed": 4.7,
                "deg": 294,
                "gust": 6.04
            },
            "visibility": 10000,
            "snow":{"3h":0.14},
            "pop": 0.2,
            "sys": {
                "pod": "d"
            },
            "dt_txt": "2024-12-10 21:00:00" 
    |}""".trimMargin()
}