package com.THLight.BLE.USBeacon.Writer.Simple.ui.pager;
/**
 * ========================================================================
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * ========================================================================
 */
public class CustomViewPager extends ViewPager {
    //private int mCurrItem= 0;

    /**
     * ==========================================================
     */
    public CustomViewPager(Context context) {
        super(context);
    }

    /**
     * ==========================================================
     */
    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * ==========================================================
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return (isEnabled() ? super.onTouchEvent(event) : false);
    }

    /**
     * ==========================================================
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return (isEnabled() ? super.onInterceptTouchEvent(event) : false);
    }

    /**
     * ==========================================================
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }
}

/**
 * ========================================================================
 */

