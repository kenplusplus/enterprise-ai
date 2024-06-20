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
            EditTextPreference ipPreference = findPreference("ip_address");
            EditTextPreference portPreference = findPreference("port");
            EditTextPreference modelNamePreference = findPreference("model_name");
            EditTextPreference apiKeyPreference = findPreference("api_key");

            EditTextPreference maxTokensPreference = findPreference("max_tokens");
            EditTextPreference temperaturePreference = findPreference("temperature");
            EditTextPreference topPPreference = findPreference("top_p");
            EditTextPreference topKPreference = findPreference("top_k");
            EditTextPreference penaltyPreference = findPreference("repetition_penalty");
            EditTextPreference chatHistoryPreference = findPreference("chat_history");
            EditTextPreference sysPromptPreference = findPreference("system_prompt");

            if (ipPreference != null) {
                ipPreference.setSummaryProvider(new CustomSummaryProvider(requireContext()));
            }

            if (portPreference != null) {
                portPreference.setSummaryProvider(new CustomSummaryProvider(requireContext()));
            }

            if (modelNamePreference != null) {
                modelNamePreference.setSummaryProvider(new CustomSummaryProvider(requireContext()));
            }

            if (apiKeyPreference != null) {
                apiKeyPreference.setSummaryProvider(new CustomSummaryProvider(requireContext()));
            }

            if (maxTokensPreference != null) {
                maxTokensPreference.setSummaryProvider(new CustomSummaryProvider(requireContext(), R.string.tip_max_tokens));
            }

            if (temperaturePreference != null) {
                temperaturePreference.setSummaryProvider(new CustomSummaryProvider(requireContext(), R.string.tip_temperature));
            }

            if (topPPreference != null) {
                topPPreference.setSummaryProvider(new CustomSummaryProvider(requireContext(), R.string.tip_top_p));
            }

            if (topKPreference != null) {
                topKPreference.setSummaryProvider(new CustomSummaryProvider(requireContext(), R.string.tip_top_k));
            }

            if (penaltyPreference != null) {
                penaltyPreference.setSummaryProvider(new CustomSummaryProvider(requireContext(), R.string.tip_repetition_penalty));
            }

            if (chatHistoryPreference != null) {
                chatHistoryPreference.setSummaryProvider(new CustomSummaryProvider(requireContext(), R.string.tip_chat_history));
            }

            if (sysPromptPreference != null) {
                sysPromptPreference.setSummaryProvider(new CustomSummaryProvider(requireContext(), R.string.tip_system_prompt));
            }
        }
    }
}
