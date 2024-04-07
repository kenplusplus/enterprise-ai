# Enterprise AI

## 1. Business Pipeline

### 1.1 ChatBot Pipeline

![](/docs/chatbot-pipeline-overview.png)

### 1.2 Meeting Summary Pipeline

![](/docs/meeting-summary-pipeline-overview.png)

## 2. Micro Services

| Name | Description | Registry |
| ---- | ----------- | ----- |
| [ASR (whisper)](/containers/whisper-server/README.md)  | Auto Speech Recognition | registry.cn-hangzhou.aliyuncs.com/kenplusplus/whisper-server |
| [ASR + Diarize (whisperx)](/containers/ken-whisperx-server/README.md) | Speech Recognition + Speaker Recognition | registry.cn-hangzhou.aliyuncs.com/kenplusplus/ken-whisperx-server |
| [ASR (fast-whisper)](/containers/faster-whisper-server/README.md) | Accelerated ASR | registry.cn-hangzhou.aliyuncs.com/kenplusplus/faster-whisper-server |
| [FastChat](/containers/fastchat-server/README.md) | AMX opted IPEX based LLM | registry.cn-hangzhou.aliyuncs.com/kenplusplus/fastchat-server |
| [TTS (OpenVoice)](/containers/openvoice-server/README.md) | Text to Speech | registry.cn-hangzhou.aliyuncs.com/kenplusplus/openvoice-server |
| [TTS （OpenTTS)](/containers/opentts-server/) | Text to Speech | registry.cn-hangzhou.aliyuncs.com/kenplusplus/opentts-server |
