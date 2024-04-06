# Faster Whisper Server

## 1. Quick Start

1.1 Start the server

```shell
docker pull registry.cn-hangzhou.aliyuncs.com/kenplusplus/faster-whisper-server:latest

docker run -it -p 5002:5000 registry.cn-hangzhou.aliyuncs.com/kenplusplus/faster-whisper-server:latest
```

1.2 Test from client

```shell
curl -X 'POST' -F 'file=@test.mp3' http://localhost:5002/v1/audio/transcriptions
```

_NOTE: you can use [1101_515185834Y.mp3](/containers/ken-whisperx-server/1101_515185834Y.mp3) as `test.mp3`_


## 2. Development

```shell
cd containers
build -c faster-whisper -a build
```

If want to change the MODEL_SIZE, please refer [Dockerfile](/containers/faster-whisper-server/Dockerfile).