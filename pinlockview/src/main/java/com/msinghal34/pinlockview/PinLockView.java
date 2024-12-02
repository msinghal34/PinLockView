package com.msinghal34.pinlockview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.HapticFeedbackConstants;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Represents a numeric lock view which can used to taken numbers as input.
 * The length of the input can be customized using {@link PinLockView#setPinLength(int)}, the default value being 4
 * <p/>
 * It can also be used as dial pad for taking number inputs.
 * Optionally, {@link IndicatorDots} can be attached to this view to indicate the length of the input taken
 */
public class PinLockView extends RecyclerView {

    private static final int DEFAULT_PIN_LENGTH = 4;
    private static final int[] DEFAULT_KEY_SET = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};

    private String mPin = "";
    private int mPinLength;
    private int mHorizontalMargin, mVerticalMargin;
    @ColorInt
    private int mTextColor, mButtonBackgroundColor;
    private int mTextSize, mButtonSize, mDeleteButtonSize;
    private int mDeleteButtonDrawableId;
    private boolean mShowDeleteButton;
    private boolean mShowButtonPressAnimation;
    private boolean mVibrate;

    private IndicatorDots mIndicatorDots;
    private PinLockAdapter mAdapter;
    private PinLockListener mPinLockListener;
    private final PinLockAdapter.OnNumberClickListener mOnNumberClickListener
            = new PinLockAdapter.OnNumberClickListener() {
        @Override
        public void onNumberClicked(int keyValue) {
            maybeVibrate(HapticFeedbackConstants.KEYBOARD_TAP);
            if (mPin.length() < mPinLength) {
                mPin = mPin.concat(String.valueOf(keyValue));

                if (isIndicatorDotsAttached()) {
                    mIndicatorDots.updateDot(mPin.length());
                }

                if (mPinLockListener != null) {
                    if (mPin.length() == mPinLength) {
                        if (mPinLockListener.onComplete(mPin)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                maybeVibrate(HapticFeedbackConstants.CONTEXT_CLICK);
                            }
                            resetPinLockView();
                        } else {
                            maybeVibrate(HapticFeedbackConstants.LONG_PRESS);
                            errorPinLockView();
                        }
                    } else {
                        mPinLockListener.onPinChange(mPin.length(), mPin);
                    }
                }
            } else {
                if (!isShowDeleteButton()) {
                    resetPinLockView();
                    mPin = mPin.concat(String.valueOf(keyValue));

                    if (isIndicatorDotsAttached()) {
                        mIndicatorDots.updateDot(mPin.length());
                    }

                    if (mPinLockListener != null) {
                        mPinLockListener.onPinChange(mPin.length(), mPin);
                    }

                } else {
                    if (mPinLockListener != null) {
                        mPinLockListener.onComplete(mPin);
                    }
                }
            }
        }
    };
    private final PinLockAdapter.OnDeleteClickListener mOnDeleteClickListener
            = new PinLockAdapter.OnDeleteClickListener() {
        @Override
        public void onDeleteClicked() {
            maybeVibrate(HapticFeedbackConstants.VIRTUAL_KEY);
            if (!mPin.isEmpty()) {
                mPin = mPin.substring(0, mPin.length() - 1);

                if (isIndicatorDotsAttached()) {
                    mIndicatorDots.updateDot(mPin.length());
                }

                if (mPinLockListener != null) {
                    if (mPin.isEmpty()) {
                        mPinLockListener.onEmpty();
                        clearInternalPin();
                    } else {
                        mPinLockListener.onPinChange(mPin.length(), mPin);
                    }
                }
            } else {
                if (mPinLockListener != null) {
                    mPinLockListener.onEmpty();
                }
            }
        }
    };
    private CustomizationOptionsBundle mCustomizationOptionsBundle;
    private int[] mCustomKeySet;

    public PinLockView(Context context) {
        super(context);
        init(null, 0);
    }

    public PinLockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PinLockView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attributeSet, int defStyle) {

        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.PinLockView);

        try {
            mPinLength = typedArray.getInt(R.styleable.PinLockView_plv_pinLength, DEFAULT_PIN_LENGTH);
            mHorizontalMargin = (int) typedArray.getDimension(R.styleable.PinLockView_plv_horizontalSpacing, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_horizontal_spacing));
            mVerticalMargin = (int) typedArray.getDimension(R.styleable.PinLockView_plv_verticalSpacing, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_vertical_spacing));
            mTextColor = typedArray.getColor(R.styleable.PinLockView_plv_textColor, ResourceUtils.getColor(getContext(), R.color.white));
            mButtonBackgroundColor = typedArray.getColor(R.styleable.PinLockView_plv_buttonBackgroundColor, ResourceUtils.getColor(getContext(), R.color.white));
            mTextSize = (int) typedArray.getDimension(R.styleable.PinLockView_plv_textSize, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_text_size));
            mButtonSize = (int) typedArray.getDimension(R.styleable.PinLockView_plv_buttonSize, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_button_size));
            mDeleteButtonSize = (int) typedArray.getDimension(R.styleable.PinLockView_plv_deleteButtonSize, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_delete_button_size));
            mDeleteButtonDrawableId = typedArray.getResourceId(R.styleable.PinLockView_plv_deleteButtonDrawable, R.drawable.ic_delete);
            mShowDeleteButton = typedArray.getBoolean(R.styleable.PinLockView_plv_showDeleteButton, true);
            mShowButtonPressAnimation = typedArray.getBoolean(R.styleable.PinLockView_plv_showButtonPressAnimation, true);
            mVibrate = typedArray.getBoolean(R.styleable.PinLockView_plv_vibrate, true);
        } finally {
            typedArray.recycle();
        }

        mCustomizationOptionsBundle = new CustomizationOptionsBundle();
        mCustomizationOptionsBundle.setTextColor(mTextColor);
        mCustomizationOptionsBundle.setButtonBackgroundColor(mButtonBackgroundColor);
        mCustomizationOptionsBundle.setTextSize(mTextSize);
        mCustomizationOptionsBundle.setButtonSize(mButtonSize);
        mCustomizationOptionsBundle.setDeleteButtonDrawable(mDeleteButtonDrawableId);
        mCustomizationOptionsBundle.setDeleteButtonSize(mDeleteButtonSize);
        mCustomizationOptionsBundle.setShowDeleteButton(mShowDeleteButton);
        mCustomizationOptionsBundle.setShowButtonPressAnimation(mShowButtonPressAnimation);

        initView();
    }

    private void initView() {
        setLayoutManager(new LTRGridLayoutManager(getContext(), 3));

        mAdapter = new PinLockAdapter(getContext());
        mAdapter.setOnItemClickListener(mOnNumberClickListener);
        mAdapter.setOnDeleteClickListener(mOnDeleteClickListener);
        mAdapter.setCustomizationOptions(mCustomizationOptionsBundle);
        setAdapter(mAdapter);

        addItemDecoration(new ItemSpaceDecoration(mHorizontalMargin, mVerticalMargin, 3, false));
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    private void maybeVibrate(int keyCode) {
        if (mVibrate) {
            try {
                performHapticFeedback(keyCode, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
            } catch (Exception e) {
                Log.e("PinLockView", "Exception while trying to vibrate", e);
            }
        }
    }

    /**
     * Sets a {@link PinLockListener} to the to listen to pin update events
     *
     * @param pinLockListener the listener
     */
    public void setPinLockListener(PinLockListener pinLockListener) {
        this.mPinLockListener = pinLockListener;
    }

    /**
     * Sets the pin length dynamically
     *
     * @param pinLength the pin length
     */
    public void setPinLength(int pinLength) {
        this.mPinLength = pinLength;

        if (isIndicatorDotsAttached()) {
            mIndicatorDots.setPinLength(pinLength);
        }
    }

    /**
     * Enables vibration dynamically.
     */
    public void enableVibration() {
        this.mVibrate = true;
    }

    /**
     * Disables vibration dynamically.
     */
    public void disableVibration() {
        this.mVibrate = false;
    }

    /**
     * Enable visual animation on pressing keypad button.
     */
    public void enableButtonPressAnimation() {
        mCustomizationOptionsBundle.setShowButtonPressAnimation(true);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Disable visual animation on pressing keypad button.
     */
    public void disableButtonPressAnimation() {
        mCustomizationOptionsBundle.setShowButtonPressAnimation(false);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Get the text color in the buttons
     *
     * @return the text color
     */
    public @ColorInt int getTextColor() {
        return mTextColor;
    }

    /**
     * Set the text color of the buttons dynamically
     *
     * @param textColor the text color
     */
    public void setTextColor(@ColorInt int textColor) {
        this.mTextColor = textColor;
        mCustomizationOptionsBundle.setTextColor(textColor);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Get the background color in the button
     *
     * @return the background color of button
     */
    public @ColorInt int getButtonBackgroundColor() {
        return mButtonBackgroundColor;
    }

    /**
     * Set the background color of the buttons dynamically
     *
     * @param buttonBackgroundColor the background color
     */
    public void setButtonBackgroundColor(@ColorInt int buttonBackgroundColor) {
        this.mButtonBackgroundColor = buttonBackgroundColor;
        mCustomizationOptionsBundle.setButtonBackgroundColor(buttonBackgroundColor);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Get the size of the text in the buttons
     *
     * @return the size of the text in pixels
     */
    public int getTextSize() {
        return mTextSize;
    }

    /**
     * Set the size of text in pixels
     *
     * @param textSize the text size in pixels
     */
    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
        mCustomizationOptionsBundle.setTextSize(textSize);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Get the size of the pin buttons
     *
     * @return the size of the button in pixels
     */
    public int getButtonSize() {
        return mButtonSize;
    }

    /**
     * Set the size of the pin buttons dynamically
     *
     * @param buttonSize the button size
     */
    public void setButtonSize(int buttonSize) {
        this.mButtonSize = buttonSize;
        mCustomizationOptionsBundle.setButtonSize(buttonSize);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Get the drawable of the delete button
     *
     * @return the delete button drawable id.
     */
    public int getDeleteButtonDrawable() {
        return mDeleteButtonDrawableId;
    }

    /**
     * Set the drawable of the delete button dynamically
     *
     * @param deleteBackgroundDrawableId the delete button drawable
     */
    public void setDeleteButtonDrawable(int deleteBackgroundDrawableId) {
        this.mDeleteButtonDrawableId = deleteBackgroundDrawableId;
        mCustomizationOptionsBundle.setDeleteButtonDrawable(deleteBackgroundDrawableId);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Get the delete button size in pixels
     *
     * @return size in pixels
     */
    public int getDeleteButtonSize() {
        return mDeleteButtonSize;
    }

    /**
     * Set the size of the delete button in pixels
     *
     * @param deleteButtonSize size in pixels
     */
    public void setDeleteButtonSize(int deleteButtonSize) {
        this.mDeleteButtonSize = deleteButtonSize;
        mCustomizationOptionsBundle.setDeleteButtonSize(deleteButtonSize);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Is the delete button shown
     *
     * @return returns true if shown, false otherwise
     */
    public boolean isShowDeleteButton() {
        return mShowDeleteButton;
    }

    /**
     * Dynamically set if the delete button should be shown
     *
     * @param showDeleteButton true if the delete button should be shown, false otherwise
     */
    public void setShowDeleteButton(boolean showDeleteButton) {
        this.mShowDeleteButton = showDeleteButton;
        mCustomizationOptionsBundle.setShowDeleteButton(showDeleteButton);
        mAdapter.notifyDataSetChanged();
    }

    public int[] getCustomKeySet() {
        return mCustomKeySet;
    }

    public void setCustomKeySet(int[] customKeySet) {
        this.mCustomKeySet = customKeySet;

        if (mAdapter != null) {
            mAdapter.setKeyValues(customKeySet);
        }
    }

    public void enableLayoutShuffling() {
        this.mCustomKeySet = ShuffleArrayUtils.shuffle(DEFAULT_KEY_SET);

        if (mAdapter != null) {
            mAdapter.setKeyValues(mCustomKeySet);
        }
    }

    private void clearInternalPin() {
        mPin = "";
    }

    /**
     * Resets the {@link PinLockView}, clearing the entered pin
     * and resetting the {@link IndicatorDots} if attached
     */
    public void resetPinLockView() {
        clearInternalPin();

        if (mIndicatorDots != null) {
            mIndicatorDots.updateDot(mPin.length());
        }
    }

    /**
     * Resets the {@link PinLockView}, clearing the entered pin
     * and resetting the {@link IndicatorDots} if attached
     */
    public void errorPinLockView() {
        if (mIndicatorDots != null) {
            mIndicatorDots.error();
        }
        ObjectAnimator shake = ObjectAnimator.ofFloat(this, "translationX", 0f, 100f, -100f, 0f);
        shake.setDuration(200);
        shake.start();
        resetPinLockView();

        postDelayed(() -> resetPinLockView(), 200L);
    }

    /**
     * Returns true if {@link IndicatorDots} are attached to {@link PinLockView}
     *
     * @return true if attached, false otherwise
     */
    public boolean isIndicatorDotsAttached() {
        return mIndicatorDots != null;
    }

    /**
     * Attaches {@link IndicatorDots} to {@link PinLockView}
     *
     * @param mIndicatorDots the view to attach
     */
    public void attachIndicatorDots(IndicatorDots mIndicatorDots) {
        this.mIndicatorDots = mIndicatorDots;
    }
}
