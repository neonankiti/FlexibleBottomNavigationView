package com.neonankiti.android.support.design.utils

import android.annotation.SuppressLint
import android.util.Log

import com.neonankiti.android.support.design.internal.FlexibleBottomNavigationItemView
import com.neonankiti.android.support.design.internal.FlexibleBottomNavigationMenuView
import com.neonankiti.android.support.design.widget.FlexibleBottomNavigationView

internal object FlexibleBottomNavigationViewHelper {

    @SuppressLint("RestrictedApi")
    fun disableShiftMode(view: FlexibleBottomNavigationView) {
        val menuView = view.getChildAt(0) as FlexibleBottomNavigationMenuView
        try {
            val shiftingMode = menuView.javaClass.getDeclaredField("mShiftingMode")
            shiftingMode.isAccessible = true
            shiftingMode.setBoolean(menuView, false)
            shiftingMode.isAccessible = false
            for (i in 0 until menuView.childCount) {
                val item = menuView.getChildAt(i) as FlexibleBottomNavigationItemView

                item.setShiftingMode(false)
                // set once again checked value, so view will be updated

                item.setChecked(item.itemData!!.isChecked)
            }
        } catch (e: NoSuchFieldException) {
            Log.e("BNVHelper", "Unable to get shift mode field", e)
        } catch (e: IllegalAccessException) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e)
        }

    }

}
