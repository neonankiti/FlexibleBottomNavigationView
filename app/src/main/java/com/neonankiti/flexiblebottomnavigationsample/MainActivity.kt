package com.neonankiti.flexiblebottomnavigationsample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.neonankiti.android.support.design.widget.FlexibleBottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<FlexibleBottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.inflateMenu(R.menu.menu_bottom_navigation_view)

        // When you want to stop animation
        bottomNavigationView.enableShiftMode(false)

        // Add your badge with item resource id.
        bottomNavigationView.setItemBadgeCount(R.id.nav1, 50)

        // When you change the text bottom_nav_item_color of badge
        bottomNavigationView.setItemBadgeTextColor(ContextCompat.getColorStateList(this, android.R.color.white))

        // When you change the background bottom_nav_item_color of badge
        bottomNavigationView.setItemBadgeBackgroundResource(android.R.color.holo_orange_dark)

    }
}
