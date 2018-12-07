package com.beibeilab.keepin.util

import android.graphics.drawable.GradientDrawable

class Utils {
    companion object {
        fun createOvalDrawable(color :Int, radius: Int) = GradientDrawable().apply {
            setSize(radius, radius)
            shape = GradientDrawable.OVAL
            setColor(color)
        }
    }
}