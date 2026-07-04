import kotlin.math.pow

fun main() {
    val p = 1000.0
    val r = 12.0
    val y = 1
    val s = 0.0

    var totalInvested = 0.0
    var maturityValue = 0.0
    var currentMonthlySip = p
    val monthlyReturnRate = (r / 100.0) / 12.0
    val totalMonths = y * 12

    for (m in 1..totalMonths) {
        totalInvested += currentMonthlySip
        maturityValue = (maturityValue + currentMonthlySip) * (1 + monthlyReturnRate)
    }
    println("Invested: $totalInvested, Maturity: $maturityValue")
}
