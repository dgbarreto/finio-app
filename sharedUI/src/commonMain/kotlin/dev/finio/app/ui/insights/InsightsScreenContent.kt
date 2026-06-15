package dev.finio.app.ui.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import dev.finio.designsystem.component.FinioBody
import dev.finio.designsystem.component.FinioCard
import dev.finio.designsystem.component.FinioHeadline
import dev.finio.designsystem.component.FinioLabel
import dev.finio.designsystem.theme.FinioColors
import dev.finio.designsystem.theme.FinioShape
import dev.finio.designsystem.theme.FinioSpacing
import dev.finio.designsystem.theme.FinioTypography
import dev.finio.insights.domain.model.InsightsSummary
import dev.finio.insights.domain.model.MonthlyEvolution
import dev.finio.insights.domain.model.SpendingByCategory
import dev.finio.insights.presentation.InsightsViewModel
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.minus
import kotlinx.datetime.atStartOfDayIn
import kotlin.time.Clock
import org.koin.compose.koinInject
import kotlin.time.ExperimentalTime

enum class InsightsPeriod(val label: String, val evolutionMonths: Int){
    THIS_MONTH("This Month", 1),
    LAST_3_MONTHS("Last 3 Months", 3),
    LAST_6_MONTHS("Last 6 Months", 6),
    THIS_YEAR("This Year", 12);

    /**
     * Returns a pair of ISO-8601 strings (start, end) for this period based on the current date.
     * Format: yyyy-MM-ddTHH:mm:ss.SSSZ
     */
    fun toDateRange(): Pair<String, String> {
        // Get current time as kotlin.time.Instant then convert to kotlinx.datetime.Instant
        val epochMs = kotlin.time.Clock.System.now().toEpochMilliseconds()
        val now = kotlinx.datetime.Instant.fromEpochMilliseconds(epochMs)

        // compute today's LocalDate in the system timezone
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date

        val startDate = when (this) {
            THIS_MONTH -> LocalDate(today.year, today.month, 1)
            else -> {
                val monthsBack = evolutionMonths - 1
                val monthBackDate = today.minus(DatePeriod(months = monthsBack))
                LocalDate(monthBackDate.year, monthBackDate.month, 1)
            }
        }

        // start at 00:00:00.000Z (UTC) for the start date
        val startInstant = startDate.atStartOfDayIn(TimeZone.UTC)

        fun kotlinx.datetime.Instant.toIsoWithMillis(): String {
            val s = this.toString()
            return if (s.contains('.')) s else s.replace("Z", ".000Z")
        }

        return startInstant.toIsoWithMillis() to now.toIsoWithMillis()
    }
}

private val monthNames = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
)

private fun MonthlyEvolution.shortLabel() = monthNames[(month -1).coerceIn(0, 11)]

private fun formatCurrency(value: Double): String{
    val sign = if(value < 0) "-" else ""
    val abs = kotlin.math.abs(value)
    val rounded = kotlin.math.round(abs * 100) / 100
    val intPart = rounded.toLong()
    val decPart = kotlin.math.round((rounded - intPart) * 100).toInt()
    return "$sign$intPart.${decPart.toString().padStart(2, '0')}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreenContent(){
    val viewModel: InsightsViewModel = koinInject()
    val state by viewModel.state.collectAsState()
    var selectedPeriod by remember { mutableStateOf(InsightsPeriod.THIS_MONTH) }

    LaunchedEffect(selectedPeriod){
        val (start, end) = selectedPeriod.toDateRange()
        viewModel.loadAll(start, end, selectedPeriod.evolutionMonths)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(FinioSpacing.md)
    ){
        PeriodSelector(selected = selectedPeriod, onPeriodSelected = { selectedPeriod = it })

        when{
            state.isLoading -> Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = FinioSpacing.xl),
                contentAlignment = Alignment.Center
            ){ CircularProgressIndicator(color = FinioColors.primary) }

            state.error != null -> Text(
                text = state.error ?: "Failed to load insights"
            )

            else -> {
                state.summary?.let {
                    Spacer(Modifier.height(20.dp))
                    SummarySection(it)
                }

                if(state.spendingByCategory.isNotEmpty()){
                    Spacer(Modifier.height(20.dp))
                    FinioHeadline("Spending by Category")
                    SpendingByCategorySection(state.spendingByCategory)
                }

                if(state.monthlyEvolution.isNotEmpty()){
                    FinioHeadline("Monthly Evolution")
                    MonthlyEvolutionChart(state.monthlyEvolution)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodSelector(selected: InsightsPeriod, onPeriodSelected: (InsightsPeriod) -> Unit){
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }){
        OutlinedTextField(
            value = selected.label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Period", style = FinioTypography.labelSmall) },
            textStyle = FinioTypography.bodyMedium,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = FinioShape.sm,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FinioColors.primary,
                unfocusedBorderColor = FinioColors.divider,
                focusedTextColor = FinioColors.onBackground,
                unfocusedTextColor = FinioColors.onBackground
            ),
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = FinioColors.surface
        ){
            InsightsPeriod.entries.forEach { period ->
                DropdownMenuItem(
                    text = { Text(period.label, style = FinioTypography.bodyMedium, color = FinioColors.onBackground) },
                    onClick = {
                        onPeriodSelected(period)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SummarySection(summary: InsightsSummary){
    Column(verticalArrangement = Arrangement.spacedBy(FinioSpacing.xs)){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(FinioSpacing.xs)
        ){
            SummaryCard("Income", summary.totalIncome, FinioColors.success, Modifier.weight(1f))
            SummaryCard("Expense", summary.totalExpenses, FinioColors.error, Modifier.weight(1f))
            SummaryCard(
                "Balance",
                summary.balance,
                if (summary.balance >= 0) FinioColors.success else FinioColors.error,
                Modifier.weight(1f)
            )
        }
        summary.topCategory?.let {
            FinioLabel("Top category: $it")
        }
    }
}

@Composable
private fun SummaryCard(label: String, value: Double, color: Color, modifier: Modifier = Modifier){
    FinioCard(modifier = modifier){
        Column( verticalArrangement = Arrangement.spacedBy(FinioSpacing.xxs)){
            FinioLabel(label)
            Text(
                formatCurrency(value),
                style = FinioTypography.titleMedium,
                color = color
            )
        }
    }
}

@Composable
private fun SpendingByCategorySection(data: List<SpendingByCategory>){
    val colors = remember(data.size){
        List(data.size) { index -> Color.hsv(index * 360f / data.size.coerceAtLeast(1), 0.55f, 0.85f) }
    }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 20.dp)){
        Canvas(modifier = Modifier.size(140.dp)){
            val strokeWith = size.minDimension * 0.22f
            val diameter = size.minDimension - strokeWith
            var startAngle = -90f
            data.forEachIndexed { index, item ->
                val sweep = item.percentage / 100f * 360f
                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    size = Size(diameter, diameter),
                    style = Stroke(width = strokeWith, cap = StrokeCap.Butt)
                )
                startAngle += sweep
            }
        }

        Spacer(Modifier.width(FinioSpacing.md))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(FinioSpacing.xs)
        ){
            data.forEachIndexed { index, item ->
                Row(verticalAlignment = Alignment.CenterVertically){
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(FinioShape.xs)
                            .background(colors[index])
                    )
                    Spacer(Modifier.width(FinioSpacing.xxs))
                    FinioBody("${item.category} · ${item.percentage}% · ${formatCurrency(item.total)}")
                }
            }
        }
    }
}

@Composable
private fun MonthlyEvolutionChart(data: List<MonthlyEvolution>){
    val maxValue = remember(data){
        data.maxOfOrNull { maxOf(it.income, it.expenses) }?.takeIf { it > 0 } ?: 1.0
    }

    Column{
        Row(
            modifier = Modifier.fillMaxWidth().height(160.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ){
            data.forEach { item ->
                Row(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    horizontalArrangement = Arrangement.spacedBy(FinioSpacing.xxs),
                    verticalAlignment = Alignment.Bottom
                ){
                    Bar((item.income / maxValue).toFloat(), FinioColors.success, Modifier.weight(1f))
                    Bar((item.expenses / maxValue).toFloat(), FinioColors.error, Modifier.weight(1f))
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            data.forEach { item ->
                Text(
                    item.shortLabel(),
                    style = FinioTypography.labelSmall,
                    color = FinioColors.subtle,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(FinioSpacing.xs))

        Row(horizontalArrangement = Arrangement.spacedBy(FinioSpacing.md)){
            LegendDot(FinioColors.success, "Income")
            LegendDot(FinioColors.error, "Expenses")
        }
    }
}

@Composable
private fun Bar(heightFraction: Float, color: Color, modifier: Modifier){
    Box(
        modifier = modifier
            .fillMaxHeight(heightFraction.coerceIn(0f, 1f))
            .clip(FinioShape.xs)
            .background(color)
    )
}

@Composable
private fun LegendDot(color: Color, label: String){
    Row(verticalAlignment = Alignment.CenterVertically){
        Box(modifier = Modifier.size(10.dp).clip(FinioShape.xs).background(color))
        Spacer(Modifier.width(FinioSpacing.xxs))
        FinioLabel(label)
    }
}