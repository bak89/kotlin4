import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javafx.application.Platform
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
import tornadofx.*
import javafx.collections.ObservableList
import javafx.scene.chart.XYChart
import java.io.FileWriter
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlinx.coroutines.launch
import kotlin.concurrent.fixedRateTimer


class BitcoinApp : App(MainView::class) {
    override fun start(stage: Stage) {
        stage.width = 650.0
        stage.height = 1000.0
        super.start(stage)
    }
}

class MainView : View("Bitcoin Viewer") {
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
    private var price: Price by singleAssign()
    private var job = CoroutineScope(Dispatchers.Default)

    /***
     * Main View
     */
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

/*
    private fun updateGUI2(currency: Currency) {
        Platform.runLater(Runnable {
            update(currency)
        })

    }

    private fun update(currency: Currency) {
        job.launch {
            fixedRateTimer(
                name = "BitCoin-timer",
                initialDelay = 1000, period = 2000, daemon = true
            ) { setCurrency(currency) }
        }

        stopUpdates()
    }

    private fun stopUpdates() {
        job.cancel()
        job = CoroutineScope(Dispatchers.Default)
    }*/

    private fun setCurrency(currency: Currency) {
        job.launch {
            while (true) {
                price = fetchPrice(currency)

            }
        }
        updateGUI(price, currency)
    }

    /***
     * Function that is called to set a currency, and updates the GUI
     */
    /* private fun setCurrency(currency: Currency) {
          runBlocking {
              val curr = fetchPrice(currency)
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
              dataChart.xValue =
                  curr.time.format(DateTimeFormatter.ISO_LOCAL_TIME.withZone(ZoneId.of("Europe/Paris")))
              dataChart.yValue = curr.value
              println("${dataChart.xValue}, ${dataChart.yValue}")
          }
      }*/

    private fun updateGUI(price: Price, currency: Currency) {
        status.text = "Updating..."
        status.textFill = Color.ORANGE
        bitcoinPrice.text = price.value.toString()
        dateUpdate.text = price.time.format(
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withZone(ZoneId.of("Europe/Paris"))
        )
        currencyLabel.text = currency.toString()
        status.text = "Updated."
        status.textFill = Color.DARKGREEN
        println("${price.time}, ${price.value}")
        data.add(
            arrayOf(
                price.time.format(
                    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withZone(ZoneId.of("Europe/Paris"))
                ),
                price.value.toString(),
                currency.name
            )
        )
        /* val uno = arrayOf(price)
         println("${uno[0].time}, ${uno[0].value}")
         data.add(
             arrayOf(
                 uno[0].time.format(
                     DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withZone(ZoneId.of("Europe/Paris"))
                 ),
                 uno[0].value.toString(),
                 currency.name
             )
         )*/
        dataChart.xValue =
            price.time.format(DateTimeFormatter.ISO_LOCAL_TIME.withZone(ZoneId.of("Europe/Paris")))
        dataChart.yValue = price.value
        println("${dataChart.xValue}, ${dataChart.yValue}")
        updateGraph(currency, dataChart.xValue, dataChart.yValue)
    }

    /*  private fun setCurrency(currency: Currency) {
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
              dataChart.xValue =
                  curr.time.format(DateTimeFormatter.ISO_LOCAL_TIME.withZone(ZoneId.of("Europe/Paris")))
              dataChart.yValue = curr.value
              println("${dataChart.xValue}, ${dataChart.yValue}")
              updateGraph(currency, dataChart.xValue, dataChart.yValue)
          }

      }*/


    /***
     * Function to write the Json into a File
     */
    private fun writeToFile() {
        val fileName = "myHistory.txt"
        val mapper = jacksonObjectMapper()
        val writer = FileWriter(fileName)
        mapper.writeValue(writer, data)

        status.text = "Written on file"
        status.textFill = Color.ORANGE
    }

    /***
     * Update the Graph
     */
    private fun updateGraph(currency: Currency, time: String, value: Float) {
        area.series(currency.name) {
            data(time, value)
        }
    }
}

fun main(args: Array<String>) {
    launch<BitcoinApp>(args)
}