package com.msinghal34.pinlockview;

/**
 * The customization options for the buttons in {@link PinLockView}
 * passed to the {@link PinLockAdapter} to decorate the individual views
 * <p>
 * Created by aritraroy on 01/06/16.
 */
public class CustomizationOptionsBundle {

    private int textColor;
    private int buttonBackgroundColor;
    private int textSize;
    private int buttonSize;
    private int deleteButtonDrawableId;
    private int deleteButtonSize;
    private boolean showDeleteButton;

    public CustomizationOptionsBundle() {
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getButtonBackgroundColor() {
        return buttonBackgroundColor;
    }

    public void setButtonBackgroundColor(int buttonBackgroundColor) {
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
