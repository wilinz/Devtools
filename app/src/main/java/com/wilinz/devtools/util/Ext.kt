import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.wilinz.devtools.data.moshi
import okio.BufferedSink
import okio.BufferedSource
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

inline fun <reified T> Moshi.adapter(): JsonAdapter<T> = adapter(T::class.java)

inline fun <reified T> Moshi.toJson(v: T): String = adapter<T>().toJson(v)

inline fun <reified T> Moshi.toJson(sink: BufferedSink, v: T) = adapter<T>().toJson(sink, v)

inline fun <reified T> Moshi.toJson(writer: JsonWriter, v: T) = adapter<T>().toJson(writer, v)

inline fun <reified T> Moshi.fromJson(json: String) = adapter<T>().fromJson(json)

inline fun <reified T> Moshi.fromJson(json: BufferedSource) = adapter<T>().fromJson(json)

inline fun <reified T> Moshi.fromJson(json: JsonReader) = adapter<T>().fromJson(json)

inline fun <reified T : Any> T.toMap(): Map<String, String> {
    val json = moshi.toJson(this)
    val map = moshi.fromJson<Map<String,String>>(json)
    return map!!
}
