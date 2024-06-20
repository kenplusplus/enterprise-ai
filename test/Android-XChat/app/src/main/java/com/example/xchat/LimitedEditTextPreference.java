package com.example.xchat;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;

import androidx.preference.EditTextPreference;

public class LimitedEditTextPreference extends EditTextPreference {

    public LimitedEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnBindEditTextListener(editText -> editText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 8)}));
        setOnPreferenceChangeListener((preference, newValue) -> {
            try {
                int value = Integer.parseInt((String) newValue);
                return value >= 0 && value <= 8;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }
}
