import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.runInterruptible
import java.net.URL
import java.time.LocalDateTime
import java.util.*

val MAPPER: ObjectMapper = ObjectMapper()
    .registerModule(KotlinModule())
    //.registerModule(JavaMod)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

data class Price constructor(val update: LocalDateTime, val price: Double)

suspend fun fetchPrice(currency: Currency): Price {
    val url = "https://api.coindesk.com/v1/bpi/currentprice/$currency.json"
    val text = runInterruptible {
        URL(url).readText()
    }

    data class Time(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:00XXX")
        val updatedISO: LocalDateTime
    )

    data class Bpi(
        @JsonAlias("rate_float")
        val rateFloat: Double
    )

    data class Data(
        val time: Time,
        val bpi: EnumMap<Currency, Bpi>
    )

    val data = MAPPER.readValue<Data>(text)

    return Price(data.time.updatedISO, data.bpi[currency]!!.rateFloat)
}