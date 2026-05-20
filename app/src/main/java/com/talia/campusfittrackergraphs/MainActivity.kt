package com.talia.campusfittrackergraphs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// vico imports used to create the line graph
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf

// this is the main activity that runs when the app opens
class MainActivity : ComponentActivity() {

    // this function runs first when the screen is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // this allows the app content to use more of the screen space
        enableEdgeToEdge()

        // this tells android that the screen will be built using jetpack compose
        setContent {

            // this calls the main composable function for the app
            CampusFitTrackerDemo()
        }
    }
}

// this data class represents one item that will appear on the graph
// each graph item needs a label and a number value
data class WellnessData(

    // this is the text label for the data point
    // example: monday, tuesday, wednesday
    val label: String,

    // this is the number that will be plotted on the graph
    // example: 7000 steps
    val value: Int
)

// this is the main app container
// it sets the material design theme and background surface
@Composable
fun CampusFitTrackerDemo() {

    // materialtheme gives the app default colours, typography, and styling
    MaterialTheme {

        // surface is used as the main background container for the app
        Surface(

            // fills the whole phone screen
            modifier = Modifier.fillMaxSize(),

            // uses the default background colour from the material theme
            color = MaterialTheme.colorScheme.background
        ) {

            // this displays the actual main screen content
            CampusFitScreen()
        }
    }
}

// this function contains the main screen layout and logic
@Composable
fun CampusFitScreen() {

    // this list stores the hardcoded step data for this week
    // each item contains a day label and a step value
    val thisWeeksSteps = listOf(
        WellnessData("Monday", 7000),
        WellnessData("Tuesday", 7093),
        WellnessData("Wednesday", 5409),
        WellnessData("Thursday", 5255),
        WellnessData("Friday", 3472),
        WellnessData("Saturday", 34223),
        WellnessData("Sunday", 8654)
    )

    // this list stores the hardcoded step data for last week
    // we use this second list so the user can compare two different weeks
    val lastWeeksSteps = listOf(
        WellnessData("Monday", 7040),
        WellnessData("Tuesday", 7098),
        WellnessData("Wednesday", 5209),
        WellnessData("Thursday", 5205),
        WellnessData("Friday", 3672),
        WellnessData("Saturday", 4223),
        WellnessData("Sunday", 26540)
    )

    // this variable stores which filter button is currently selected
    // it starts with "this week", so this week's graph is shown first
    // var is used because the value changes when a button is clicked
    // remember keeps the selected value even when the screen recomposes
    var selectedWeek by remember {
        mutableStateOf("This week")
    }

    // this chooses which list of data must be shown
    // if the user selected "this week", use thisWeeksSteps
    // otherwise, use lastWeeksSteps
    val selectedData = if (selectedWeek == "This week") {
        thisWeeksSteps
    } else {
        lastWeeksSteps
    }

    // this calculates the total number of steps for the selected week
    // sumof loops through each wellnessdata item and adds its value
    val totalSteps = selectedData.sumOf { it.value }

    // this calculates the average number of steps for the selected week
    // map extracts only the values from the wellnessdata objects
    // average calculates the mean value
    // toint converts the result from decimal to whole number
    val aveSteps = selectedData
        .map { it.value }
        .average()
        .toInt()

    // this finds the day with the highest number of steps
    // maxbyornull checks the value property and returns the item with the largest value
    val highestDay = selectedData.maxByOrNull { it.value }

    // scaffold gives the screen a proper material layout structure
    // paddingvalues are used so the content does not overlap system bars
    Scaffold { paddingValues ->

        // column places ui elements vertically from top to bottom
        Column(
            modifier = Modifier

                // makes the column take up the full screen
                .fillMaxSize()

                // applies padding from scaffold
                .padding(paddingValues)

                // adds extra padding around the screen content
                .padding(20.dp),

            // adds space between each item inside the column
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // this displays the app title
            Text(
                text = "Campus Fit Tracker.",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            // this shows the summary card
            // we pass selectedweek so the card knows which week is being viewed
            // we pass totalsteps so the card can show the total
            // we pass avesteps so the card can show the average
            // we pass highestday so the card can show the best day
            SummaryCard(
                selectedWeek = selectedWeek,
                totalsteps = totalSteps,
                aveSteps = aveSteps,
                highestDay = highestDay
            )

            // this displays a heading above the graph
            Text(
                text = "Daily steps graph:",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // this displays the line graph
            // selecteddata is passed into the chart so it knows what values to draw
            StepsLineChart(
                data = selectedData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            )

            // row places the filter buttons next to each other horizontally
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                // if this week is currently selected, show a filled button
                // this visually tells the user that the filter is active
                if (selectedWeek == "This week") {
                    Button(
                        onClick = {

                            // this updates the selected week state
                            // when this changes, compose redraws the screen
                            selectedWeek = "This week"
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("This week")
                    }
                } else {

                    // if this week is not selected, show an outlined button
                    OutlinedButton(
                        onClick = {

                            // this changes the selected data back to this week
                            selectedWeek = "This week"
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("This week")
                    }
                }

                // if last week is currently selected, show a filled button
                if (selectedWeek == "Last week") {
                    Button(
                        onClick = {

                            // this updates the selected week state to last week
                            selectedWeek = "Last week"
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Last week")
                    }
                } else {

                    // if last week is not selected, show an outlined button
                    OutlinedButton(
                        onClick = {

                            // this changes the selected data to last week
                            selectedWeek = "Last week"
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Last week")
                    }
                }
            }

            // this gives the user a short explanation of what the graph is showing
            Text(
                text = "This graph shows daily step progress. " +
                        "Use the filter buttons to compare this week and last week.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun StepsLineChart(
    // this receives the selected data from campusfitscreen
    // it will either receive thisweekssteps or lastweekssteps
    data: List<WellnessData>,

    // this allows the parent composable to control size and layout
    modifier: Modifier = Modifier
) {

    // entrymodelof converts normal kotlin numbers into a format vico can draw
    // data.map gets only the step values from the wellnessdata list
    // tofloat converts the int values to float values because vico works with chart numbers
    // totypedarray converts the list into an array
    // the * is the spread operator, which passes each value separately into entrymodelof
    val chartEntryModel = entryModelOf(
        *data.map { it.value.toFloat() }.toTypedArray()
    )

    // this card creates a neat container around the graph
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        // this column places the chart and the labels vertically
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // this is the actual vico chart component
            Chart(

                // linechart tells vico to draw the data as a line graph
                chart = lineChart(),

                // model is the graph data that vico must display
                // chartentrymodel was created from the selected data list above
                model = chartEntryModel,

                // startaxis displays the vertical axis on the left side
                // this shows the step values
                startAxis = startAxis(),

                // bottomaxis displays the horizontal axis at the bottom
                // this helps users understand the x-axis area
                bottomAxis = bottomAxis(),

                // this controls the size of the chart inside the card
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )

            // this adds space between the graph and the day labels
            Spacer(modifier = Modifier.height(8.dp))

            // this row shows the day labels underneath the chart
            // vico is displaying the values, and this row helps label each point
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // this loops through every item in the data list
                // each item label is displayed under the chart
                data.forEach { item ->
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    // this receives the selected week text from campusfitscreen
    selectedWeek: String,

    // this receives the calculated total steps from campusfitscreen
    totalsteps: Int,

    // this receives the calculated average steps from campusfitscreen
    aveSteps: Int,

    // this receives the highest day object from campusfitscreen
    // it is nullable because the list could technically be empty
    highestDay: WellnessData?
) {

    // this card displays the summary information neatly
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {

        // column places the summary text items vertically
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // this shows which week the summary belongs to
            Text(
                text = selectedWeek,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // this shows the total number of steps for the selected week
            Text(text = "Total steps -> $totalsteps")

            // this shows the average number of steps for the selected week
            Text(text = "Average steps -> $aveSteps")

            // this checks that highestday is not null before using it
            // if highestday is null, the app will not crash
            if (highestDay != null) {
                Text(
                    text = "Highest Day -> ${highestDay.label} with ${highestDay.value} steps"
                )
            }
        }
    }
}