package com.neonankiti.model


import android.content.res.ColorStateList
import android.view.View

internal class NumberBadge(count: Int, textColor: ColorStateList, backgroundColor: Int) : Badge(count, textColor, backgroundColor) {

    override val visibility: Int
        get() = if (count == 0) View.GONE else View.VISIBLE

    override val textSize: Int
        get() = getBadgeTextSize(count)

    override fun getCount(): String = if (count > 99) "+99" else count.toString()

    override fun getTextColor(): ColorStateList = textColor

    override fun getBackgroundColor(): Int = backgroundColor

    /**
     * Returning the sp size.
     */
    private fun getBadgeTextSize(count: Int): Int = when {
        count in 0..9 -> 12
        count in 10..99 -> 10
        count >= 99 -> 8
        else -> 0
    }
}
