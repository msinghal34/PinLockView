package com.msinghal34.pinlockview;

import androidx.annotation.ColorInt;

/**
 * The customization options for the buttons in {@link PinLockView}
 * passed to the {@link PinLockAdapter} to decorate the individual views
 */
public class CustomizationOptionsBundle {

    private @ColorInt int textColor;
    private @ColorInt int buttonBackgroundColor;
    private int textSize;
    private int buttonSize;
    private int deleteButtonDrawableId;
    private int deleteButtonSize;
    private boolean showDeleteButton;

    public CustomizationOptionsBundle() {
    }

    public @ColorInt int getTextColor() {
        return textColor;
    }

    public void setTextColor(@ColorInt int textColor) {
        this.textColor = textColor;
    }

    public @ColorInt int getButtonBackgroundColor() {
        return buttonBackgroundColor;
    }

    public void setButtonBackgroundColor(@ColorInt int buttonBackgroundColor) {
        this.buttonBackgroundColor = buttonBackgroundColor;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getButtonSize() {
        return buttonSize;
    }

    public void setButtonSize(int buttonSize) {
        this.buttonSize = buttonSize;
    }

    public int getDeleteButtonDrawable() {
        return deleteButtonDrawableId;
    }

    public void setDeleteButtonDrawable(int deleteButtonDrawableId) {
        this.deleteButtonDrawableId = deleteButtonDrawableId;
    }

    public int getDeleteButtonSize() {
        return deleteButtonSize;
    }

    public void setDeleteButtonSize(int deleteButtonSize) {
        this.deleteButtonSize = deleteButtonSize;
    }

    public boolean isShowDeleteButton() {
        return showDeleteButton;
    }

    public void setShowDeleteButton(boolean showDeleteButton) {
        this.showDeleteButton = showDeleteButton;
    }
}
