import tornadofx.*

class BitcoinProperty(time:String, currency: Currency, rate: Float?) {
    var time by property(time)
    fun timeProperty() = getProperty(BitcoinProperty::time)

    var currency by property(currency)
    fun currencyProperty() = getProperty(BitcoinProperty::currency)

    var rate by property(rate)
    fun rateProperty() = getProperty(BitcoinProperty::rate)

}

fun main() {
    val bitcoinInfo = BitcoinInfo
    //bitcoinInfo.
}

