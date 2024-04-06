
# Whisper Based ASR Server

## 1. Introduction

![](/docs/asr-service.png)

## 2. Quick Start

### 2.1 Start Server

```shell
docker pull registry.cn-hangzhou.aliyuncs.com/kenplusplus/ken-whisper-server:latest
docker run -it -p 5000:5000 registry.cn-hangzhou.aliyuncs.com/kenplusplus/ken-whisper-server
```

_NOTE: the Restful API is compatible with OpenAI [Transcription API](https://platform.openai.com/docs/api-reference/audio/createTranscription)._

Then you can use following approach to test ASR server:

### 2.2 Test from Client

- Use Shell command line

```shell
curl -X 'POST' -F "file=@/path/to/file" http://localhost:5000/v1/audio/transcriptions
```

_NOTE: You can replace /path/to/file to [example file](./containers/ken-whisper-server/test22.wav)._

- Use simple application

```shell
python ./containers/whisper-server/whisper_client.py -f <audio file.wav> -u http://localhost:5000/whisper
```

_NOTE: Please replace `localhost` into your hosting ASR server address._

## 3. Whisper Models

| Size | Parameters | English-only model | Multilingual model | Required VRAM | Relative Speed |
| ---- | ---------- | ------------------ | ------------------ | ------------- | -------------- |
| tiny          | 39M   | [tiny.en](https://openaipublic.azureedge.net/main/whisper/models/d3dd57d32accea0b295c96e26691aa14d8822fac7d9d27d5dc00b4ca2826dd03/tiny.en.pt) | [tiny](https://openaipublic.azureedge.net/main/whisper/models/65147644a518d12f04e32d6f3b26facc3f8dd46e5390956a9424a650c0ce22b9/tiny.pt) | ~1GB | ~32x |
| base          | 74M   | [base.en](https://openaipublic.azureedge.net/main/whisper/models/25a8566e1d0c1e2231d1c762132cd20e0f96a85d16145c3a00adf5d1ac670ead/base.en.pt) | [base](https://openaipublic.azureedge.net/main/whisper/models/ed3a0b6b1c0edf879ad9b11b1af5a0e6ab5db9205f891f668f8b0e6c6326e34e/base.pt) | ~1GB | ~16x |
| small         | 244M  | [small.en](https://openaipublic.azureedge.net/main/whisper/models/f953ad0fd29cacd07d5a9eda5624af0f6bcf2258be67c92b79389873d91e0872/small.en.pt) | [small](https://openaipublic.azureedge.net/main/whisper/models/9ecf779972d90ba49c06d968637d720dd632c55bbf19d441fb42bf17a411e794/small.pt) | ~2GB | ~6x |
| medium        | 769M  | [medium.en](https://openaipublic.azureedge.net/main/whisper/models/d7440d1dc186f76616474e0ff0b3b6b879abc9d1a4926b7adfa41db2d497ab4f/medium.en.pt) | [medium](https://openaipublic.azureedge.net/main/whisper/models/345ae4da62f9b3d59415adc60127b97c714f32e89e936602e85993674d08dcb1/medium.pt) | ~5GB | ~2x |
| large-v1      | 1550M | | [large-v1](https://openaipublic.azureedge.net/main/whisper/models/e4b87e7e0bf463eb8e6956e646f1e277e901512310def2c24bf0e11bd3c28e9a/large.pt) | ~10GB | 1x |
| large-v2      |  |  | [large-v2](https://openaipublic.azureedge.net/main/whisper/models/81f7c96c852ee8fc832187b0132e569d6c3065a3252ed18e56effd0b6a73e524/large-v2.pt) |  |  |
| large-v3      |  | | [large-v3](https://openaipublic.azureedge.net/main/whisper/models/e5b1a55b89c1367dacf97e3e19bfd829a01529dbfdeefa8caeb59b3f1b81dadb/large-v3.pt) |  |  |

_NOTE: By default, using the small model at [here](./containers/ken-whisper-server/app.py)_
