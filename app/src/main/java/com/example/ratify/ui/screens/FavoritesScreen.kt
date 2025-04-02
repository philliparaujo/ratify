package com.example.ratify.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ratify.ui.components.MySlider
import com.example.ratify.ui.components.MySwitch
import com.example.ratify.ui.theme.RatifyTheme
import kotlin.math.roundToInt

@Composable
fun FavoritesScreen(

) {
    var currentValue by remember { mutableLongStateOf(2L) }
    val maxValue: Long = 10;

    @Composable
    fun RenderSettings() {
        Column {
            MySwitch(
                leftText = "Artist view",
                rightText = "Album view",
                checked = false,
            )

            MySlider(
                currentValue = currentValue,
                maxValue = maxValue,
                onValueChanging = { newValue ->
                    currentValue = newValue.toFloat().roundToInt().toLong()
                },
                onValueChangeFinished = { newValue ->
                    currentValue = newValue.toFloat().roundToInt().toLong()
                }
            )
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RenderSettings()
        HorizontalDivider()
        Text(
            text = "Hello Favorites"
        )
    }
}

// Previews
@Preview(name = "Dark Favorites Screen")
@Composable
fun DarkFavoritesScreenPreview() {
    RatifyTheme(
        darkTheme = true
    ) {
        FavoritesScreen(

        )
    }
}

@Preview(name = "Light Favorites Screen")
@Composable
fun LightFavoritesScreenPreview() {
    RatifyTheme(
        darkTheme = false
    ) {
        FavoritesScreen(

        )
    }
}

@Preview(
    name = "Dark Landscape Favorites Screen",
    device = landscapeDevice
)
@Composable
fun DarkLandscapeFavoritesScreenPreview() {
    RatifyTheme(
        darkTheme = true
    ) {
        FavoritesScreen(

        )
    }
}

@Preview(
    name = "Light Landscape Favorites Screen",
    device = landscapeDevice
)
@Composable
fun LightLandscapeFavoritesScreenPreview() {
    RatifyTheme(
        darkTheme = false
    ) {
        FavoritesScreen(

        )
    }
}