# Xeon Inferece Microservice (XIM)

## 1. Overview

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

## 2. Quick Start

![](/docs/create-xim.png)

To create a XIM, please:

1. Create a directory under the directory `<xeon-enterprise-ai>/xim/`
2. Create a Dockfile for container with EXPOSE port and optimized engine
3. Create a api.py to expose industrial standard API
4. Run `./build.sh -c new-xim` to build and publish XIM container to `ccr-registry.caas.intel.com/opea-xim`


## 3. Explainations

### 3.1 Microservice

Microservices is an architectural design for building a distributed application
using **containers**. They get their name because each function of the application
operates as an independent service. This architecture allows for each service
to **scale** or update without disrupting other services in the application. [refer](https://avinetworks.com/glossary/microservice/)

![](https://avinetworks.com/wp-content/uploads/2018/06/Microservices-vs-monolithic-architecture-diagram.png)

A typical microservices based design could support the divese front-ends like
mobile app or browser via **API** (RESTFul or RPC) gateway.

![](https://blog.talent500.co/wp-content/uploads/2023/08/images-2023-07-27T150504.847.png)

Microservice is built from **Dockerfile** like below or [Dockerfile](/xim/whisperx/Dockerfile)

```
#------------------------------------------------------------------------------
FROM python:3.10-slim as ipex_base

RUN apt-get update && apt-get install git -y ffmpeg wget
RUN pip install intel-extension-for-pytorch==2.2.0 oneccl_bind_pt \
        --trusted-host ipex-1711374217.us-west-2.elb.amazonaws.com \
        --extra-index-url https://ipex-1711374217.us-west-2.elb.amazonaws.com/release-whl/stable/cpu/us/

#------------------------------------------------------------------------------

FROM ipex_base as base

RUN mkdir -p ~/.cache/torch/hub/checkpoints/
RUN wget -O ~/.cache/torch/whisperx-vad-segmentation.bin \
        https://whisperx.s3.eu-west-2.amazonaws.com/model_weights/segmentation/0b5b3216d60a2d32fc086b47ea8c67589aaeb26b7e07fcbe620d6d0b83e209ea/pytorch_model.bin
RUN wget -O ~/.cache/torch/hub/checkpoints/wav2vec2_fairseq_large_lv60k_asr_ls960.pth \
        https://download.pytorch.org/torchaudio/models/wav2vec2_fairseq_large_lv60k_asr_ls960.pth
RUN wget -O ~/.cache/torch/hub/checkpoints/wav2vec2_fairseq_base_ls960_asr_ls960.pth \
        https://download.pytorch.org/torchaudio/models/wav2vec2_fairseq_base_ls960_asr_ls960.pth

RUN mkdir -p /root/.cache/huggingface/hub/
ADD .model_cache/models--pyannote--segmentation-3.0 /root/.cache/huggingface/hub/models--pyannote--segmentation-3.0/
ADD .model_cache/models--pyannote--speaker-diarization-3.0 /root/.cache/huggingface/hub/models--pyannote--speaker-diarization-3.0/
ADD .model_cache/models--pyannote--wespeaker-voxceleb-resnet34-LM /root/.cache/huggingface/hub/models--pyannote--wespeaker-voxceleb-resnet34-LM/

ADD .model_cache/models--hbredin--wespeaker-voxceleb-resnet34-LM /root/.cache/huggingface/hub/models--hbredin--wespeaker-voxceleb-resnet34-LM/
ADD .model_cache/models--jonatasgrosman--wav2vec2-large-xlsr-53-chinese-zh-cn /root/.cache/huggingface/hub/models--jonatasgrosman--wav2vec2-large-xlsr-53-chinese-zh-cn/
ADD .model_cache/models--Systran--faster-whisper-small /root/.cache/huggingface/hub/models--Systran--faster-whisper-small/

#------------------------------------------------------------------------------

FROM base

ARG pip_mirror="-i https://mirrors.aliyun.com/pypi/simple/"

RUN apt-get install procps -y

WORKDIR /app/
COPY . .
RUN pip3 install -r ./requirements.txt
RUN pip3 install ./whisperx/ ${pip_mirror}

EXPOSE 5000

CMD [ "./start.sh"]
```
### 3.2 Optimizations

In the example at [](#21-microservice), following part is the optimization layer
including accelerator and optimized inference engine
```
FROM python:3.10-slim as ipex_base

RUN apt-get update && apt-get install git -y ffmpeg wget
RUN pip install intel-extension-for-pytorch==2.2.0 oneccl_bind_pt \
        --trusted-host ipex-1711374217.us-west-2.elb.amazonaws.com \
        --extra-index-url https://ipex-1711374217.us-west-2.elb.amazonaws.com/release-whl/stable/cpu/us/
```

It can also be the optimizations of **xFasterTransformer** like [Dockerfile](/xim/vllm-xft-base/Dockerfile) or below

```
FROM intel/xfastertransformer:latest
```

### 3.3 Models

The model weight can be downloaded from model server in runtime/deployment time,
or embedded into microservice's container as follows:
```
RUN wget -O ~/.cache/torch/whisperx-vad-segmentation.bin \
        https://whisperx.s3.eu-west-2.amazonaws.com/model_weights/segmentation/0b5b3216d60a2d32fc086b47ea8c67589aaeb26b7e07fcbe620d6d0b83e209ea/pytorch_model.bin
RUN wget -O ~/.cache/torch/hub/checkpoints/wav2vec2_fairseq_large_lv60k_asr_ls960.pth \
        https://download.pytorch.org/torchaudio/models/wav2vec2_fairseq_large_lv60k_asr_ls960.pth
RUN wget -O ~/.cache/torch/hub/checkpoints/wav2vec2_fairseq_base_ls960_asr_ls960.pth \
        https://download.pytorch.org/torchaudio/models/wav2vec2_fairseq_base_ls960_asr_ls960.pth

RUN mkdir -p /root/.cache/huggingface/hub/
ADD .model_cache/models--pyannote--segmentation-3.0 /root/.cache/huggingface/hub/models--pyannote--segmentation-3.0/
ADD .model_cache/models--pyannote--speaker-diarization-3.0 /root/.cache/huggingface/hub/models--pyannote--speaker-diarization-3.0/
ADD .model_cache/models--pyannote--wespeaker-voxceleb-resnet34-LM /root/.cache/huggingface/hub/models--pyannote--wespeaker-voxceleb-resnet34-LM/
```

### 3.4 API

There are following industrial standard APIs:

| Name | Vendor | Example |
| ---- | ------ | ------ |
| [Chat Complete](https://platform.openai.com/docs/api-reference/chat/create) | OpenAI | `POST https://api.openai.com/v1/chat/completions`|
| [Embedding](https://platform.openai.com/docs/api-reference/embeddings/create) | OpenAI | `POST https://api.openai.com/v1/embeddings` |
| [Image Generation](https://platform.openai.com/docs/api-reference/images/create) | OpenAI | `POST https://api.openai.com/v1/images/generations` |
| [Text to Speech](https://platform.openai.com/docs/api-reference/audio/createSpeech) | OpenAI | `POST https://api.openai.com/v1/audio/speech` |
| [Speech to Text](https://platform.openai.com/docs/api-reference/audio/createTranscription) | OpenAI | `POST https://api.openai.com/v1/audio/transcriptions` |
| [Text Generation Inference](https://huggingface.co/docs/text-generation-inference/index#text-generation-inference) | Huggingface | `POST https://127.0.0.1:8080/generate` |

[openapi.py](/xim/whisper/openapi.py) is an example to expose OpenAI compatible speech API
```
@app.post("/v1/audio/transcriptions")
def create_transcription(file: Annotated[bytes, File()],
                         model: Annotated[str, Form()] = 'whipser-1',
                         language: Annotated[str | None, Form()] = None,
                         prompt: Annotated[str | None, Form()] = None):
    temp = NamedTemporaryFile()
    temp.write(file)
    result = whisper_engine.transcribe(temp.name)
    return {
        "text": result['text']
    }

```
