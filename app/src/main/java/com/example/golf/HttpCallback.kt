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
    var isCompleted : Boolean = false
    var receivedData = ByteBuffer.allocateDirect(307200)

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
        byteBuffer!!.flip() // Prepare the received buffer for reading

        // Ensure sufficient capacity in receivedData
        if (receivedData.remaining() < byteBuffer.remaining()) {
            val newBuffer = ByteBuffer.allocate(receivedData.capacity() + byteBuffer.remaining())
            receivedData.flip()
            newBuffer.put(receivedData)
            receivedData = newBuffer
        }

        receivedData.put(byteBuffer) // Append received data
        byteBuffer.clear() // Prepare the received buffer for the next read

        request!!.read(byteBuffer)
    }

    override fun onSucceeded(request: UrlRequest?, info: UrlResponseInfo?) {
        val str1 = String(receivedData.array(), StandardCharsets.UTF_8).replace("\u0000", "")
        try {
            weatherApiResponse = Json.decodeFromString<WeatherApiResponse>(str1)
        } catch(e: Exception) {
            Log.e(TAG, e.message.toString())
        }
        isCompleted = true
        Log.i(TAG, "onSucceeded method called.")
    }

    override fun onFailed(
        request: UrlRequest?,
        info: UrlResponseInfo?,
        error: CronetException?
    ) {
        weatherApiResponse = null
        Log.e(TAG, "onFailed method called, error: $error, urlResponseInfo: $info")
        isCompleted = true
    }
}