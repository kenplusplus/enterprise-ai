# FunASR Server

## 1. Introduction
This is ASR service.
The code is from: https://github.com/alibaba/FunASR.git

1.2 Test from client

```shell
curl -X 'POST' -F 'file=@asr_example_en' http://localhost:5000/v1/audio/transcriptions
```

## 2. Development

### 2.1 Build Docker image
```shell
docker build --build-arg https_proxy --build-arg http_proxy -t funasr-server:<TAG> .
```

### 2.2 Launch server
```shell
docker run -itd -p 5000:5000 funasr:<TAG>
```

### 2.3 Client test
- Restful API by curl
```shell
curl -X 'POST' -F 'file=@test.mp3' http://localhost:5000/v1/audio/transcriptions
```
