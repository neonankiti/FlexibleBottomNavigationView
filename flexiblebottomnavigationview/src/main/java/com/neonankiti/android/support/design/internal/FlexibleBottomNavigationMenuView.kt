package com.neonankiti.android.support.design.internal

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.support.design.internal.BottomNavigationMenu
import android.support.design.internal.TextScale
import android.support.transition.AutoTransition
import android.support.transition.TransitionManager
import android.support.transition.TransitionSet
import android.support.v4.util.Pools
import android.support.v4.view.ViewCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.view.menu.MenuItemImpl
import android.support.v7.view.menu.MenuView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

import com.neonankiti.flexiblebottomnavigationview.R
import com.neonankiti.model.Badge
import com.neonankiti.model.NumberBadge

@SuppressLint("RestrictedApi")
class FlexibleBottomNavigationMenuView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : ViewGroup(context, attrs), MenuView {

    private val mSet: TransitionSet
    private val mInactiveItemMaxWidth: Int
    private val mInactiveItemMinWidth: Int
    private val mActiveItemMaxWidth: Int
    private val mItemHeight: Int
    private val mOnClickListener: View.OnClickListener
    private val mItemPool = Pools.SynchronizedPool<FlexibleBottomNavigationItemView>(5)

    private var mShiftingMode = true

    private var mButtons: Array<FlexibleBottomNavigationItemView?>? = null
    private var mBadges: Array<Badge?>? = null
    var selectedItemId = 0
        private set
    private var mSelectedItemPosition = 0
    /**
     * Returns the tint which is applied to menu items' icons.
     *
     * @return the ColorStateList that is used to tint menu items' icons
     */
    /**
     * Sets the tint which is applied to the menu items' icons.
     *
     * @param tint the tint to apply
     */
    var iconTintList: ColorStateList? = null
        set(tint) {
            field = tint
            mButtons?.let { buttons ->
                for (item in buttons) {
                    item?.setIconTintList(tint)
                }
            }
        }
    /**
     * Returns the text color used on menu items.
     *
     * @return the ColorStateList used for menu items' text
     */
    /**
     * Sets the text color to be used on menu items.
     *
     * @param color the ColorStateList used for menu items' text.
     */
    var itemTextColor: ColorStateList? = null
        set(color) {
            field = color
            mButtons?.let { buttons ->
                for (item in buttons) {
                    item?.setTextColor(color)
                }
            }
        }
    /**
     * Returns the resource ID for the background of the menu items.
     *
     * @return the resource ID for the background
     */
    /**
     * Sets the resource ID to be used for item background.
     *
     * @param background the resource ID of the background
     */
    var itemBackgroundRes: Int = 0
        set(background) {
            field = background
            mButtons?.let { buttons ->
                for (item in buttons) {
                    item?.setItemBackground(background)
                }
            }
        }
    private val mTempChildWidths: IntArray
    private lateinit var mItemBadgeTextColor: ColorStateList
    private var mItemBadgeBackgroundRes: Int = 0

    private lateinit var mPresenter: FlexibleBottomNavigationPresenter
    private var mMenu: MenuBuilder? = null

    private val newItem: FlexibleBottomNavigationItemView
        get() {
            var item: FlexibleBottomNavigationItemView? = mItemPool.acquire()
            if (item == null) {
                item = FlexibleBottomNavigationItemView(context)
            }
            return item
        }

    init {
        val res = resources
        mInactiveItemMaxWidth = res.getDimensionPixelSize(
                R.dimen.design_bottom_navigation_item_max_width)
        mInactiveItemMinWidth = res.getDimensionPixelSize(
                R.dimen.design_bottom_navigation_item_min_width)
        mActiveItemMaxWidth = res.getDimensionPixelSize(
                R.dimen.design_bottom_navigation_active_item_max_width)
        mItemHeight = res.getDimensionPixelSize(R.dimen.design_bottom_navigation_height)

        mSet = AutoTransition()
        mSet.setOrdering(TransitionSet.ORDERING_TOGETHER)
        mSet.setDuration(ACTIVE_ANIMATION_DURATION_MS)
        mSet.setInterpolator(FastOutSlowInInterpolator())
        mSet.addTransition(TextScale())

        mOnClickListener = OnClickListener { v ->
            val itemView = v as FlexibleBottomNavigationItemView
            val item = itemView.itemData
            mMenu?.let {
                if (!it.performItemAction(item, mPresenter, 0)) {
                    item?.isChecked = true
                }
            }
        }
        mTempChildWidths = IntArray(BottomNavigationMenu.MAX_ITEM_COUNT)
    }

    override fun initialize(menu: MenuBuilder?) {
        mMenu = menu
    }

    fun setItemBadgeCount(position: Int, count: Int) {
        mBadges?.let { badges ->
            if (badges.size > position && badges[position] != null) {
                badges[position]?.let { badge ->
                    badge.setCount(count)
                    mButtons?.let { buttons ->
                        if (buttons.size > position) {
                            buttons[position]?.setBadgeCount(badge.getCount())
                        }
                    }
                }

            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val count = childCount

        val heightSpec = View.MeasureSpec.makeMeasureSpec(mItemHeight, View.MeasureSpec.EXACTLY)

        if (mShiftingMode) {
            val inactiveCount = count - 1
            val activeMaxAvailable = width - inactiveCount * mInactiveItemMinWidth
            val activeWidth = Math.min(activeMaxAvailable, mActiveItemMaxWidth)
            val inactiveMaxAvailable = (width - activeWidth) / inactiveCount
            val inactiveWidth = Math.min(inactiveMaxAvailable, mInactiveItemMaxWidth)
            var extra = width - activeWidth - inactiveWidth * inactiveCount
            for (i in 0 until count) {
                mTempChildWidths[i] = if (i == mSelectedItemPosition) activeWidth else inactiveWidth
                if (extra > 0) {
                    mTempChildWidths[i]++
                    extra--
                }
            }
        } else {
            val maxAvailable = width / if (count == 0) 1 else count
            val childWidth = Math.min(maxAvailable, mActiveItemMaxWidth)
            var extra = width - childWidth * count
            for (i in 0 until count) {
                mTempChildWidths[i] = childWidth
                if (extra > 0) {
                    mTempChildWidths[i]++
                    extra--
                }
            }
        }

        var totalWidth = 0
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) {
                continue
            }
            child.measure(View.MeasureSpec.makeMeasureSpec(mTempChildWidths[i], View.MeasureSpec.EXACTLY),
                    heightSpec)
            val params = child.layoutParams
            params.width = child.measuredWidth
            totalWidth += child.measuredWidth
        }
        setMeasuredDimension(
                View.resolveSizeAndState(totalWidth,
                        View.MeasureSpec.makeMeasureSpec(totalWidth, View.MeasureSpec.EXACTLY), 0),
                View.resolveSizeAndState(mItemHeight, heightSpec, 0))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val count = childCount
        val width = right - left
        val height = bottom - top
        var used = 0
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) {
                continue
            }
            if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                child.layout(width - used - child.measuredWidth, 0, width - used, height)
            } else {
                child.layout(used, 0, child.measuredWidth + used, height)
            }
            used += child.measuredWidth
        }
    }

    override fun getWindowAnimations(): Int {
        return 0
    }

    fun setItemBadgeBackgroundRes(background: Int) {
        mItemBadgeBackgroundRes = background
        mButtons?.let {
            for (item in it) {
                item?.setBadgeBackground(background)
            }
        }
    }

    fun setItemBadgeTextColor(color: ColorStateList) {
        mItemBadgeTextColor = color
        mButtons?.let {
            for (item in it) {
                item?.setBadgeTextColor(color)
            }
        }
    }

    fun setPresenter(presenter: FlexibleBottomNavigationPresenter) {
        mPresenter = presenter
    }

    fun buildMenuView() {
        removeAllViews()
        mButtons?.let {
            for (item in it) {
                mItemPool.release(item)
            }
        }

        mMenu?.let { menu ->
            if (menu.size() == 0) {
                selectedItemId = 0
                mSelectedItemPosition = 0
                mButtons = null
                return
            }

            mButtons = arrayOfNulls<FlexibleBottomNavigationItemView?>(menu.size())
            mBadges = arrayOfNulls<Badge?>(menu.size())
            mShiftingMode = menu.size() > 3

            for (i in 0 until menu.size()) {
                mPresenter.setUpdateSuspended(true)
                menu.getItem(i).isCheckable = true
                mPresenter.setUpdateSuspended(false)
                mButtons?.let { buttons ->
                    val child = newItem
                    buttons[i] = child
                    child.setIconTintList(iconTintList)
                    child.setTextColor(itemTextColor)
                    child.setItemBackground(itemBackgroundRes)
                    child.setShiftingMode(mShiftingMode)
                    child.itemPosition = i
                    child.setOnClickListener(mOnClickListener)

                    mBadges?.let { badges ->
                        badges[i] = NumberBadge(0, mItemBadgeTextColor, mItemBadgeBackgroundRes)
                        child.setBadgeTextColor(mItemBadgeTextColor)
                        child.setBadgeBackground(mItemBadgeBackgroundRes)
                        child.setBadgeCount(badges[i]?.getCount())
                        child.setBadgeTextSize(badges[i]?.textSize)
                        child.setBadgeVisibility(badges[i]?.visibility)
                    }

                    child.initialize(menu.getItem(i) as MenuItemImpl, 0)
                    addView(child)
                }
            }

            mSelectedItemPosition = Math.min(menu.size() - 1, mSelectedItemPosition)
            menu.getItem(mSelectedItemPosition).isChecked = true
        }
    }

    fun updateMenuView() {
        mMenu?.let { menu ->
            val menuSize = menu.size()

            if (menuSize != mButtons?.size) {
                // The size has changed. Rebuild menu view from scratch.
                buildMenuView()
                return
            }
            val previousSelectedId = selectedItemId

            for (i in 0 until menuSize) {
                val item = menu.getItem(i)
                if (item.isChecked) {
                    selectedItemId = item.itemId
                    mSelectedItemPosition = i
                }
            }
            if (previousSelectedId != selectedItemId) {
                // Note: this has to be called before BottomNavigationItemView#initialize().
                TransitionManager.beginDelayedTransition(this, mSet)
            }

            for (i in 0 until menuSize) {
                mPresenter.setUpdateSuspended(true)
                // This is where updating the each item view references.
                mButtons?.let { buttons ->
                    buttons[i]?.initialize(menu.getItem(i) as MenuItemImpl, 0)
                    mBadges?.let { badges ->
                        if (badges[i] != null) {
                            buttons[i]?.setBadgeTextColor(mItemBadgeTextColor)
                            buttons[i]?.setBadgeBackground(mItemBadgeBackgroundRes)
                            buttons[i]?.setBadgeCount(badges[i]?.getCount())
                            buttons[i]?.setBadgeVisibility(badges[i]?.visibility)
                            buttons[i]?.setBadgeTextSize(badges[i]?.textSize)
                        }
                    }
                }

                mPresenter.setUpdateSuspended(false)
            }
        }
    }

    internal fun tryRestoreSelectedItemId(itemId: Int) {
        mMenu?.let { menu ->
            val size = menu.size()
            for (i in 0 until size) {
                val item = menu.getItem(i)
                if (itemId == item.itemId) {
                    selectedItemId = itemId
                    mSelectedItemPosition = i
                    item.isChecked = true
                    break
                }
            }
        }
    }

    companion object {
        private val ACTIVE_ANIMATION_DURATION_MS = 115L
    }
}
