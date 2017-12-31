package com.neonankiti.flexiblebottomnavigationview.model

import android.content.res.ColorStateList


abstract class Badge constructor(internal var count: Int,
                                 internal var textColor: ColorStateList,
                                 internal var backgroundColor: Int) {

    abstract val visibility: Int
    abstract val textSize: Int

    fun setCount(count: Int) {
        this.count = count
    }

    abstract fun getCount(): String

    abstract fun getTextColor(): ColorStateList

    abstract fun getBackgroundColor(): Int
}
