package com.example.xchat;

import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChatSettings {
    private String url;
    private String modelName;
    private String apiKey;
    private int maxTokens;

    private final float temperature;
    private final float topP;
    private final int topK;
    private final float penalty;
    private final int historyRounds;
    public final String systemPrompt;

    public ChatSettings(String url, String modelName, String apiKey, int maxTokens, float temperature, float topP, int topK, float penalty, int historyRounds, String systemPrompt) {
        this.url = url;
        this.modelName = modelName;
        this.apiKey = apiKey;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
        this.topP = topP;
        this.topK = topK;
        this.penalty = penalty;
        this.historyRounds = historyRounds;
        this.systemPrompt = systemPrompt;
    }

    public String getUrl() {
        return url;
    }

    public String getModelName() {
        return modelName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getTopP() {
        return topP;
    }

    public int getTopK() {
        return topK;
    }

    public float getPenalty() {
        return penalty;
    }

    public int getHistoryRounds() {
        return historyRounds;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "url='" + url + '\'' +
                ", modelName='" + modelName + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", maxTokens=" + maxTokens +
                '}';
    }

    @Nullable
    public static ChatSettings fromPreferences(Context ctx) {
        // Get SharedPreferences
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(ctx);

        // Retrieve values from SharedPreferences
        String strIP =
                sharedPreferences.getString("ip_address", "101.201.111.141");
        String strPort = sharedPreferences.getString("port", "8000");
        String url = String.format("http://%s:%s/v1/chat/completions", strIP, strPort);
        String modelName = sharedPreferences.getString("model_name", "chatglm2-6b");
        String apiKey = sharedPreferences.getString("api_key", "");

        String maxTokensString = sharedPreferences.getString("max_tokens", "512");
        int maxTokens = -1;
        try {
            maxTokens = Integer.parseInt(maxTokensString);
        } catch (NumberFormatException e) {
            maxTokens = 512;
        }

        // Get preferences for temperature, top_p, top_k, repetition_penalty
        // and convert to float or integer value
        String temperatureString = sharedPreferences.getString("temperature", "0.9");
        String topPString = sharedPreferences.getString("top_p", "0.9");
        String topKString = sharedPreferences.getString("top_k", "20");
        String penaltyString = sharedPreferences.getString("repetition_penalty", "1.0");
        String historyString = sharedPreferences.getString("chat_history", "3");
        String systemPrompt = sharedPreferences.getString("system_prompt", "你是一个运行在Intel至强平台上由xFasterTransformer推理框架部署的AI小助手。");
        float temperature = Float.parseFloat(temperatureString);
        float topP = Float.parseFloat(topPString);
        int topK = Integer.parseInt(topKString);
        float penalty = Float.parseFloat(penaltyString);
        int historyRounds = Integer.parseInt(historyString);

        return new ChatSettings(url, modelName, apiKey, maxTokens, temperature, topP, topK, penalty, historyRounds, systemPrompt);
    }
}
