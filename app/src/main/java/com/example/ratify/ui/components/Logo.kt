package com.example.ratify.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ratify.R
import com.example.ratify.mocks.MyPreview
import com.example.ratify.mocks.PreviewSuite

@Composable
fun Logo(
    darkTheme: Boolean,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(
                if (darkTheme) R.drawable.ratify_logo_darktheme
                else R.drawable.ratify_logo_lighttheme
            ),
            contentDescription = "App logo",
            modifier = Modifier
        )
        Image(
            painter = painterResource(
                R.drawable.ratify_logo_stars
            ),
            contentDescription = "App logo",
            colorFilter = ColorFilter.tint(primaryColor),
            modifier = Modifier
        )

    }
}

// Previews
@PreviewSuite
@Composable
fun LogoPreviews() {
    MyPreview {
        Column(
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Logo(darkTheme = true, primaryColor = Color.Cyan)
            Logo(darkTheme = true, primaryColor = Color.Green)
            Logo(darkTheme = true, primaryColor = Color.Magenta)
            Logo(darkTheme = true, primaryColor = Color.Yellow)
            Logo(darkTheme = true, primaryColor = Color.LightGray)
        }
    }
}