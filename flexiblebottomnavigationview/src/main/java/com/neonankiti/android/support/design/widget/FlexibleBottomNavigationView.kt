package com.neonankiti.android.support.design.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.design.internal.BottomNavigationMenu
import android.support.v4.content.ContextCompat
import android.support.v4.view.AbsSavedState
import android.support.v4.view.ViewCompat
import android.support.v7.content.res.AppCompatResources
import android.support.v7.view.SupportMenuInflater
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.widget.TintTypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

import com.neonankiti.flexiblebottomnavigationview.R
import com.neonankiti.android.support.design.internal.FlexibleBottomNavigationMenuView
import com.neonankiti.android.support.design.internal.FlexibleBottomNavigationPresenter
import com.neonankiti.android.support.design.utils.ThemeUtils

@SuppressLint("RestrictedApi")
class FlexibleBottomNavigationView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    private val mMenu: MenuBuilder
    private val mMenuView: FlexibleBottomNavigationMenuView
    private val mPresenter = FlexibleBottomNavigationPresenter()
    private var mMenuInflater: MenuInflater? = null

    private var mSelectedListener: OnNavigationItemSelectedListener? = null
    private var mReselectedListener: OnNavigationItemReselectedListener? = null

    /**
     * Returns the [Menu] instance associated with this bottom navigation bar.
     */
    val menu: Menu
        get() = mMenu

    /**
     * @return The maximum number of items that can be shown in BottomNavigationView.
     */
    val maxItemCount: Int
        get() = BottomNavigationMenu.MAX_ITEM_COUNT

    /**
     * Returns the tint which is applied to our menu items' icons.
     *
     * @attr ref R.styleable#BottomNavigationView_itemIconTint
     * @see .setItemIconTintList
     */
    /**
     * Set the tint which is applied to our menu items' icons.
     *
     * @param tint the tint to apply.
     * @attr ref R.styleable#BottomNavigationView_itemIconTint
     */
    var itemIconTintList: ColorStateList?
        get() = mMenuView.iconTintList
        set(tint) {
            mMenuView.iconTintList = tint
        }

    /**
     * Returns colors used for the different states (normal, selected, focused, etc.) of the menu
     * item text.
     *
     * @return the ColorStateList of colors used for the different states of the menu items text.
     * @attr ref R.styleable#BottomNavigationView_itemTextColor
     * @see .setItemTextColor
     */
    /**
     * Set the colors to use for the different states (normal, selected, focused, etc.) of the menu
     * item text.
     *
     * @attr ref R.styleable#BottomNavigationView_itemTextColor
     * @see .getItemTextColor
     */
    var itemTextColor: ColorStateList?
        get() = mMenuView.itemTextColor
        set(textColor) {
            mMenuView.itemTextColor = textColor
        }

    /**
     * Returns the background resource of the menu items.
     *
     * @attr ref R.styleable#BottomNavigationView_itemBackground
     * @see .setItemBackgroundResource
     */
    /**
     * Set the background of our menu items to the given resource.
     *
     * @param resId The identifier of the resource.
     * @attr ref R.styleable#BottomNavigationView_itemBackground
     */
    var itemBackgroundResource: Int
        @DrawableRes
        get() = mMenuView.itemBackgroundRes
        set(@DrawableRes resId) {
            mMenuView.itemBackgroundRes = resId
        }

    /**
     * Returns the currently selected menu item ID, or zero if there is no menu.
     *
     * @see .setSelectedItemId
     */
    /**
     * Set the selected menu item ID. This behaves the same as tapping on an item.
     *
     * @param itemId The menu item ID. If no item has this ID, the current selection is unchanged.
     * @see .getSelectedItemId
     */
    var selectedItemId: Int
        @IdRes
        get() = mMenuView.selectedItemId
        set(@IdRes itemId) {
            val item = mMenu.findItem(itemId)
            if (item != null) {
                if (!mMenu.performItemAction(item, mPresenter, 0)) {
                    item.isChecked = true
                }
            }
        }

    private val menuInflater: MenuInflater?
        get() {
            if (mMenuInflater == null) {
                mMenuInflater = SupportMenuInflater(context)
            }
            return mMenuInflater
        }

    init {

        ThemeUtils.checkAppCompatTheme(context)

        // Create the menu
        mMenu = BottomNavigationMenu(context)

        mMenuView = FlexibleBottomNavigationMenuView(context)
        val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER
        mMenuView.layoutParams = params

        mPresenter.setBottomNavigationMenuView(mMenuView)
        mPresenter.id = MENU_PRESENTER_ID
        mMenuView.setPresenter(mPresenter)
        mMenu.addMenuPresenter(mPresenter)
        mPresenter.initForMenu(getContext(), mMenu)

        // Custom attributes
        val a = TintTypedArray.obtainStyledAttributes(context, attrs,
                R.styleable.FlexibleBottomNavigationView, defStyleAttr,
                R.style.Widget_FlexibleBottomNavigationStyle)

        if (a.hasValue(R.styleable.FlexibleBottomNavigationView_itemIconTint)) {
            mMenuView.iconTintList = a.getColorStateList(R.styleable.FlexibleBottomNavigationView_itemIconTint)
        } else {
            mMenuView.iconTintList = createDefaultColorStateList(android.R.attr.textColorSecondary)
        }
        if (a.hasValue(R.styleable.FlexibleBottomNavigationView_itemTextColor)) {
            mMenuView.itemTextColor = a.getColorStateList(R.styleable.FlexibleBottomNavigationView_itemTextColor)
        } else {
            mMenuView.itemTextColor = createDefaultColorStateList(android.R.attr.textColorSecondary)
        }
        if (a.hasValue(R.styleable.FlexibleBottomNavigationView_badgeBackgroundColor)) {
            mMenuView.setItemBadgeBackgroundRes(
                    a.getResourceId(R.styleable.FlexibleBottomNavigationView_badgeBackgroundColor, 0)
            )
        } else {
            mMenuView.setItemBadgeBackgroundRes(createPrimaryColor(android.R.attr.textColorPrimary))
        }
        if (a.hasValue(R.styleable.FlexibleBottomNavigationView_badgeTextColor)) {
            mMenuView.setItemBadgeTextColor(a.getColorStateList(R.styleable.FlexibleBottomNavigationView_badgeTextColor))
        } else {
            mMenuView.setItemBadgeTextColor(createDefaultColorStateList(android.R.attr.textColorSecondary)!!)
        }
        if (a.hasValue(R.styleable.FlexibleBottomNavigationView_elevation)) {
            ViewCompat.setElevation(this, a.getDimensionPixelSize(
                    R.styleable.FlexibleBottomNavigationView_elevation, 0).toFloat())
        }

        val itemBackground = a.getResourceId(R.styleable.FlexibleBottomNavigationView_itemBackground, 0)
        mMenuView.itemBackgroundRes = itemBackground

        if (a.hasValue(R.styleable.FlexibleBottomNavigationView_menu)) {
            inflateMenu(a.getResourceId(R.styleable.FlexibleBottomNavigationView_menu, 0))
        }
        a.recycle()

        addView(mMenuView, params)
        if (Build.VERSION.SDK_INT < 21) {
            addCompatibilityTopDivider(context)
        }

        mMenu.setCallback(object : MenuBuilder.Callback {
            override fun onMenuItemSelected(menu: MenuBuilder, item: MenuItem): Boolean {
                if (mReselectedListener != null && item.itemId == selectedItemId) {
                    mReselectedListener!!.onNavigationItemReselected(item)
                    return true // item is already selected
                }
                return mSelectedListener != null && !mSelectedListener!!.onNavigationItemSelected(item)
            }

            override fun onMenuModeChange(menu: MenuBuilder) {}
        })
    }

    /**
     * Set a listener that will be notified when a bottom navigation item is selected. This listener
     * will also be notified when the currently selected item is reselected, unless an
     * [OnNavigationItemReselectedListener] has also been set.
     *
     * @param listener The listener to notify
     * @see .setOnNavigationItemReselectedListener
     */
    fun setOnNavigationItemSelectedListener(
            listener: OnNavigationItemSelectedListener?) {
        mSelectedListener = listener
    }

    /**
     * Set a listener that will be notified when the currently selected bottom navigation item is
     * reselected. This does not require an [OnNavigationItemSelectedListener] to be set.
     *
     * @param listener The listener to notify
     * @see .setOnNavigationItemSelectedListener
     */
    fun setOnNavigationItemReselectedListener(
            listener: OnNavigationItemReselectedListener?) {
        mReselectedListener = listener
    }

    /**
     * Inflate a menu resource into this navigation view.
     *
     *
     *
     * Existing items in the menu will not be modified or removed.
     *
     * @param resId ID of a menu resource to inflate
     */
    fun inflateMenu(resId: Int) {
        mPresenter.setUpdateSuspended(true)
        menuInflater?.inflate(resId, mMenu)
        mPresenter.setUpdateSuspended(false)
        mPresenter.updateMenuView(true)
    }

    fun setItemBadgeBackgroundResource(@ColorRes resId: Int) {
        mMenuView.setItemBadgeBackgroundRes(resId)
    }

    fun setItemBadgeTextColor(textColor: ColorStateList) {
        mMenuView.setItemBadgeTextColor(textColor)
    }

    fun setItemBadgeCount(menuItemId: Int, count: Int) {
        mPresenter.setItemBadgeCount(menuItemId, count)
        mMenuView.updateMenuView()
    }

    /**
     * Listener for handling selection events on bottom navigation items.
     */
    interface OnNavigationItemSelectedListener {

        /**
         * Called when an item in the bottom navigation menu is selected.
         *
         * @param item The selected item
         * @return true to display the item as the selected item and false if the item should not
         * be selected. Consider setting non-selectable items as disabled preemptively to
         * make them appear non-interactive.
         */
        fun onNavigationItemSelected(item: MenuItem): Boolean
    }

    /**
     * Listener for handling reselection events on bottom navigation items.
     */
    interface OnNavigationItemReselectedListener {

        /**
         * Called when the currently selected item in the bottom navigation menu is selected again.
         *
         * @param item The selected item
         */
        fun onNavigationItemReselected(item: MenuItem)
    }

    private fun addCompatibilityTopDivider(context: Context) {
        val divider = View(context)
        divider.setBackgroundColor(
                ContextCompat.getColor(context, R.color.design_bottom_navigation_shadow_color))
        val dividerParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(
                        R.dimen.design_bottom_navigation_shadow_height))
        divider.layoutParams = dividerParams
        addView(divider)
    }

    private fun createPrimaryColor(baseColorThemeAttr: Int): Int {
        val value = TypedValue()
        if (!context.theme.resolveAttribute(baseColorThemeAttr, value, true)) {
            return 0
        }
        return if (!context.theme.resolveAttribute(
                android.support.v7.appcompat.R.attr.colorPrimary, value, true)) {
            0
        } else value.resourceId
    }

    private fun createDefaultColorStateList(baseColorThemeAttr: Int): ColorStateList? {
        val value = TypedValue()
        if (!context.theme.resolveAttribute(baseColorThemeAttr, value, true)) {
            return null
        }
        val baseColor = AppCompatResources.getColorStateList(
                context, value.resourceId)
        if (!context.theme.resolveAttribute(
                android.support.v7.appcompat.R.attr.colorPrimary, value, true)) {
            return null
        }
        val colorPrimary = value.data
        val defaultColor = baseColor.defaultColor
        return ColorStateList(arrayOf(DISABLED_STATE_SET, CHECKED_STATE_SET, View.EMPTY_STATE_SET), intArrayOf(baseColor.getColorForState(DISABLED_STATE_SET, defaultColor), colorPrimary, defaultColor))
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState)
        savedState.menuPresenterState = Bundle()
        mMenu.savePresenterStates(savedState.menuPresenterState)
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        mMenu.restorePresenterStates(state.menuPresenterState)
    }

    internal class SavedState : AbsSavedState {
        var menuPresenterState: Bundle? = null

        constructor(superState: Parcelable) : super(superState) {}

        constructor(source: Parcel, loader: ClassLoader?) : super(source, loader) {
            readFromParcel(source, loader)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeBundle(menuPresenterState)
        }

        private fun readFromParcel(`in`: Parcel, loader: ClassLoader?) {
            menuPresenterState = `in`.readBundle(loader)
        }

        companion object {

            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.ClassLoaderCreator<SavedState> {
                override fun createFromParcel(`in`: Parcel, loader: ClassLoader): SavedState {
                    return SavedState(`in`, loader)
                }

                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`, null)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {

        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
        private val DISABLED_STATE_SET = intArrayOf(-android.R.attr.state_enabled)

        private val MENU_PRESENTER_ID = 1
    }
}
