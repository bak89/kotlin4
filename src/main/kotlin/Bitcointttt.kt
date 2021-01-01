import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import khttp.get
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import org.json.JSONObject
import java.time.LocalTime
import javax.json.Json


//enum class Currency { BTC, CHF, CNY, EUR, EGP, GBP, JPY, USD }

class Bitcoinccc(val price: Double, val currency: Currency, val time: Date) {
    /*
    - builder
    - connect api
    - get value api
    - print bitcoin value
    - print time value
    - async update background all min or when append
    - history
    - converter

    ----------------------
    - more currency
    - save history
    - graphic of change bt
     */

    val currentPrice = "https://api.coindesk.com/v1/bpi/currentprice.json"
    val historyPrice = "https://api.coindesk.com/v1/bpi/historical/close.json"
    val converterPrice = "https://api.coindesk.com/v1/bpi/currentprice/$currency.json"

    fun URL.getText(): String {
        return openConnection().run {
            this as HttpURLConnection
            inputStream.bufferedReader().readText()
        }
    }

    fun getCurrent(): String {
        return URL(currentPrice).getText()
    }

    fun getHistory(): String {
        return URL(historyPrice).getText()
    }

    fun getConvert(): String {
        return URL(converterPrice).getText()
    }

    fun getJson(): JSONObject {
        val r = get(currentPrice)
        return r.jsonObject
    }

    /*fun getMap(): HashMap<*, *>? {
        return ObjectMapper().readValue(getCurrent(), HashMap::class.java)
    }*/

    val map = ObjectMapper().readValue(getCurrent(), HashMap::class.java)


    fun getUsd() {
        val map = ObjectMapper().readValue(getCurrent(), HashMap::class.java)
        for(key in map.keys){
            println("Element at key $key = ${map.get(key)}")
        }

        //println(map.get(key = "bpi"))
        //println(map.size)

    }

    fun getTime(){
        println(map.get(key = "time"))
    }





/*
    data class BT(
        //val bpi: Json,
        val rate: Float? = 2f,
        //var time: Date
    )

    val mapper = jacksonObjectMapper()
    fun deserializeBitCoin() {
        val bt: BT = mapper.readValue(URL(currentPrice))
        //print(bt.bpi)
        print(bt.rate)
    }*/
}
fun main() {

    //list of currency:BTC,CHF,CNY,EUR,EGP,GBP,JPY,USD
    //val bitcoin = Bitcoin(22.3,Currency.USD, Date(22-12-2020))
   // println(bitcoin.getMap())
    //val r = get(bitcoin.currentPrice)
    //println(r.statusCode)
    //println(r.jsonObject)
    //bitcoin.whenDeserializeBitCoin()
    //println(bitcoin.getCurrent())

    //print(bitcoin.getUsd())
    //bitcoin.getTime()

//    bitcoin.deserializeBitCoin()

}