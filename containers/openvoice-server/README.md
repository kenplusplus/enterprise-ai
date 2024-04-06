# TTS Server

Use [OpenVoice](https://github.com/kenplusplus/OpenVoice) setup the TTS server

![](/docs/tts-service.png)

## 1. Quick Start

```shell
docker pull registry.cn-hangzhou.aliyuncs.com/kenplusplus/openvoice-server:latest
docker run -it -p 5500:5500 registry.cn-hangzhou.aliyuncs.com/kenplusplus/openvoice-server:latest
```

_NOTE: the Restful API is compatible with OpenAI [speech API](https://platform.openai.com/docs/api-reference/audio/createSpeech)._

Then you can use following approach to test TTS server:

```shell
curl -X 'POST' http://localhost:5500/v1/audio/speech \
    -d '{ "input": "人民是我们党执政的最大底气，是我们共和国的坚实根基，是我们强党兴国的根本所在" }' \
    -H 'accept: application/json' \
    -H 'Content-Type: application/json' --output test55.mp3
```

_NOTE: Please replace `localhost` into your hosting TTS server address._