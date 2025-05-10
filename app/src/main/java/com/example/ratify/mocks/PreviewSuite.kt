package com.example.ratify.mocks

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Light Portrait", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark Portrait", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light Landscape", uiMode = Configuration.UI_MODE_NIGHT_NO, device = LANDSCAPE_DEVICE)
@Preview(name = "Dark Landscape", uiMode = Configuration.UI_MODE_NIGHT_YES, device = LANDSCAPE_DEVICE)
annotation class PreviewSuite
