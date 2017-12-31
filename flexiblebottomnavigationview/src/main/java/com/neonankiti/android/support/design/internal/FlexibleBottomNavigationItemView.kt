package com.neonankiti.android.support.design.internal

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.view.PointerIconCompat
import android.support.v4.view.ViewCompat
import android.support.v7.view.menu.MenuItemImpl
import android.support.v7.view.menu.MenuView
import android.support.v7.widget.TooltipCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import com.neonankiti.flexiblebottomnavigationview.R

@SuppressLint("RestrictedApi")
class FlexibleBottomNavigationItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                                                 defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr), MenuView.ItemView {

    private val mDefaultMargin: Int
    private val mShiftAmount: Int
    private val mScaleUpFactor: Float
    private val mScaleDownFactor: Float

    private var mShiftingMode: Boolean = false

    private val mIcon: ImageView
    private val mSmallLabel: TextView
    private val mLargeLabel: TextView
    var itemPosition = INVALID_ITEM_POSITION
    private val mCount: TextView
    private val mBadge: FrameLayout

    private var mItemData: MenuItemImpl? = null

    private var mIconTint: ColorStateList? = null

    init {
        val res = resources
        val inactiveLabelSize = res.getDimensionPixelSize(R.dimen.design_bottom_navigation_text_size)
        val activeLabelSize = res.getDimensionPixelSize(
                R.dimen.design_bottom_navigation_active_text_size)
        mDefaultMargin = res.getDimensionPixelSize(R.dimen.design_bottom_navigation_margin)
        mShiftAmount = inactiveLabelSize - activeLabelSize
        mScaleUpFactor = 1f * activeLabelSize / inactiveLabelSize
        mScaleDownFactor = 1f * inactiveLabelSize / activeLabelSize

        LayoutInflater.from(context).inflate(R.layout.flexible_bottom_navigation_item, this, true)
        setBackgroundResource(R.drawable.design_bottom_navigation_item_background)
        mIcon = findViewById(R.id.icon)
        mSmallLabel = findViewById(R.id.smallLabel)
        mLargeLabel = findViewById(R.id.largeLabel)
        mCount = findViewById(R.id.count)
        mBadge = findViewById(R.id.badge)
    }

    override fun initialize(itemData: MenuItemImpl, menuType: Int) {
        mItemData = itemData
        setCheckable(itemData.isCheckable)
        setChecked(itemData.isChecked)
        isEnabled = itemData.isEnabled
        setIcon(itemData.icon)
        setTitle(itemData.title)
        id = itemData.itemId
        contentDescription = itemData.contentDescription
        TooltipCompat.setTooltipText(this, itemData.tooltipText)
    }

    fun setShiftingMode(enabled: Boolean) {
        mShiftingMode = enabled
    }

    override fun getItemData(): MenuItemImpl? {
        return mItemData
    }

    override fun setTitle(title: CharSequence) {
        mSmallLabel.text = title
        mLargeLabel.text = title
    }

    fun setBadgeCount(count: String?) {
        count?.let {
            mCount.text = it
        }
    }

    /**
     * @param textSize sp size.
     */
    fun setBadgeTextSize(textSize: Int?) {
        textSize?.let {
            mCount.setTextSize(TypedValue.COMPLEX_UNIT_DIP, it.toFloat())
        }
    }

    fun setBadgeVisibility(visibility: Int?) {
        visibility?.let {
            mBadge.visibility = it
        }
    }

    fun setBadgeTextColor(color: ColorStateList?) {
        color?.let {
            mCount.setTextColor(it)
        }
    }

    fun setBadgeBackground(background: Int) {
        ViewCompat.setBackground(mBadge, createBadgeDrawable(background))
    }

    private fun createBadgeDrawable(background: Int): GradientDrawable {
        val drawable = GradientDrawable()
        val resources = resources
        val width = resources.getDimensionPixelSize(R.dimen.size_badge)
        val height = resources.getDimensionPixelSize(R.dimen.size_badge)
        drawable.setSize(width, height)
        drawable.shape = GradientDrawable.OVAL
        drawable.setColor(ContextCompat.getColor(context, background))
        return drawable
    }

    override fun setCheckable(checkable: Boolean) {
        refreshDrawableState()
    }

    override fun setChecked(checked: Boolean) {
        mLargeLabel.pivotX = (mLargeLabel.width / 2).toFloat()
        mLargeLabel.pivotY = mLargeLabel.baseline.toFloat()
        mSmallLabel.pivotX = (mSmallLabel.width / 2).toFloat()
        mSmallLabel.pivotY = mSmallLabel.baseline.toFloat()
        if (mShiftingMode) {
            if (checked) {
                val iconParams = mIcon.layoutParams as FrameLayout.LayoutParams
                iconParams.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
                iconParams.topMargin = mDefaultMargin
                mIcon.layoutParams = iconParams
                mLargeLabel.visibility = View.VISIBLE
                mLargeLabel.scaleX = 1f
                mLargeLabel.scaleY = 1f
            } else {
                val iconParams = mIcon.layoutParams as FrameLayout.LayoutParams
                iconParams.gravity = Gravity.CENTER
                iconParams.topMargin = mDefaultMargin
                mIcon.layoutParams = iconParams
                mLargeLabel.visibility = View.INVISIBLE
                mLargeLabel.scaleX = 0.5f
                mLargeLabel.scaleY = 0.5f
            }
            mSmallLabel.visibility = View.INVISIBLE
        } else {
            if (checked) {
                val iconParams = mIcon.layoutParams as FrameLayout.LayoutParams
                iconParams.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
                iconParams.topMargin = mDefaultMargin + mShiftAmount
                mIcon.layoutParams = iconParams
                mLargeLabel.visibility = View.VISIBLE
                mSmallLabel.visibility = View.INVISIBLE

                mLargeLabel.scaleX = 1f
                mLargeLabel.scaleY = 1f
                mSmallLabel.scaleX = mScaleUpFactor
                mSmallLabel.scaleY = mScaleUpFactor
            } else {
                val iconParams = mIcon.layoutParams as FrameLayout.LayoutParams
                iconParams.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
                iconParams.topMargin = mDefaultMargin
                mIcon.layoutParams = iconParams
                mLargeLabel.visibility = View.INVISIBLE
                mSmallLabel.visibility = View.VISIBLE

                mLargeLabel.scaleX = mScaleDownFactor
                mLargeLabel.scaleY = mScaleDownFactor
                mSmallLabel.scaleX = 1f
                mSmallLabel.scaleY = 1f
            }
        }

        refreshDrawableState()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        mSmallLabel.isEnabled = enabled
        mLargeLabel.isEnabled = enabled
        mIcon.isEnabled = enabled

        if (enabled) {
            ViewCompat.setPointerIcon(this,
                    PointerIconCompat.getSystemIcon(context, PointerIconCompat.TYPE_HAND))
        } else {
            ViewCompat.setPointerIcon(this, null)
        }

    }

    public override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        mItemData?.let {
            if (it.isCheckable && it.isChecked) {
                View.mergeDrawableStates(drawableState, CHECKED_STATE_SET)
            }
        }
        return drawableState
    }

    override fun setShortcut(showShortcut: Boolean, shortcutKey: Char) {}

    override fun setIcon(icon: Drawable?) {
        var icon = icon
        if (icon != null) {
            val state = icon.constantState
            icon = DrawableCompat.wrap(if (state == null) icon else state.newDrawable()).mutate()
            DrawableCompat.setTintList(icon!!, mIconTint)
        }
        mIcon.setImageDrawable(icon)
    }

    override fun prefersCondensedTitle(): Boolean {
        return false
    }

    override fun showsIcon(): Boolean {
        return true
    }

    fun setIconTintList(tint: ColorStateList?) {
        mIconTint = tint
        mItemData?.let {
            // Update the icon so that the tint takes effect
            setIcon(it.icon)
        }
    }

    fun setTextColor(color: ColorStateList?) {
        mSmallLabel.setTextColor(color)
        mLargeLabel.setTextColor(color)
    }

    fun setItemBackground(background: Int) {
        val backgroundDrawable = if (background == 0)
            null
        else
            ContextCompat.getDrawable(context, background)
        ViewCompat.setBackground(this, backgroundDrawable)
    }

    companion object {
        val INVALID_ITEM_POSITION = -1

        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    }
}
