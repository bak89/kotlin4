import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.*
import java.net.URL
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.*

val MAPPER: ObjectMapper = ObjectMapper()
    .registerModule(JavaTimeModule())
    .registerModule(KotlinModule())
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)


enum class Currency { BTC, CHF, CNY, EUR, EGP, GBP, JPY, USD }

data class Price constructor(val time: ZonedDateTime, val value: Float)


suspend fun fetchPrice(currency: Currency): Price = withContext(Dispatchers.IO) {
    val url = "https://api.coindesk.com/v1/bpi/currentprice/$currency.json"
    val text = runInterruptible {
        URL(url).readText()
    }

    data class Time(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:00XXX")
        val updatedISO: ZonedDateTime
    )

    data class Bpi(
        @JsonAlias("rate_float")
        val rateFloat: Float
    )

    data class Data(
        val time: Time,
        val bpi: EnumMap<Currency, Bpi>
    )

    val data = MAPPER.readValue<Data>(text)

    return@withContext Price(data.time.updatedISO, data.bpi[currency]!!.rateFloat)
}

fun main() {
    runBlocking {
        val eur = fetchPrice(Currency.EUR)
        val usd = fetchPrice(Currency.USD)
        println(eur)
        println(usd)
    }
}