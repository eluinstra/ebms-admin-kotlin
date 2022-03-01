package nl.clockwork.ebms.admin.views.message

import com.github.appreciated.apexcharts.ApexCharts
import com.github.appreciated.apexcharts.ApexChartsBuilder
import com.github.appreciated.apexcharts.config.builder.*
import com.github.appreciated.apexcharts.config.chart.Type
import com.github.appreciated.apexcharts.config.chart.builder.ZoomBuilder
import com.github.appreciated.apexcharts.config.legend.Labels
import com.github.appreciated.apexcharts.config.legend.Position
import com.github.appreciated.apexcharts.config.stroke.Curve
import com.github.appreciated.apexcharts.config.subtitle.Align
import com.github.appreciated.apexcharts.config.xaxis.builder.TitleBuilder
import com.github.appreciated.apexcharts.helper.Series
import com.github.mvysny.karibudsl.v10.KComposite
import com.github.mvysny.karibudsl.v10.beanValidationBinder
import com.github.mvysny.karibudsl.v10.h2
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent
import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.HasValue.ValueChangeListener
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import nl.clockwork.ebms.admin.components.WithElement
import nl.clockwork.ebms.admin.dao.EbMSDAO
import nl.clockwork.ebms.admin.views.MainLayout
import nl.clockwork.ebms.admin.views.WithBean
import java.time.LocalDateTime
import java.time.temporal.TemporalUnit
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream


@Route(value = "trafficChart", layout = MainLayout::class)
@PageTitle("Traffic Chart")
class TrafficChartView(
    private val config: TrafficChartConfig = TrafficChartConfig.of(TimeUnit.DAY, EbMSMessageTrafficChartOption.ALL),
    private var chart: Component = updateApexCharts(createDefaultApexCharts(), config) //new Div()
) : KComposite(), WithBean, WithElement {
    private val root = ui {
        verticalLayout {
            setSizeFull()
            h2(getTranslation("trafficChart"))
            val binder = beanValidationBinder<TrafficChartConfig>()
            binder.readBean(config)
            //TODO use binder
            createDateBar(binder,config)
            chart
            createChartBar(binder,config)
        }
    }

    private fun createDateBar(binder: Binder<TrafficChartConfig>, config: TrafficChartConfig): Component {
        val result = HorizontalLayout()
        result.setWidthFull()
        result.alignItems = FlexComponent.Alignment.STRETCH
        result.add(createButton(Icon(VaadinIcon.STEP_BACKWARD)) {
            config.previousPeriod()
            updateChart(config)
        })
        result.add( /* bind(binder, */createComboBox(listOf(*TimeUnit.values()), config.timeUnit) {
            config.timeUnit = it.value // FIXME
            config.resetFrom()
            updateChart(config)
        } /* ,"timeUnit") */)
        result.add(createButton(Icon(VaadinIcon.STEP_FORWARD)) {
            config.nextPeriod()
            updateChart(config)
        })
        return result
    }

    private fun createButton(icon: Icon, clickListener: ComponentEventListener<ClickEvent<Icon?>>): Component? {
        icon.addClickListener(clickListener)
        return icon
    }

    private fun <T> createComboBox(
        items: List<T>,
        value: T,
        changeListener: ValueChangeListener<in ComponentValueChangeEvent<ComboBox<T>, T>>
    ): ComboBox<T> {
        val result = ComboBox<T>()
        result.setItems(items)
        result.value = value
        result.addValueChangeListener(changeListener)
        return result
    }

    private fun updateChart(config: TrafficChartConfig) {
        val newChart = updateApexCharts(createDefaultApexCharts(), config)
// TODO       this@TrafficChartView.replace(chart, newChart)
        chart = newChart
    }

    private fun createChartBar(binder: Binder<TrafficChartConfig>, config: TrafficChartConfig): Component? {
        val result = HorizontalLayout()
        result.setWidthFull()
        result.alignItems = FlexComponent.Alignment.END
        result.add( /* bind(binder, */createComboBox(
            listOf(EbMSMessageTrafficChartOption.values()),
            config.ebMSMessageTrafficChartOption
        ) { event ->
            config.ebMSMessageTrafficChartOption = event.value as EbMSMessageTrafficChartOption // FIXME
            updateChart(config)
        } /* ,"ebMSMessageTrafficChartOption") */)
        return result
    }

    companion object {
        private fun createDefaultApexCharts(): ApexCharts {
            return ApexChartsBuilder()
                .withChart(
                    ChartBuilder.get()
                        .withType(Type.line)
                        .withZoom(
                            ZoomBuilder.get()
                                .withEnabled(false)
                                .build()
                        )
                        .build()
                )
                .withLegend(
                    LegendBuilder.get()
                        .withLabels(createLabels())
                        .withPosition(Position.top)
                        .build()
                )
                .withYaxis(
                    YAxisBuilder.get()
                        .withTitle(
                            com.github.appreciated.apexcharts.config.yaxis.builder.TitleBuilder.get()
                                .withText("Messages")
                                .build()
                        )
                        .build()
                )
                .build()
        }

        private fun createLabels(): Labels =
            Labels().apply {
                useSeriesColors = true
            }

        private fun updateApexCharts(charts: ApexCharts, config: TrafficChartConfig): ApexCharts {
            val dateStrings = getDateStrings(config)
            charts.setTitle(
                TitleSubtitleBuilder.get()
                    .withText(TrafficChartConfig.createChartTitle(config))
                    .withAlign(Align.center)
                    .build()
            )
            charts.setStroke(
                StrokeBuilder.get()
                    .withCurve(Curve.straight)
                    .withColors(*getColors(config.ebMSMessageTrafficChartOption))
                    .build()
            )
            charts.setXaxis(
                XAxisBuilder.get()
                    .withCategories(dateStrings)
                    .withTitle(
                        TitleBuilder.get()
                            .withText(config.timeUnit.units)
                            .build()
                    )
                    .build()
            )
            charts.updateSeries(
                *createSeries(
                    config.ebMSMessageTrafficChartOption.ebMSMessageTrafficChartSeries
                ) { serie: EbMSMessageTrafficChartSerie ->
                    getMessages(
                        config,
                        dateStrings,
                        serie
                    )
                })
            return charts
        }

        private fun getDateStrings(config: TrafficChartConfig): List<String> =
            calculateDates(config.timeUnit.unit, config.from, config.to)
                .map { config.timeUnit.timeUnitDateFormat.format(it) }
                .toList()

        private fun calculateDates(unit: TemporalUnit, from: LocalDateTime, to: LocalDateTime): List<LocalDateTime> =
            //TODO: improve
            Stream.iterate(from) { it.plus(unit.duration) }
                .limit(from.until(to, unit))
                .collect(Collectors.toList())

        private fun getColors(trafficChartOption: EbMSMessageTrafficChartOption): Array<String> =
            trafficChartOption.ebMSMessageTrafficChartSeries
                .map(EbMSMessageTrafficChartSerie::color)
                .toTypedArray()

        private fun createSeries(
            series: Array<EbMSMessageTrafficChartSerie>,
            dataBySerie: (s: EbMSMessageTrafficChartSerie) -> Array<Int>
        ): Array<Series<Int>> {
            return Arrays.stream(series).map { serie ->
                createSerie(serie.name) {
                    dataBySerie(serie)
                }
            }.toArray { size -> arrayOfNulls(size) }
        }

        private fun createSerie(name: String, dataSupplier: () -> Array<Int>): Series<Int> =
            Series<Int>().apply {
                setName(name)
                setData(dataSupplier())
            }

        private fun getMessages(
            config: TrafficChartConfig,
            dates: List<String>,
            serie: EbMSMessageTrafficChartSerie
        ): Array<Int> {
            val messageTraffic: Map<Int, Int> =
                //TODO: fix
                WithBean.getBean("ebMSAdminDAO", EbMSDAO::class.java).selectMessageTraffic(config.from, config.to, config.timeUnit, *serie.ebMSMessageStatuses)
            return dates.stream().map { date: String ->
                messageTraffic[date.toInt()] ?: 0
            }.toArray { size -> arrayOfNulls(size) }
        }

    }
}