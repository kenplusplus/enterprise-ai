# Enterprise AI

- ChatBot Pipeline

![](/docs/chatbot-pipeline-overview.png)

- Meeting Summary Pipeline

![](/docs/meeting-summary-pipeline-overview.png)

## 1. Micro Services



| Name | Description | Registry |
| ---- | ----------- | ----- |
| [ASR (whisper)](/containers/whisper-server/README.md)  | Auto Speech Recognition | registry.cn-hangzhou.aliyuncs.com/kenplusplus/whisper-server |
| [ASR + Diarize (whisperx)](/containers/ken-whisperx-server/README.md) | Speech Recognition + Speaker Recognition | registry.cn-hangzhou.aliyuncs.com/kenplusplus/ken-whisperx-server |
| [ASR (fast-whisper)](/containers/faster-whisper-server/README.md) | Accelerated ASR | registry.cn-hangzhou.aliyuncs.com/kenplusplus/faster-whisper-server |
| [FastChat](/containers/fastchat-server/README.md) | AMX opted IPEX based LLM | registry.cn-hangzhou.aliyuncs.com/kenplusplus/fastchat-server |
| [TTS (OpenVoice)](/containers/openvoice-server/README.md) | Text to Speech | registry.cn-hangzhou.aliyuncs.com/kenplusplus/openvoice-server |
| [TTS （OpenTTS)](/containers/opentts-server/) | Text to Speech | registry.cn-hangzhou.aliyuncs.com/kenplusplus/opentts-server |

Registry:
## 2. Develop

Example:

```shell
# Default build script
./containers/build.sh

# Only build whisper server, without push to registry
./containers/build.sh -c ken-whisper-server -a build

# Only build TTS server, without push to registry
./containers/build.sh -c ken-opentts-server -a build
```

The complete instruction is:

```shell
usage: build.sh [OPTION]...
    -a <build|download|publish|save|all>  all is default, which not include save. Please execute save explicity if need.
    -r <registry prefix> the prefix string for registry
    -c <container name> same as directory name
    -g <tag> container image tag
    -f Clean build
```
