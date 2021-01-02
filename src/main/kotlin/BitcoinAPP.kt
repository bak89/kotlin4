import com.sun.tools.javac.Main
import javafx.collections.FXCollections.observableArrayList
import javafx.concurrent.Task
import javafx.geometry.Pos
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.stage.Stage
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


import tornadofx.*
import java.io.File
import java.util.*

class BitcoinApp : App(MainView::class) {
    override fun start(stage: Stage) {
        stage.width = 550.0
        stage.height = 950.0
        super.start(stage)
    }
}

class MainView : View("Bitcoin Viewer") {

    //private var bitcoin = Bitcoin(Currency.USD)
    //private var bitcoinInfo: Task<BitcoinInfo>? = null
    private var price by singleAssign<Price>()
    private var bitcoinPrice: Label by singleAssign()
    private var currencyLabel: Label by singleAssign()
    private var dateUpdate: Label by singleAssign()
    private var status: Label by singleAssign()

    var tblItems: TableView<String> by singleAssign()
    var export: Button by singleAssign()
    var dataGraph: String? = ""
    var area = areachart("Bitcoin History", CategoryAxis(), NumberAxis()) {}

    //var bitcoinProperty = BitcoinProperty(getTime(), bitcoin.currency, getRate(bitcoin.currency))
    //private var history = ArrayList<BitcoinInfo>().asObservable()
    private var table = ArrayList<String>().asObservable()


    private val data = observableArrayList(
        //arrayOf(history[0].updated, history[0].prices),
        //arrayOf("BBB", "222"),
        arrayOf("", "", "")
    )


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
                        button("JPY") {
                            prefWidth = 70.0
                            action {
                                setCurrency(Currency.JPY)
                            }
                        }
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
                        series("USD") {
                            data("13.31", 26785.105)
                            data("13:32", 26788.2)
                            data("13:33", 26790.3)

                        }
                        series("GBP") {
                            data("13:31", 19869.469)
                            data("13:32", 19859.469)
                            data("13:33", 19870.6)
                        }
                        series("EUR") {
                            table.forEach { item ->
                                data(item, 2222222)
                            }
                        }
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
                    //tblItems =tableview(data) {
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
                        /*column("Currency 3", String::class) {
                            value { it.value }
                        }*/
                        /* column("Time", bitcoinProperty::time)
                        column("Time", BitcoinInfo::updated)
                        column("Time", Number::updated)
                        column("Currency 1", Number::)
                        column("Currency 2", Item::price)
                          column("Currency 3", Item::taxable)*/

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
                                    //writeToFile()
                                    status.text = "Successfully exported"
                                    status.textFill = Color.DARKGREEN
                                } catch (e: Exception) {
                                    //status.text = "Attention: ${e.message}."
                                    //status.textFill = Color.RED
                                }
                                //updateUI()
                            }
                        }

                    }

                }
            }
        }
    }

    private fun setCurrency(currency: Currency) {

/*        MainScope().launch {
            val one = withContext(Dispatchers.Main) { fetchPrice(currency) }
        }*/
        /* val job = CoroutineScope(Dispatchers.Main).launch {
             while (true) {
                 delay(5000)
                 val curr = fetchPrice(currency)
                 bitcoinPrice.text = curr.value.toString()
                 dateUpdate.text = curr.time.toString()
                 currencyLabel.text = currency.toString()
                 status.text = "Updated."
                 status.textFill = Color.DARKGREEN
                 //add to array?
                 val uno = arrayOf(curr)
                 println("${uno[0].time}, ${uno[0].value}")
                 data.add(arrayOf(uno[0].time.toString(), uno[0].value.toString(), currency.name))

             }
         }

         job.cancel()*/

        runBlocking {
            status.text = "Updating..."
            status.textFill = Color.ORANGE
            val curr = fetchPrice(currency)
            bitcoinPrice.text = curr.value.toString()
            dateUpdate.text = curr.time.toString()
            currencyLabel.text = currency.toString()
            status.text = "Updated."
            status.textFill = Color.DARKGREEN
            //add to array?
            val uno = arrayOf(curr)
            println("${uno[0].time}, ${uno[0].value}")
            data.add(arrayOf(uno[0].time.toString(), uno[0].value.toString(), currency.name))
            dataGraph = uno[0].time.toString()
        }
    }
/*
    private fun writeToFile() {
        val fileName = "myHistory.txt"
        val myfile = File(fileName)

        val content = Json.encodeToString(listOf(bitcoin.getCurrent3Price(), bitcoin.getCurrent2Price()))

        myfile.writeText(content)

        status.text = "Written on file"
        status.textFill = Color.ORANGE
    }

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