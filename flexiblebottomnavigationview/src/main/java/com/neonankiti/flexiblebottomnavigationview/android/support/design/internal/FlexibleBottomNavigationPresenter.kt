package com.neonankiti.flexiblebottomnavigationview.android.support.design.internal

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.view.menu.MenuItemImpl
import android.support.v7.view.menu.MenuPresenter
import android.support.v7.view.menu.MenuView
import android.support.v7.view.menu.SubMenuBuilder
import android.view.ViewGroup

class FlexibleBottomNavigationPresenter : MenuPresenter {
    private var mMenu: MenuBuilder? = null
    private lateinit var mMenuView: FlexibleBottomNavigationMenuView
    private var mUpdateSuspended = false
    private var mId: Int = 0

    fun setBottomNavigationMenuView(menuView: FlexibleBottomNavigationMenuView) {
        mMenuView = menuView
    }

    override fun initForMenu(context: Context, menu: MenuBuilder) {
        mMenuView.initialize(mMenu)
        mMenu = menu
    }

    @SuppressLint("RestrictedApi")
    fun setItemBadgeCount(menuItemId: Int, count: Int) {
        mMenu?.let {
            mMenuView.setItemBadgeCount(it.findItemIndex(menuItemId), count)
        }
    }

    override fun getMenuView(root: ViewGroup): MenuView? {
        return mMenuView
    }

    override fun updateMenuView(cleared: Boolean) {
        if (mUpdateSuspended) return
        if (cleared) {
            mMenuView.buildMenuView()
        } else {
            mMenuView.updateMenuView()
        }
    }

    override fun setCallback(cb: MenuPresenter.Callback) {}

    override fun onSubMenuSelected(subMenu: SubMenuBuilder): Boolean {
        return false
    }

    override fun onCloseMenu(menu: MenuBuilder, allMenusAreClosing: Boolean) {}

    override fun flagActionItems(): Boolean {
        return false
    }

    override fun expandItemActionView(menu: MenuBuilder, item: MenuItemImpl): Boolean {
        return false
    }

    override fun collapseItemActionView(menu: MenuBuilder, item: MenuItemImpl): Boolean {
        return false
    }

    fun setId(id: Int) {
        mId = id
    }

    override fun getId(): Int {
        return mId
    }

    override fun onSaveInstanceState(): Parcelable {
        val savedState = SavedState()
        savedState.selectedItemId = mMenuView.selectedItemId
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is SavedState) {
            mMenuView.tryRestoreSelectedItemId(state.selectedItemId)
        }
    }

    fun setUpdateSuspended(updateSuspended: Boolean) {
        mUpdateSuspended = updateSuspended
    }

    internal class SavedState : Parcelable {
        var selectedItemId: Int = 0

        constructor() {}

        constructor(`in`: Parcel) {
            selectedItemId = `in`.readInt()
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            out.writeInt(selectedItemId)
        }

        companion object {

            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}
