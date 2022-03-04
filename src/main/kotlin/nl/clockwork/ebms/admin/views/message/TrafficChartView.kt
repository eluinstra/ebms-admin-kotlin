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
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import nl.clockwork.ebms.admin.components.WithElement
import nl.clockwork.ebms.admin.components.backButton
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
    private var chart: Component = updateApexCharts(defaultApexCharts(), config)
) : KComposite(), WithBean, WithElement {
    private val root = ui {
        verticalLayout {
            setSizeFull()
            h2(getTranslation("trafficChart"))
            add(dateBar(config))
            add(chart)
            add(chartBar(config))
            backButton(getTranslation("cmd.back"))
        }
    }

    private fun dateBar(config: TrafficChartConfig): Component =
        HorizontalLayout().apply {
            setWidthFull()
            alignItems = FlexComponent.Alignment.STRETCH
            add(button(Icon(VaadinIcon.STEP_BACKWARD)) {
                config.previousPeriod()
                updateChart(config)
            })
            add(comboBox(listOf(*TimeUnit.values()), config.timeUnit) {
                config.timeUnit = it.value
                config.resetFrom()
                updateChart(config)
            } /* ,"timeUnit") */)
            add(button(Icon(VaadinIcon.STEP_FORWARD)) {
                config.nextPeriod()
                updateChart(config)
            })
        }

    private fun button(icon: Icon, clickListener: ComponentEventListener<ClickEvent<Icon?>>): Component =
        icon.apply {
            addClickListener(clickListener)
        }

    private fun <T> comboBox(
        items: List<T>,
        initialValue: T,
        changeListener: ValueChangeListener<in ComponentValueChangeEvent<ComboBox<T>, T>>
    ): ComboBox<T> =
        ComboBox<T>().apply {
            setItems(items)
            value = initialValue
            addValueChangeListener(changeListener)
        }

    private fun updateChart(config: TrafficChartConfig) {
        val newChart = updateApexCharts(defaultApexCharts(), config)
        root.replace(chart, newChart)
        chart = newChart
    }

    private fun chartBar(config: TrafficChartConfig): Component =
        HorizontalLayout().apply {
            setWidthFull()
            alignItems = FlexComponent.Alignment.END
            add(comboBox(
                listOf(*EbMSMessageTrafficChartOption.values()),
                config.ebMSMessageTrafficChartOption
            ) { event ->
                config.ebMSMessageTrafficChartOption = event.value as EbMSMessageTrafficChartOption // FIXME
                updateChart(config)
            } /* ,"ebMSMessageTrafficChartOption") */)
        }

    companion object {
        private fun defaultApexCharts(): ApexCharts =
            ApexChartsBuilder()
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
                        .withLabels(labels())
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

        private fun labels(): Labels =
            Labels().apply {
                useSeriesColors = true
            }

        private fun updateApexCharts(charts: ApexCharts, config: TrafficChartConfig): ApexCharts {
            val dateStrings = dateStrings(config)
            charts.setTitle(
                TitleSubtitleBuilder.get()
                    .withText(TrafficChartConfig.createChartTitle(config))
                    .withAlign(Align.center)
                    .build()
            )
            charts.setStroke(
                StrokeBuilder.get()
                    .withCurve(Curve.straight)
                    .withColors(*colors(config.ebMSMessageTrafficChartOption))
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
                *series(
                    config.ebMSMessageTrafficChartOption.ebMSMessageTrafficChartSeries
                ) { serie: EbMSMessageTrafficChartSerie ->
                    messages(
                        config,
                        dateStrings,
                        serie
                    )
                })
            return charts
        }

        private fun dateStrings(config: TrafficChartConfig): List<String> =
            dates(config.timeUnit.timeUnit, config.from, config.to)
                .map { config.timeUnit.timeUnitDateFormat.format(it) }
                .toList()

        private fun dates(unit: TemporalUnit, from: LocalDateTime, to: LocalDateTime): List<LocalDateTime> =
            //TODO: improve
            Stream.iterate(from) { it.plus(unit.duration) }
                .limit(from.until(to, unit))
                .collect(Collectors.toList())

        private fun colors(trafficChartOption: EbMSMessageTrafficChartOption): Array<String> =
            trafficChartOption.ebMSMessageTrafficChartSeries
                .map(EbMSMessageTrafficChartSerie::color)
                .toTypedArray()

        private fun series(
            series: Array<EbMSMessageTrafficChartSerie>,
            dataBySerie: (s: EbMSMessageTrafficChartSerie) -> Array<Int>
        ): Array<Series<Int>> =
            Arrays.stream(series).map { serie ->
                serie(serie.name) {
                    dataBySerie(serie)
                }
            }.toArray { size -> arrayOfNulls(size) }

        private fun serie(name: String, dataSupplier: () -> Array<Int>): Series<Int> =
            Series<Int>().apply {
                setName(name)
                setData(dataSupplier())
            }

        private fun messages(
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