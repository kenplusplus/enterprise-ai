package com.example.xchat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            // Set summaries to current values
            EditTextPreference urlPreference = findPreference("url");
            EditTextPreference modelNamePreference = findPreference("model_name");
            EditTextPreference apiKeyPreference = findPreference("api_key");
            EditTextPreference maxTokensPreference = findPreference("max_tokens");

            if (urlPreference != null) {
                urlPreference.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
            }

            if (modelNamePreference != null) {
                modelNamePreference.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
            }

            if (apiKeyPreference != null) {
                apiKeyPreference.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
            }

            if (maxTokensPreference != null) {
                maxTokensPreference.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
            }
        }
    }
}
