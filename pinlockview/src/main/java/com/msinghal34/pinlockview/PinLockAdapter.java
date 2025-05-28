package com.msinghal34.pinlockview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

public class PinLockAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_NUMBER = 0;
    private static final int VIEW_TYPE_DELETE = 1;

    private final Context mContext;
    private CustomizationOptionsBundle mCustomizationOptionsBundle;
    private OnNumberClickListener mOnNumberClickListener;
    private OnDeleteClickListener mOnDeleteClickListener;

    private int[] mKeyValues;

    public PinLockAdapter(Context context) {
        this.mContext = context;
        this.mKeyValues = getAdjustKeyValues(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_NUMBER) {
            View view = inflater.inflate(R.layout.layout_number_item, parent, false);
            viewHolder = new NumberViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.layout_delete_item, parent, false);
            viewHolder = new DeleteViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_NUMBER) {
            NumberViewHolder vh1 = (NumberViewHolder) holder;
            configureNumberButtonHolder(vh1, position);
        } else if (holder.getItemViewType() == VIEW_TYPE_DELETE) {
            DeleteViewHolder vh2 = (DeleteViewHolder) holder;
            configureDeleteButtonHolder(vh2);
        }
    }

    private void configureNumberButtonHolder(NumberViewHolder holder, int position) {
        if (holder != null) {
            if (position == 9) {
                holder.mNumberButton.setVisibility(View.GONE);
            } else {
                holder.mNumberButton.setVisibility(View.VISIBLE);
                holder.mNumberText.setText(String.valueOf(mKeyValues[position]));
                holder.mNumberText.setTag(mKeyValues[position]);
            }

            if (mCustomizationOptionsBundle != null) {
                holder.mNumberText.setTextColor(mCustomizationOptionsBundle.getTextColor());
                holder.mNumberText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCustomizationOptionsBundle.getTextSize());

                if (mCustomizationOptionsBundle.showButtonPressAnimation()) {
                    try {
                        TypedValue outValue = new TypedValue();
                        mContext.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
                        holder.mNumberText.setBackgroundResource(outValue.resourceId);
                    } catch (Exception e) {
                        Log.e("PinLockView", "Exception while setting press feedback", e);
                    }
                } else {
                    holder.mNumberText.setBackground(null);
                }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        mCustomizationOptionsBundle.getButtonSize(),
                        mCustomizationOptionsBundle.getButtonSize());
                holder.mNumberButton.setLayoutParams(params);
                holder.mNumberButton.setBackgroundTintList(ColorStateList.valueOf(mCustomizationOptionsBundle.getButtonBackgroundColor()));
            }

            holder.mNumberText.setOnClickListener(v -> {
                if (mOnNumberClickListener != null) {
                    mOnNumberClickListener.onNumberClicked((Integer) v.getTag());
                }
            });
        }
    }

    private void configureDeleteButtonHolder(DeleteViewHolder holder) {
        if (mCustomizationOptionsBundle.showDeleteButton()) {
            holder.mDeleteImage.setVisibility(View.VISIBLE);
            if (mCustomizationOptionsBundle.getDeleteButtonDrawable() == R.drawable.ic_delete) {
                try {
                    LayerDrawable layeredDrawable = (LayerDrawable) AppCompatResources.getDrawable(mContext, R.drawable.ic_delete);
                    if (layeredDrawable != null) {
                        Drawable foreground = layeredDrawable.findDrawableByLayerId(R.id.foreground);
                        Drawable background = layeredDrawable.findDrawableByLayerId(R.id.background);

                        if (foreground != null) {
                            foreground.setColorFilter(new PorterDuffColorFilter(
                                    mCustomizationOptionsBundle.getTextColor(),
                                    PorterDuff.Mode.SRC_IN
                            ));
                        }

                        if (background != null) {
                            background.setColorFilter(new PorterDuffColorFilter(
                                    mCustomizationOptionsBundle.getButtonBackgroundColor(),
                                    PorterDuff.Mode.SRC_IN
                            ));
                        }

                        holder.mDeleteImage.setImageDrawable(layeredDrawable);
                    }
                } catch (Exception e) {
                    Log.e("PinLockView", "Something went wrong while handling layer drawable");
                    holder.mDeleteImage.setImageResource(R.drawable.ic_delete);
                    holder.mDeleteImage.setColorFilter(mCustomizationOptionsBundle.getTextColor(), PorterDuff.Mode.SRC_ATOP);
                }
            } else {
                holder.mDeleteImage.setImageResource(mCustomizationOptionsBundle.getDeleteButtonDrawable());
            }
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                    mCustomizationOptionsBundle.getDeleteButtonSize(),
                    mCustomizationOptionsBundle.getDeleteButtonSize());
            holder.mDeleteImage.setLayoutParams(imageParams);

            holder.mDeleteImage.setOnClickListener(v -> {
                if (mOnDeleteClickListener != null) {
                    mOnDeleteClickListener.onDeleteClicked();
                }
            });
        } else {
            holder.mDeleteImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return 12;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return VIEW_TYPE_DELETE;
        }
        return VIEW_TYPE_NUMBER;
    }

    public void setKeyValues(int[] keyValues) {
        this.mKeyValues = getAdjustKeyValues(keyValues);
        notifyDataSetChanged();
    }

    private int[] getAdjustKeyValues(int[] keyValues) {
        int[] adjustedKeyValues = new int[keyValues.length + 1];
        for (int i = 0; i < keyValues.length; i++) {
            if (i < 9) {
                adjustedKeyValues[i] = keyValues[i];
            } else {
                adjustedKeyValues[i] = -1;
                adjustedKeyValues[i + 1] = keyValues[i];
            }
        }
        return adjustedKeyValues;
    }

    public void setOnItemClickListener(OnNumberClickListener onNumberClickListener) {
        this.mOnNumberClickListener = onNumberClickListener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.mOnDeleteClickListener = onDeleteClickListener;
    }

    public void setCustomizationOptions(CustomizationOptionsBundle customizationOptionsBundle) {
        this.mCustomizationOptionsBundle = customizationOptionsBundle;
    }

    public interface OnNumberClickListener {
        void onNumberClicked(int keyValue);
    }

    public interface OnDeleteClickListener {
        void onDeleteClicked();
    }

    public class NumberViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mNumberButton;
        TextView mNumberText;

        public NumberViewHolder(final View itemView) {
            super(itemView);
            mNumberButton = itemView.findViewById(R.id.button);
            mNumberText = itemView.findViewById(R.id.number);
        }
    }

    public class DeleteViewHolder extends RecyclerView.ViewHolder {
        ImageView mDeleteImage;

        public DeleteViewHolder(final View itemView) {
            super(itemView);
            mDeleteImage = itemView.findViewById(R.id.image);
        }
    }
}
