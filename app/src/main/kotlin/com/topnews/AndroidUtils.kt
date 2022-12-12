package com.topnews

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics

fun convertDpToPixel(dp: Float, context: Context): Int {
    val resources: Resources = context.resources
    val metrics: DisplayMetrics = resources.displayMetrics
    return (dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}
