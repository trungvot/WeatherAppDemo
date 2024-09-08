package com.trungvothanh.weatherapplication.helpers

import android.view.Window
import android.view.WindowManager

class StatusBarHelper {
    fun makeTransparent(window: Window) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
}