package com.example.golf

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.golf.databinding.FragmentWeatherEntryBinding


class Weather1Adapter :
    RecyclerView.Adapter<Weather1Adapter.ViewHolder>() {
    private var entries: List<WeatherEntry> = listOf()
    fun setEntries(entries: List<WeatherEntry>?) {
        this.entries = entries!!
        notifyDataSetChanged()
    }

    override fun getItemCount() = entries.size

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        return ViewHolder(
            FragmentWeatherEntryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder, position: Int
    ) {
        holder.bind(entries[position])
    }

    inner class ViewHolder(private val binding: FragmentWeatherEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val resources = binding.root.context.resources
        fun bind(weatherEntry: WeatherEntry) {
            binding.textViewTemp.text =
                resources.getString(R.string.weather_temp, weatherEntry.temp.toString())
            binding.textViewWeatherSkies.text =
                resources.getString(R.string.weather_skies, weatherEntry.skyTitle, weatherEntry.skyDescription)
            binding.imageViewWeatherIcon.setImageResource(getWeatherIconDrawable(weatherEntry.skyIcon))
            binding.textViewWind.text =
                resources.getString(R.string.weather_wind, weatherEntry.windMph.toString(), weatherEntry.windGustMph.toString(), weatherEntry.windDirection)
            binding.textViewHeading.text = weatherEntry.heading
        }

        private fun getWeatherIconDrawable(weatherIcon: String): Int {
            return when(weatherIcon) {
                "01d" -> R.drawable._1d
                "02d" -> R.drawable._2d
                "03d" -> R.drawable._3d
                "04d" -> R.drawable._4d
                "09d" -> R.drawable._9d
                "10d" -> R.drawable._10d
                "11d" -> R.drawable._11d
                "13d" -> R.drawable._13d
                "50d" -> R.drawable._50d
                "01n" -> R.drawable._1n
                "02n" -> R.drawable._2n
                "03n" -> R.drawable._3n
                "04n" -> R.drawable._4n
                "09n" -> R.drawable._9n
                "10n" -> R.drawable._10n
                "11n" -> R.drawable._11n
                "13n" -> R.drawable._13n
                "50n" -> R.drawable._50n
                else -> R.drawable._1d
            }
        }
    }
}