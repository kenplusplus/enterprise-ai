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

## 3. Models
Following models are used:

| Name | Size | Micro Services | Description |
| ---- | ---- | -------------- | ----------- |
| [Trelis/Llama-2-7b-chat-hf-shared-bf16](https://huggingface.co/Trelis/Llama-2-7b-chat-hf-sharded-bf16) | 25G | [FastChat](/containers/fastchat-server/README.md) | LLM model using BF16 for AMX |
| [lmsys/vicuna-7b-v1.3](https://huggingface.co/lmsys/vicuna-7b-v1.3) | 13.5G | [FastChat](/containers/fastchat-server/README.md) | LLM model using INT8 for VNNI |
| [Systran/faster-whisper-tiny](https://huggingface.co/Systran/faster-whisper-tiny) | 75M |[faster-whisper](/containers/faster-whisper-server/README.md) | Speech Recognition model |
| [pyannote/speaker-diarization-3.1](https://huggingface.co/pyannote/speaker-diarization-3.1) | 14M | [ken-whisperx-server](/containers/ken-whisperx-server/README.md) | Speaker Diarize |
| [pyannote/segmentation-3.0](https://huggingface.co/pyannote/segmentation-3.0) | 5.8M | [ken-whisperx-server](/containers/ken-whisperx-server/README.md) | Speech Segmentation |
| [jonatasgrosman/wav2vec2-large-xlsr-53-chinese-zh-cn](https://huggingface.co/jonatasgrosman/wav2vec2-large-xlsr-53-chinese-zh-cn) | 2.4G | [ken-whisperx-server](/containers/ken-whisperx-server/README.md) | Chinese Speech to vector |
| [pyannote/wespeaker-voxceleb-resnet34-LM](https://huggingface.co/pyannote/wespeaker-voxceleb-resnet34-LM) | 51M | [ken-whisperx-server](/containers/ken-whisperx-server/README.md) | Extract embedding  |
| [silero-vad](https://github.com/snakers4/silero-vad) | 17M | [openvoice-server](/containers/openvoice-server/README.md) |  Voice Activity Detector  |
| [whisper(small)](https://openaipublic.azureedge.net/main/whisper/models/9ecf779972d90ba49c06d968637d720dd632c55bbf19d441fb42bf17a411e794/small.pt)| 244M | [whisper-server](/containers/whisper-server/README.md) | OpenAI whisper model |