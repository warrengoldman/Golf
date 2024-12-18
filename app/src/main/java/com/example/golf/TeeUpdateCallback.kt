package com.example.golf

import android.util.Log
import org.chromium.net.CronetEngine
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

private const val TAG = "****TEEUPDATECALLBACK****"
class TeeUpdateCallback(val cronetEngine: CronetEngine, val url: String) : UrlRequest.Callback() {
    override fun onRedirectReceived(
        request: UrlRequest?,
        info: UrlResponseInfo?,
        newLocationUrl: String?
    ) {
        request?.followRedirect()
    }

    override fun onResponseStarted(
        request: UrlRequest?,
        info: UrlResponseInfo?
    ) {
        request?.read(ByteBuffer.allocateDirect(102400))
    }

    override fun onReadCompleted(
        request: UrlRequest?,
        info: UrlResponseInfo?,
        byteBuffer: ByteBuffer?
    ) {
        val str1 = String(byteBuffer!!.array(), StandardCharsets.UTF_8)
        Log.i(TAG, "byteBuffer value $str1")
        byteBuffer!!.clear() // Prepare the received buffer for the next read

        request!!.read(byteBuffer)
        Log.i(TAG, "onReadCompleted method called. $info")
    }

    override fun onSucceeded(
        request: UrlRequest?,
        info: UrlResponseInfo?
    ) {
        Log.i(TAG, "onSucceeded method called, urlResponseInfo: $info")
    }

    override fun onFailed(
        request: UrlRequest?,
        info: UrlResponseInfo?,
        error: CronetException?
    ) {
        Log.e(TAG, "onFailed method called, error: $error, urlResponseInfo: $info")
    }

    fun setTeeTimes() {
        val requestBuilder = cronetEngine.newUrlRequestBuilder(
            url,
            this,
            Executors.newSingleThreadExecutor()
        )
        requestBuilder.build().start()
    }
}