package com.example.xchat;

public class ChatSettings {
    private String url;
    private String modelName;
    private String apiKey;
    private int maxTokens;

    public ChatSettings(String url, String modelName, String apiKey, int maxTokens) {
        this.url = url;
        this.modelName = modelName;
        this.apiKey = apiKey;
        this.maxTokens = maxTokens;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
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
}
