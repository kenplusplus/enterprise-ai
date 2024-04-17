# WhisperX based ASR + Diarize

1.1 Start the server

```shell
docker pull registry.cn-hangzhou.aliyuncs.com/kenplusplus/whisperx-server:latest

docker run -it -p 5000:5000 registry.cn-hangzhou.aliyuncs.com/kenplusplus/whisperx-server:latest
```

1.2 Test from client

```shell
curl -X 'POST' -F 'file=@test.mp3' http://localhost:5000/v1/audio/transcriptions
```

The example output json is [here](/containers/ken-whisperx-server/example_openai_output.json).

_NOTE: you can use [1101_515185834Y.mp3](/containers/ken-whisperx-server/1101_515185834Y.mp3) as `test.mp3`_



## 2. Development

```shell
cd containers
build -c ken-whisper-server -a build
```
