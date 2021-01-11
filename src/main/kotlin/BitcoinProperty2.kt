import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.*
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

enum class Currency { CHF, CNY, EUR, EGP, GBP, JPY, USD }

data class Price(val time: LocalDateTime, val value: Float)


data class Time(
    val updatedISO: String
)

data class Bpi(
    @JsonAlias("rate_float")
    val rateFloat: Float
)

data class Data(
    val time: Time,
    val bpi: EnumMap<Currency, Bpi>
)

suspend fun fetchPrice(currency: Currency): Price = withContext(Dispatchers.IO) {
    val mapper = jacksonObjectMapper()
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    val url = "https://api.coindesk.com/v1/bpi/currentprice/$currency.json"
    val text = URL(url).readText()

    val data: Data = mapper.readValue(text)

    return@withContext Price(
        LocalDateTime.parse(data.time.updatedISO, DateTimeFormatter.ISO_DATE_TIME),
        data.bpi[currency]!!.rateFloat
    )
}
//return Price(data.bpi[currency]!!.rateFloat)


fun main() {
    runBlocking {
        val eur = fetchPrice(Currency.EUR)
        val usd = fetchPrice(Currency.USD)
        println(eur)
        println(usd)
    }
}