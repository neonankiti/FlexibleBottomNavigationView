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

## Milestones
- TBD

## Request
Please let me know if you have any questions or request. 
yk.nanri@gmail.com
