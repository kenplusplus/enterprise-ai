# Flowise Server

## 1. Introduction
This is a replacement for Flowise.
This Flowise has integrated xFaiss which accelerated Faiss with AMX.

## 2. Quick Start
### 2.1 Build Docker image
```shell
DOCKER_BUILDKIT=1 docker build --build-arg https_proxy --build-arg http_proxy --secret id=.netrc,src=$HOME/.netrc -t xflowise:<TAG> .
```

### 2.2 Launch server
```shell
docker run -itd -p 3000:3000 xflowise:latest
```
Now Flowise Server is listening at 0.0.0.0:3000

