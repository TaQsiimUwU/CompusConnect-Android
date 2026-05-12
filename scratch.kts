import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

val input = "Tue May 12 2026 12:22:00 GMT+0000 (Coordinated Universal Time)"
val clean = input.substringBefore(" (")
val parser = SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.US)
val date = parser.parse(clean)
val iso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}.format(date)
println(iso)
