import java.text.NumberFormat
import java.util.Locale

fun main() {
    val f = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    println(f.format(1000))
    val f2 = NumberFormat.getCurrencyInstance(Locale.US)
    println(f2.format(1000))
}
