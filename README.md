# Enterprise AI

## 1. Overall

The Enterprise AI includes XIM (Xeon Inference Microservice) and scalable cloud
native framework which is part of [OPEA(Open Platform Enterprise AI)](https://github.com/opea-project).


![](/docs/xim-opea-overall.png)

Xeon Inference Microservice (XIM) is a scalable and stateless container service
exposing standard resful APIs. It allow Intel accelerators to optimize the
inference engine and customized model for AIGC workload.

![](/docs/xim-arch.png)

| Layer name | Description |
| ---------- | ----------- |
| Accelerators | A XIM could be optimized by any of Intel Accelerators like AMX/VNNI/AVX512 etc |
| Optimized Engine | Intel provide many engine for different purposes like OneAPI, xFT, IPEX |
| Models | A model can be customized for xFT format in different Quantization like BF16/INT8/FP4 etc |
| Microservices | A container services with stateless design to support scalable ochrestartion |
| API | LangChain/LlamaIndex and existing vendor like OpenAI provide industrial standard restfule API to expsoe service|

Please refer [here](/docs/xim.md) for more details.

## 2. Business Pipeline

### 2.1 ChatBot Pipeline

![](/docs/chatbot-pipeline-overview.png)

### 2.2 Meeting Summary Pipeline

![](/docs/meeting-summary-pipeline-overview.png)

More Business pipeline please refer to [OPEA's GenAIExamples](https://github.com/opea-project/GenAIExamples)

## 3. XIM (Xeon Inference Microservice)

| Name | Description | Registry |
| ---- | ----------- | ----- |
| [ASR (whisper)](/xim/whisper-server/README.md)  | Auto Speech Recognition | registry.cn-hangzhou.aliyuncs.com/kenplusplus/whisper-server |
| [ASR + Diarize (whisperx)](/xim/whisperx-server/README.md) | Speech Recognition + Speaker Recognition | registry.cn-hangzhou.aliyuncs.com/kenplusplus/whisperx-server |
| [ASR (fast-whisper)](/xim/faster-whisper-server/README.md) | Accelerated ASR | registry.cn-hangzhou.aliyuncs.com/kenplusplus/faster-whisper-server |
| [FastChat](/xim/fastchat-server/README.md) | AMX opted IPEX based LLM | registry.cn-hangzhou.aliyuncs.com/kenplusplus/fastchat-server |
| [TTS (OpenVoice)](/xim/openvoice-server/README.md) | Text to Speech | registry.cn-hangzhou.aliyuncs.com/kenplusplus/openvoice-server |
| [TTS （OpenTTS)](/xim/opentts-server/) | Text to Speech | registry.cn-hangzhou.aliyuncs.com/kenplusplus/opentts-server |

Following models are used:

| Name | Size | Micro Services | Description |
| ---- | ---- | -------------- | ----------- |
| [THUDM/chatglm2-6b](https://github.com/THUDM/ChatGLM2-6B) | 12G | [FastChat](/xim/fastchat-server/README.md) | LLM model |
| [Trelis/Llama-2-7b-chat-hf-shared-bf16](https://huggingface.co/Trelis/Llama-2-7b-chat-hf-sharded-bf16) | 25G | [FastChat](/xim/fastchat-server/README.md) | LLM model using BF16 for AMX |
| [lmsys/vicuna-7b-v1.3](https://huggingface.co/lmsys/vicuna-7b-v1.3) | 13.5G | [FastChat](/xim/fastchat-server/README.md) | LLM model using INT8 for VNNI |
| [Systran/faster-whisper-tiny](https://huggingface.co/Systran/faster-whisper-tiny) | 75M |[faster-whisper](/xim/faster-whisper-server/README.md) | Speech Recognition model |
| [pyannote/speaker-diarization-3.1](https://huggingface.co/pyannote/speaker-diarization-3.1) | 14M | [whisperx-server](/xim/whisperx-server/README.md) | Speaker Diarize |
| [pyannote/segmentation-3.0](https://huggingface.co/pyannote/segmentation-3.0) | 5.8M | [whisperx-server](/xim/whisperx-server/README.md) | Speech Segmentation |
| [jonatasgrosman/wav2vec2-large-xlsr-53-chinese-zh-cn](https://huggingface.co/jonatasgrosman/wav2vec2-large-xlsr-53-chinese-zh-cn) | 2.4G | [whisperx-server](/xim/whisperx-server/README.md) | Chinese Speech to vector |
| [pyannote/wespeaker-voxceleb-resnet34-LM](https://huggingface.co/pyannote/wespeaker-voxceleb-resnet34-LM) | 51M | [whisperx-server](/xim/whisperx-server/README.md) | Extract embedding  |
| [silero-vad](https://github.com/snakers4/silero-vad) | 17M | [openvoice-server](/xim/openvoice-server/README.md) |  Voice Activity Detector  |
| [whisper(small)](https://openaipublic.azureedge.net/main/whisper/models/9ecf779972d90ba49c06d968637d720dd632c55bbf19d441fb42bf17a411e794/small.pt)| 244M | [whisper-server](/xim/whisper-server/README.md) | OpenAI whisper model |

## 4. Business Pipeline Orchestration

### 4.1 Flowise
TBD

### 4.2 Dify
TBD

## 5. Cloud Native Services Orchestration
TBD

### 5.1 Scalability for Concurrent
TBD

### 5.2 Sustainability
TBD

### 5.3 Confidentiality
TBD

## 6. Deployment
TBD
