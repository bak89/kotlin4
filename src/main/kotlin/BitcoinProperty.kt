import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.*
import java.net.URL
import java.time.ZonedDateTime
import java.util.*

/***
 * ObjectMapper used to deserialize the json
 */
private val MAPPER: ObjectMapper = ObjectMapper()
    .registerModule(JavaTimeModule())
    .registerModule(KotlinModule())
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

/***
 * Enum Class with all the currency needed
 */
enum class Currency { CHF, CNY, EUR, GBP, USD }

/***
 * Price class where the deserialized data are saved
 */
data class Price constructor(val time: ZonedDateTime, val value: Float)

/***
 * Function to request data from the api , deserialize it, and return it in Price format
 */
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

var job = CoroutineScope(Dispatchers.Default)

fun startUpdates(currency: Currency) {
    job.launch {
        while (true) {
            fetchPrice(currency)
            delay(5000)
        }
    }
}

fun stopUpdates() {
    job.cancel()
    job = CoroutineScope(Dispatchers.Default)
}

fun main() {

    runBlocking {
        val resultOne = async { fetchPrice(Currency.GBP) }
        val resultTwo = async { startUpdates(Currency.EUR) }
        println(resultTwo.await())
    }
    //stopUpdates()
}