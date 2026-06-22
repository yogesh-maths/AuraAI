package com.yogesh.auraai.core.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAdComposable(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    val adWidth = configuration.screenWidthDp

    AndroidView(
        modifier = modifier,
        factory = {
            AdView(context).apply {

                setAdSize(
                    AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                        context,
                        adWidth
                    )
                )

                // Google Test Banner Ad Unit ID
                adUnitId = "ca-app-pub-3940256099942544/6300978111"

                loadAd(
                    AdRequest.Builder().build()
                )
            }
        }
    )
}