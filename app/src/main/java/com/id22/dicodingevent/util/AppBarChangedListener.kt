package com.id22.dicodingevent.util

import android.content.res.Configuration
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.ColorUtils
import com.google.android.material.appbar.AppBarLayout
import com.id22.dicodingevent.R
import kotlin.math.abs

fun AppBarLayout.onScrollListener(toolbar: Toolbar) {
    val lightColor = context.getColor(R.color.colorWhite)
    val darkColor = context.getColor(R.color.colorBackground)

    val isDarkMode =
        context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

    this.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
        val scrollRange = appBarLayout.totalScrollRange
        val percentage = abs(verticalOffset.toDouble()).toFloat() / scrollRange

        val baseColor = if (isDarkMode) darkColor else lightColor
        val alpha = (255 * percentage).toInt()
        val color = ColorUtils.setAlphaComponent(baseColor, alpha)

        toolbar.setBackgroundColor(color)
    }
}