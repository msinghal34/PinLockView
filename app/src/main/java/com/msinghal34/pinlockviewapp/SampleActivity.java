package com.msinghal34.pinlockviewapp;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.msinghal34.pinlockview.IndicatorDots;
import com.msinghal34.pinlockview.PinLockListener;
import com.msinghal34.pinlockview.PinLockView;

public class SampleActivity extends AppCompatActivity {

    public static final String TAG = "PinLockView";

    private final PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public Boolean onComplete(String pin) {
            Log.d(TAG, "Pin complete: " + pin);
            return false;
        }

        @Override
        public void onEmpty() {
            Log.d(TAG, "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sample);

        PinLockView mPinLockView = findViewById(R.id.pin_lock_view);
        IndicatorDots mIndicatorDots = findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);
    }
}
