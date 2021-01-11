import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javafx.collections.FXCollections.observableArrayList
import javafx.geometry.Pos
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.stage.Stage
import kotlinx.coroutines.*
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json


import tornadofx.*
import javafx.beans.property.SimpleIntegerProperty

import javafx.beans.property.IntegerProperty
import javafx.collections.ObservableList
import javafx.scene.chart.XYChart
import java.io.FileWriter
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import java.time.LocalTime

import java.time.LocalDate


class BitcoinApp : App(MainView::class) {
    override fun start(stage: Stage) {
        stage.width = 650.0
        stage.height = 1000.0
        super.start(stage)
    }
}

class MainView : View("Bitcoin Viewer") {

    //private var bitcoin = Bitcoin(Currency.USD)
    // private var bitcoinInfo: Task<Price>? = null
    // private var price by singleAssign<Price>()
    private var bitcoinPrice: Label by singleAssign()
    private var currencyLabel: Label by singleAssign()
    private var dateUpdate: Label by singleAssign()
    private var status: Label by singleAssign()

    private var export: Button by singleAssign()
    private var valueChart: Float = 1.0F
    private var dataChart = XYChart.Data("Currency", valueChart)
    private var area = areachart("Bitcoin History", CategoryAxis(), NumberAxis()) {}

    private val data: ObservableList<Array<String>> = observableArrayList(
        arrayOf("", "", "")
    )

    //var first: IntegerProperty = SimpleIntegerProperty(1000)


    override val root = borderpane {
        top {
            vbox(alignment = Pos.TOP_CENTER, spacing = 10) {
                label("Bitcoin Analyzer") {
                    font = Font.font("Dialog", FontWeight.BOLD, 36.0)
                }
            }
        }
        center {
            vbox(alignment = Pos.CENTER_LEFT, spacing = 10) {
                padding = insets(10)
                vbox(spacing = 10.0, alignment = Pos.CENTER_LEFT) {
                    status = label("") {
                        font = Font.font("Dialog", FontWeight.BOLD, 30.0)
                    }
                    hbox(spacing = 10.0, alignment = Pos.CENTER_LEFT) {
                        label("Current Bitcoin price: ") {
                            font = Font.font("Dialog", FontWeight.BOLD, 20.0)
                        }
                        bitcoinPrice = label("0") {
                            font = Font.font("Dialog", FontWeight.BOLD, 20.0)
                        }
                    }
                    hbox(spacing = 10.0, alignment = Pos.CENTER_LEFT) {
                        label("Currency:") {
                            font = Font.font("Dialog", FontWeight.BOLD, 20.0)
                        }
                        currencyLabel = label("0") {
                            font = Font.font("Dialog", FontWeight.BOLD, 20.0)
                        }
                    }
                    hbox(spacing = 10.0, alignment = Pos.CENTER_LEFT) {
                        label("Updated on:") {
                            font = Font.font("Dialog", FontWeight.BOLD, 20.0)
                        }
                        dateUpdate = label("0") {
                            font = Font.font("Dialog", FontWeight.BOLD, 20.0)
                        }
                    }
                }
                vbox(spacing = 10.0, alignment = Pos.CENTER) {
                    label("Currencies") {
                        font = Font.font("Dialog", FontWeight.NORMAL, 18.0)
                    }
                    hbox(spacing = 10.0, alignment = Pos.CENTER) {
                        button("USD") {
                            prefWidth = 70.0
                            action {
                                setCurrency(Currency.USD)
                            }
                        }
                        button("GBP") {
                            prefWidth = 70.0
                            action {
                                setCurrency(Currency.GBP)
                            }
                        }
                        button("EUR") {
                            prefWidth = 70.0
                            action {
                                setCurrency(Currency.EUR)
                            }
                        }
                    }
                    hbox(spacing = 10.0, alignment = Pos.CENTER) {
                        button("CHF") {
                            prefWidth = 70.0
                            action {
                                setCurrency(Currency.CHF)
                            }
                        }
                        /*button("JPY") {
                            prefWidth = 70.0
                            action {
                                setCurrency(Currency.JPY)
                            }
                        }*/
                        button("CNY") {
                            prefWidth = 70.0
                            action {
                                setCurrency(Currency.CNY)
                            }
                        }
                    }
                }
                vbox(spacing = 10.0, alignment = Pos.CENTER_LEFT) {
                    label("Graph") {
                        font = Font.font("Dialog", FontWeight.NORMAL, 18.0)
                    }
                    area = areachart("Bitcoin History", CategoryAxis(), NumberAxis()) {
                        /* series("USD") {
                             //data.onChange { data[0] }
                             //data("13.51", 12323)
                             //data(d11.xValue, d11.yValue.value)
                             // data(d11.xValue, d11.yValue)


                         }
                         series("GBP") {

                         }
                         series("EUR") {

                         }
                         series("CHF") {

                         }
                         /*series("JPY") {jpn number too big for graph

                         }*/
                         series("CNY") {

                         }*/
                    }
                }

                separator {}
            }
            bottom {
                vbox(alignment = Pos.CENTER_LEFT, spacing = 10) {
                    padding = insets(10)
                    label("History") {
                        font = Font.font("Dialog", FontWeight.NORMAL, 18.0)
                    }
                    tableview(data) {
                        column("Time", String::class) {
                            value { it.value[0] }
                        }
                        column("value", String::class) {
                            value { it.value[1] }
                        }
                        column("Currency", String::class) {
                            value { it.value[2] }
                        }

                        prefWidth = 667.0
                        prefHeight = 200.0

                        columnResizePolicy = CONSTRAINED_RESIZE_POLICY

                        vboxConstraints {
                            vGrow = Priority.ALWAYS
                        }
                    }
                    hbox(spacing = 10.0, alignment = Pos.CENTER_LEFT) {
                        label("Export history as text file:") {
                            font = Font.font("Dialog", FontWeight.NORMAL, 16.0)
                        }
                        export = button("Export") {
                            prefWidth = 70.0
                            action {
                                try {
                                    writeToFile()
                                    status.text = "Successfully exported"
                                    status.textFill = Color.DARKGREEN
                                } catch (e: Exception) {
                                    status.text = "Attention: ${e.message}."
                                    status.textFill = Color.RED
                                }
                            }
                        }

                    }

                }
            }
        }
    }

    //2variante TODO
    private fun setCurrency(currency: Currency) {
        //2.1 variante
        /*MainScope().launch {
            val one = withContext(Dispatchers.Main) { fetchPrice(Currency.EUR) }
            println(one.time)
        }*/
        //2.2 variante
        /*val job = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                status.text = "Updating..."
                status.textFill = Color.ORANGE
                val curr = fetchPrice(currency)
                bitcoinPrice.text = curr.value.toString()
                dateUpdate.text = curr.time.toString()
                currencyLabel.text = currency.toString()
                status.text = "Updated."
                status.textFill = Color.DARKGREEN
                val uno = arrayOf(curr)
                println("${uno[0].time}, ${uno[0].value}")
                data.add(arrayOf(uno[0].time.toString(), uno[0].value.toString(), currency.name))
                dataGraph = uno[0].time.toString()

                dataChart.xValue = curr.time.hour.toString() + "." + curr.time.minute.toString()
                dataChart.yValue = curr.value
                println("${dataChart.xValue}, ${dataChart.yValue}")
                updateGraph(currency, dataChart.xValue, dataChart.yValue)
            }
        }

        job.cancel()*/


        // runblocking variante actually work TODO
        runBlocking {
            status.text = "Updating..."
            status.textFill = Color.ORANGE
            val curr = fetchPrice(currency)
            bitcoinPrice.text = curr.value.toString()
            dateUpdate.text = curr.time.format(
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withZone(ZoneId.of("Europe/Paris"))
            )
            currencyLabel.text = currency.toString()
            status.text = "Updated."
            status.textFill = Color.DARKGREEN
            val uno = arrayOf(curr)
            println("${uno[0].time}, ${uno[0].value}")
            data.add(
                arrayOf(
                    uno[0].time.format(
                        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withZone(ZoneId.of("Europe/Paris"))
                    ),
                    uno[0].value.toString(),
                    currency.name
                )
            )
            //dataChart.xValue = curr.time.hour.toString() + "." + curr.time.minute.toString()
            dataChart.xValue = curr.time.format(DateTimeFormatter.ISO_LOCAL_TIME.withZone(ZoneId.of("Europe/Paris")))
            dataChart.yValue = curr.value
            println("${dataChart.xValue}, ${dataChart.yValue}")
            updateGraph(currency, dataChart.xValue, dataChart.yValue)
        }
    }

    /***
     * Function to w
     */
    private fun writeToFile() {
        val fileName = "myHistory.txt"
        val mapper = jacksonObjectMapper()
        val writer = FileWriter(fileName)
        mapper.writeValue(writer, data)

        status.text = "Written on file"
        status.textFill = Color.ORANGE
    }

    // update problem with series TODO
    private fun updateGraph(currency: Currency, time: String, value: Float) {
        area.series(currency.name) {
            data(time, value)
        }
    }
//1 variante
    /* not working anymore
    private fun setCurrency(currency: Currency) {
        bitcoinInfo?.cancel()
        bitcoin = Bitcoin(currency)
        update()
    }

    private fun update() {
        status.text = "Updating..."
        status.textFill = Color.ORANGE
        bitcoinInfo = runAsync {
            if (bitcoin.currency in listOf(Currency.USD, Currency.EUR, Currency.GBP)) {
                bitcoin.getCurrent3Price()
            } else {
                bitcoin.getCurrent2Price()
            }
        }.success {
            val number = history.find { h -> h.updated == it.updated }
            if (number != null) {
                number.prices.putAll(it.prices)
            } else {
                history.add(it)
            }
            populateData(bitcoin.currency)
            println(getTime())
            println(getRate(bitcoin.currency))
            println(getRateCurrency())
            println(history)
            bitcoinPrice.text = it.prices[bitcoin.currency].toString()
            dateUpdate.text = it.updated.toString()
            currencyLabel.text = bitcoin.currency.toString()
            status.text = "Updated."
            status.textFill = Color.DARKGREEN
        }.fail {
            status.text = "Attention: ${it.message}."
            status.textFill = Color.RED
        }
    }

    fun getTime(): String? {
        val time = history[history.lastIndex]?.updated
        // table.add(time)
        //  println(table)
        return time
    }

    fun getRateCurrency(): MutableCollection<Float> {
        val curr = history[history.lastIndex].prices.values
        //history2.add(1,curr.toString())
        return curr
    }


    fun getRate(currency: Currency): Float? {
        val curr = history[history.lastIndex].prices[currency]
        // table.add(curr.toString())
        return curr
    }

    fun populateData(currency: Currency) {
        val time = history[history.lastIndex].updated
        val curr = history[history.lastIndex].prices[currency].toString()
        val value = arrayOf(time, curr)
        data.add(value)


    }

 */
}

fun main(args: Array<String>) {
    launch<BitcoinApp>(args)
}