package com.example.xchat;

import android.content.Context;
import android.content.res.Resources;

import androidx.preference.EditTextPreference;

public class CustomSummaryProvider implements EditTextPreference.SummaryProvider<EditTextPreference> {

    private final Context context;

    private final int tipResId;

    public CustomSummaryProvider(Context context, int tipResId) {
        this.context = context;
        this.tipResId = tipResId;
    }

    public CustomSummaryProvider(Context context) {
        this.context = context;
        this.tipResId = -1;
    }

    @Override
    public CharSequence provideSummary(EditTextPreference preference) {
        String value = preference.getText();
        Resources res = context.getResources();
        if (tipResId != -1) {
            String tip = res.getString(tipResId);
            String strHead = res.getString(R.string.str_current_value);
            return tip + "\n" + strHead + ": " + value;
        } else {
            String strHead = res.getString(R.string.str_current_value);
            return strHead + ": " + value;
        }
    }
}
