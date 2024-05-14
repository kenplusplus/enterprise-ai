## 1. Introduction
This is a video generation service.
currently it leverages animateddiff-lightning

## 2. Quick Start
### 2.1 Build Docker image
```shell
docker build --build-arg HTTP_PROXY=$http_proxy --build-arg HTTPS_PROXY=$https_proxy -t video_gen:latest .
```

### 2.2 Launch server
```shell
docker run -itd -p 5000:5000 video_gen:latest
```

### 2.3 Client test
- Restful API by curl
```shell
curl --noproxy localhost -X POST http://localhost:5000/create_video/ -H 'Content-Type: application/json' -d '{"label":"shot_1", "text":"tropical beach with boats floating"}'

curl --noproxy localhost -X GET http://localhost:5000/download_video/shot_1_fps8.mp4 -o /tmp/shot_1_fps8.mp4
```


- python example TBD
```python
```