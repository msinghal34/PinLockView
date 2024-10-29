package com.msinghal34.pinlockview;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * It represents a set of indicator dots which when attached with {@link PinLockView}
 * can be used to indicate the current length of the input
 */
public class IndicatorDots extends LinearLayout {

    private static final int DEFAULT_PIN_LENGTH = 4;
    private static final int DEFAULT_FILL_DRAWABLE = R.drawable.dot_filled;
    private static final int DEFAULT_EMPTY_DRAWABLE = R.drawable.dot_empty;
    private final int mDotDiameter;
    private final int mDotMargin;
    private final int mDotColor;
    private final int mFillDrawable;
    private final int mEmptyDrawable;
    private int mPinLength;
    private int mIndicatorType;
    private int mPreviousLength;

    public IndicatorDots(Context context) {
        this(context, null);
    }

    public IndicatorDots(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorDots(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IndicatorDots);
        try {
            mDotDiameter = (int) typedArray.getDimension(R.styleable.IndicatorDots_id_dotDiameter, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_dot_diameter));
            mDotMargin = (int) typedArray.getDimension(R.styleable.IndicatorDots_id_dotMargin, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_dot_spacing));
            mDotColor = typedArray.getColor(R.styleable.IndicatorDots_id_dotColor, ResourceUtils.getColor(getContext(), R.color.white));
            mFillDrawable = typedArray.getResourceId(R.styleable.IndicatorDots_id_filledDotDrawable, DEFAULT_FILL_DRAWABLE);
            mEmptyDrawable = typedArray.getResourceId(R.styleable.IndicatorDots_id_emptyDotDrawable, DEFAULT_EMPTY_DRAWABLE);
            mPinLength = typedArray.getInt(R.styleable.PinLockView_plv_pinLength, DEFAULT_PIN_LENGTH);
            mIndicatorType = typedArray.getInt(R.styleable.IndicatorDots_id_indicatorType, IndicatorType.FIXED);
        } finally {
            typedArray.recycle();
        }

        initView(context);
    }

    private void initView(Context context) {
        setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        setGravity(Gravity.CENTER);
        if (mIndicatorType == 0) {
            for (int i = 0; i < mPinLength; i++) {
                View dot = new View(context);
                emptyDot(dot);

                addView(dot);
            }
        } else if (mIndicatorType == 2) {
            setLayoutTransition(new LayoutTransition());
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // If the indicator type is not fixed
        if (mIndicatorType != 0) {
            ViewGroup.LayoutParams params = this.getLayoutParams();
            params.height = mDotDiameter;
            requestLayout();
        }
    }

    void error() {
        ObjectAnimator shake = ObjectAnimator.ofFloat(this, "translationX", 0f, 100f, -100f, 0f);
        shake.setDuration(200);
        shake.start();
    }

    void updateDot(int length) {
        if (mIndicatorType == 0) {
            if (length > 0) {
                if (length > mPreviousLength) {
                    fillDot(getChildAt(length - 1));
                } else {
                    emptyDot(getChildAt(length));
                }
                mPreviousLength = length;
            } else {
                // When {@code mPinLength} is 0, we need to reset all the views back to empty
                for (int i = 0; i < getChildCount(); i++) {
                    View v = getChildAt(i);
                    emptyDot(v);
                }
                mPreviousLength = 0;
            }
        } else {
            if (length > 0) {
                if (length > mPreviousLength) {
                    View dot = new View(getContext());
                    fillDot(dot);

                    addView(dot, length - 1);
                } else {
                    removeViewAt(length);
                }
                mPreviousLength = length;
            } else {
                removeAllViews();
                mPreviousLength = 0;
            }
        }
    }

    private void emptyDot(View dot) {
        dot.setBackgroundResource(mEmptyDrawable);
        if (mEmptyDrawable == DEFAULT_EMPTY_DRAWABLE) {
            dot.setBackgroundTintList(ColorStateList.valueOf(mDotColor));
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mDotDiameter, mDotDiameter);
        params.setMargins(mDotMargin, 0, mDotMargin, 0);
        dot.setLayoutParams(params);
    }

    private void fillDot(View dot) {
        dot.setBackgroundResource(mFillDrawable);
        if (mFillDrawable == DEFAULT_FILL_DRAWABLE) {
            dot.setBackgroundTintList(ColorStateList.valueOf(mDotColor));
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mDotDiameter, mDotDiameter);
        params.setMargins(mDotMargin, 0, mDotMargin, 0);
        dot.setLayoutParams(params);
    }

    public void setPinLength(int pinLength) {
        this.mPinLength = pinLength;
        removeAllViews();
        initView(getContext());
    }

    public void setIndicatorType(@IndicatorType int type) {
        this.mIndicatorType = type;
        removeAllViews();
        initView(getContext());
    }

    @IntDef({IndicatorType.FIXED, IndicatorType.FILL, IndicatorType.FILL_WITH_ANIMATION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface IndicatorType {
        int FIXED = 0;
        int FILL = 1;
        int FILL_WITH_ANIMATION = 2;
    }
}
