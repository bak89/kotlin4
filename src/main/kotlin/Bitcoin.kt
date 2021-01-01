import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.modules.*
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.HashMap

object IsoDateSerializer : KSerializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ISO_DATE_TIME
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: LocalDateTime) = encoder.encodeString(formatter.format(value))
    override fun deserialize(decoder: Decoder): LocalDateTime = LocalDateTime.parse(decoder.decodeString(), formatter)
}

enum class Currency { BTC, CHF, CNY, EUR, EGP, GBP, JPY, USD }

@Serializable
data class BitcoinInfo(val updated: String, val prices: MutableMap<Currency, Float>)

class Bitcoin(val currency: Currency = Currency.USD) {

    private val module = SerializersModule {
        contextual(IsoDateSerializer)
    }

    private val format = Json {
        serializersModule = module;
        ignoreUnknownKeys = true;
        isLenient = true
    }

    //var currency = Currency.USD

    private val price = "https://api.coindesk.com/v1/bpi/currentprice.json"
    private val historyPrice = "https://api.coindesk.com/v1/bpi/historical/close.json"
    private val converterPrice = "https://api.coindesk.com/v1/bpi/currentprice/$currency.json"

    private fun URL.getText(): String {
        return openConnection().run {
            this as HttpURLConnection
            inputStream.bufferedReader().readText()
        }
    }

    fun getCurrent3Price(): BitcoinInfo {
        val data: Bt = format.decodeFromString(URL(price).getText())
        return BitcoinInfo(data.time.updated, data.bpi.mapValues { it.value.rate_float } as MutableMap<Currency, Float>)
    }

    fun getCurrent2Price(): BitcoinInfo {
        val data: Bt = format.decodeFromString(URL(converterPrice).getText())
        return BitcoinInfo(data.time.updated, data.bpi.mapValues { it.value.rate_float } as MutableMap<Currency, Float>)
    }

    fun getHistory(): String {
        return URL(historyPrice).getText()
    }



    @Serializable
    private data class Bt(
        val bpi: HashMap<Currency, Bpi>,
        val time: Time,
        val disclaimer: String
    )

    @Serializable
    private data class Bpi(
        //val code: String,
        val rate_float: Float,
        //val description: String
    )

    @Serializable
    private data class Time(
        @Contextual
        val updatedISO: LocalDateTime,
        val updated: String
    )

}

fun main() {

    val bitcoinTest = Bitcoin(Currency.EGP)
    // bitcoinTest.currency = Currency.EGP
    println(bitcoinTest.currency)
    println(bitcoinTest.getCurrent2Price())
    //val data = bitcoinTest.getUpdateTime()
    // println(data.bpi.get(key = "EGP")?.rate_float)
    // println(bitcoinTest.getUpdateTime())
    println(Json.encodeToString(listOf(bitcoinTest.getCurrent3Price(), bitcoinTest.getCurrent2Price())))

}