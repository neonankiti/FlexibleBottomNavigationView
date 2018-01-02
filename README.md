# FlexibleBottomNavigationView

This is Custome BottomNavigationView

## Image

<img src="https://github.com/neonankiti/FlexibleBottomNavigationView/blob/master/app/image/sceen_shot.gif" />


## What to solve

The existing BottomNavigationView doesn't support flexible request from developer.
This provide a bit more options for depelovers.

- notification badge 
- handling animation for transition

## How to use

write as follow in your app module build.gradle

```build.gradle
dependencies{
    implementation project(':flexiblebottomnavigationview')
}
```

write FlexibleBottomNavigationView in xml where you want to use as BottomNavigationView.

```activity_main.xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.neonankiti.flexiblebottomnavigationsample.MainActivity">

    <com.neonankiti.android.support.design.widget.FlexibleBottomNavigationView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:badgeBackgroundColor="@color/colorAccent"
        app:badgeTextColor="@android:color/white"
        app:layout_constraintBottom_toBottomOf="parent" />

</android.support.constraint.ConstraintLayout>
```

Currently attributes in xml I support are all the same as BottomNavigationView.
Additionals are only bewlow.

|attrs|format|example|
|:--|:--|:--|
|badgeBackgroundColor|color|@color/colorAccent|
|badgeTextColor|color|@android:color/white|

```MainActivity.kt
val bottomNavigationView = findViewById<FlexibleBottomNavigationView>(R.id.bottom_navigation_view)
bottomNavigationView.inflateMenu(R.menu.menu_bottom_navigation_view)

// When you want to stop animation
 bottomNavigationView.enableShiftMode(false)

// Add your badge with item resource id.
 bottomNavigationView.setItemBadgeCount(R.id.nav1, 50)

// When you change the text color of badge
bottomNavigationView.setItemBadgeTextColor(ContextCompat.getColorStateList(this, R.color.colorPrimary))

// When you change the background color of badge
bottomNavigationView.setItemBadgeBackgroundResource(android.R.color.holo_red_dark)
```

## Milestones
- TBD

## Request
Please let me know if you have any questions or request. 
yk.nanri@gmail.com
