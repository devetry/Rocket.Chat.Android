package chat.rocket.android.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import com.pixplicity.sharp.Sharp
import okhttp3.*
import java.io.IOException


object Utils {
    private var httpClient: OkHttpClient? = null

    fun fetchSvg(context: Context, url: String, target: ImageView) {
        if (httpClient == null) {
            // Use cache for performance and basic offline capability
            httpClient = OkHttpClient.Builder()
                    .cache(Cache(context.getCacheDir(), 5 * 1024 * 1014))
                    .build()
        }

        val request = Request.Builder().url(url).build()
        httpClient!!.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("SVG_ERROR", "Android is crap")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val stream = response.body()?.byteStream()
                Log.d("BODY", response.body().toString())
                Log.d("STREAM", stream.toString())
                Sharp.loadInputStream(stream).into(target)
                stream?.close()
            }
        })
    }
}