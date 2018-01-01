package com.neonankiti.android.support.design.utils

import android.content.Context

internal object ThemeUtils {

    private val APPCOMPAT_CHECK_ATTRS = intArrayOf(android.support.v7.appcompat.R.attr.colorPrimary)

    fun checkAppCompatTheme(context: Context) {
        val a = context.obtainStyledAttributes(APPCOMPAT_CHECK_ATTRS)
        val failed = !a.hasValue(0)
        a.recycle()
        if (failed) {
            throw IllegalArgumentException("You need to use a Theme.AppCompat theme " + "(or descendant) with the design library.")
        }
    }
}
