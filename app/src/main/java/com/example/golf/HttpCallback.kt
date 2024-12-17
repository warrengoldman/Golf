package com.example.golf

import android.util.Log
import com.example.golf.weather.WeatherApiResponse
import kotlinx.serialization.json.Json
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

private const val TAG = "MyUrlRequestCallback"

class HttpCallback : UrlRequest.Callback() {
    var weatherApiResponse : WeatherApiResponse? = null
    override fun onRedirectReceived(request: UrlRequest?, info: UrlResponseInfo?, newLocationUrl: String?) {
        Log.i(TAG, "onRedirectReceived method called.")
        // You should call the request.followRedirect() method to continue
        // processing the request.
        request?.followRedirect()
    }

    override fun onResponseStarted(request: UrlRequest?, info: UrlResponseInfo?) {
        Log.i(TAG, "onResponseStarted method called.")
        // You should call the request.read() method before the request can be
        // further processed. The following instruction provides a ByteBuffer object
        // with a capacity of 102400 bytes for the read() method. The same buffer
        // with data is passed to the onReadCompleted() method.
        request?.read(ByteBuffer.allocateDirect(102400))
    }

    override fun onReadCompleted(request: UrlRequest?, info: UrlResponseInfo?, byteBuffer: ByteBuffer?) {
        Log.i(TAG, "onReadCompleted method called.")
        val str1 = String(byteBuffer!!.array(), StandardCharsets.UTF_8).replace("\u0000", "")
        weatherApiResponse = Json.decodeFromString<WeatherApiResponse>(str1)
        byteBuffer.clear()
        request?.read(byteBuffer)
    }

    override fun onSucceeded(request: UrlRequest?, info: UrlResponseInfo?) {
        Log.i(TAG, "onSucceeded method called.")
    }

    override fun onFailed(
        request: UrlRequest?,
        info: UrlResponseInfo?,
        error: CronetException?
    ) {
        weatherApiResponse = null
        Log.e(TAG, "onFailed method called, error: $error, urlResponseInfo: $info")
    }
}