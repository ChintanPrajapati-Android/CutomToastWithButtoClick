package com.cp.customtoast

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.FrameLayout

class ActivityToast @SuppressLint("ClickableViewAccessibility") constructor(
    mActivity: Activity,
    toastView: View
) {
    private val mLayoutParams: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        Gravity.BOTTOM or Gravity.FILL_HORIZONTAL
    )
    private val mHandler = Handler(Looper.getMainLooper())
    private val mParent: ViewGroup = mActivity.window.decorView as ViewGroup
    private val mToastHolder: FrameLayout = FrameLayout(mActivity.baseContext)
    val view: View
    private var mShowAnimation: Animation?
    private var mCancelAnimation: Animation?
    private var mLength = LENGTH_SHORT
    private var mShowAnimationListener: Animation.AnimationListener? = null
    private var mCancelAnimationListener: Animation.AnimationListener? = null
    private var mIsAnimationRunning = false
    var isShowing = false
        private set

    fun show() {
        if (!isShowing) {
            mParent.addView(mToastHolder)
            isShowing = true
            if (mShowAnimation != null) {
                mToastHolder.startAnimation(mShowAnimation)
            } else {
                mHandler.postDelayed(mCancelTask, mLength)
            }
        }
    }

    fun cancel() {
        if (isShowing && !mIsAnimationRunning) {
            if (mCancelAnimation != null) {
                mToastHolder.startAnimation(mCancelAnimation)
            } else {
                mParent.removeView(mToastHolder)
                mHandler.removeCallbacks(mCancelTask)
                isShowing = false
            }
        }
    }

    /**
     * Pay attention that Action bars is the part of Activity window
     *
     * @param gravity Position of view in Activity window
     */
    fun setGravity(gravity: Int) {
        mLayoutParams.gravity = gravity
        if (isShowing) {
            mToastHolder.requestLayout()
        }
    }

    fun setShowAnimation(showAnimation: Animation?) {
        mShowAnimation = showAnimation
    }

    fun setCancelAnimation(cancelAnimation: Animation?) {
        mCancelAnimation = cancelAnimation
    }

    /**
     * @param cancelAnimationListener cancel toast animation. Note: you should use this instead of
     * Animation.setOnAnimationListener();
     */
    fun setCancelAnimationListener(cancelAnimationListener: Animation.AnimationListener?) {
        mCancelAnimationListener = cancelAnimationListener
    }

    /**
     * @param showAnimationListener show toast animation. Note: you should use this instead of
     * Animation.setOnAnimationListener();
     */
    fun setShowAnimationListener(showAnimationListener: Animation.AnimationListener?) {
        mShowAnimationListener = showAnimationListener
    }

    fun setLength(length: Long) {
        mLength = length
    }

    private val mCancelTask = Runnable { cancel() }
    private val mHiddenShowListener: Animation.AnimationListener =
        object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                if (mShowAnimationListener != null) {
                    mShowAnimationListener?.onAnimationStart(animation)
                }
                mIsAnimationRunning = true
            }

            override fun onAnimationEnd(animation: Animation) {
                mHandler.postDelayed(mCancelTask, mLength)
                if (mShowAnimationListener != null) {
                    mShowAnimationListener?.onAnimationEnd(animation)
                }
                mIsAnimationRunning = false
            }

            override fun onAnimationRepeat(animation: Animation) {
                if (mShowAnimationListener != null) {
                    mShowAnimationListener?.onAnimationRepeat(animation)
                }
            }
        }
    private val mHiddenCancelListener: Animation.AnimationListener =
        object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                if (mCancelAnimationListener != null) {
                    mCancelAnimationListener?.onAnimationStart(animation)
                }
                mIsAnimationRunning = true
            }

            override fun onAnimationEnd(animation: Animation) {
                mParent.removeView(mToastHolder)
                mHandler.removeCallbacks(mCancelTask)
                if (mCancelAnimationListener != null) {
                    mCancelAnimationListener?.onAnimationEnd(animation)
                }
                mIsAnimationRunning = false
                isShowing = false
            }

            override fun onAnimationRepeat(animation: Animation) {
                if (mCancelAnimationListener != null) {
                    mCancelAnimationListener?.onAnimationRepeat(animation)
                }
            }
        }

    companion object {
        const val LENGTH_SHORT: Long = 2000
        const val LENGTH_LONG: Long = 3000
        const val DEFAULT_ANIMATION_DURATION = 400
    }

    /**
     * @param activity Toast will be shown at top of the widow of this Activity
     */
    init {
        mToastHolder.layoutParams = mLayoutParams
        mShowAnimation = AlphaAnimation(0.0f, 1.0f)
        mShowAnimation?.duration = DEFAULT_ANIMATION_DURATION.toLong()
        mShowAnimation?.setAnimationListener(mHiddenShowListener)
        mCancelAnimation = AlphaAnimation(1.0f, 0.0f)
        mCancelAnimation?.duration = DEFAULT_ANIMATION_DURATION.toLong()
        mCancelAnimation?.setAnimationListener(mHiddenCancelListener)
        view = toastView
        mToastHolder.addView(view)
        mToastHolder.setOnTouchListener { view: View?, motionEvent: MotionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                cancel()
            }
            false
        }
    }
}