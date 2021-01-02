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
import java.util.*

val MAPPER: ObjectMapper = ObjectMapper()
    .registerModule(JavaTimeModule())
    .registerModule(KotlinModule())
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)


//enum class Currency { BTC, CHF, CNY, EUR, EGP, GBP, JPY, USD }

data class Price constructor(val time: LocalDateTime, val value: Double)
/*data class Price(val time: String, val value: Double)
data class Price(val value: Double)*/

suspend fun fetchPrice(currency: Currency): Price {
    val url = "https://api.coindesk.com/v1/bpi/currentprice/$currency.json"
    val text = runInterruptible {
        URL(url).readText()
    }

    data class Time(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:00XXX")
        val updatedISO: LocalDateTime,
        //@JsonProperty("updated")
        //val updated: String
    )

    data class Bpi(
        @JsonAlias("rate_float")
        val rateFloat: Double
    )

    data class Data(
        val time: Time,
        //@JsonProperty("bpi")
        val bpi: EnumMap<Currency, Bpi>
    )

    val data = MAPPER.readValue<Data>(text)

    return Price(data.time.updatedISO, data.bpi[currency]!!.rateFloat)
    //return Price(data.time.updated, data.bpi[currency]!!.rateFloat)
    //return Price(data.bpi[currency]!!.rateFloat)
}

fun main() {
    runBlocking {
        async { fetchPrice(Currency.USD) }
    }
}