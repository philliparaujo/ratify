package com.example.ratify.mocks

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Light Portrait", uiMode = Configuration.UI_MODE_NIGHT_NO, device = PORTRAIT_DEVICE)
@Preview(name = "Dark Portrait", uiMode = Configuration.UI_MODE_NIGHT_YES, device = PORTRAIT_DEVICE)
@Preview(name = "Light Landscape", uiMode = Configuration.UI_MODE_NIGHT_NO, device = LANDSCAPE_DEVICE)
@Preview(name = "Dark Landscape", uiMode = Configuration.UI_MODE_NIGHT_YES, device = LANDSCAPE_DEVICE)
@Preview(name = "Dark Tablet Portrait", uiMode = Configuration.UI_MODE_NIGHT_YES, device = TAB_S6_LITE_PORTRAIT)
@Preview(name = "Dark Tablet Landscape", uiMode = Configuration.UI_MODE_NIGHT_YES, device = TAB_S6_LITE_LANDSCAPE)
annotation class PreviewSuite
