# vllm xft Server

## 1. Introduction
Provide LLM service based on [vllm](https://github.com/vllm-project/vllm) and [xFasterTransformer](https://github.com/intel/xFasterTransformer).

Currently, the python package is from [vllm-xft](https://github.com/Duyi-Wang/vllm)

## 2. Quick Start
### 2.1 Build Docker image sample
```
docker build \
    --build-arg https_proxy=$https_proxy \
    --build-arg http_proxy=$http_proxy \
    -f Dockerfile.vllm-xft.base \
    -t vllm-xft-base:<TAG> .
```

```
docker build \
    --build-arg https_proxy=$https_proxy \
    --build-arg http_proxy=$http_proxy \
    --build-arg HF_ENDPOINT=$HF_ENDPOINT \
    --build-arg HF_TOKEN=$HF_TOKEN \
    -f Dockerfile.vllm-xft-llama2-7b \
    -t vllm-xft-llama2-7b:<TAG> .
```

```Note```: If you are unable to download model files from [huggingface](https://huggingface.co/) directly, please set environment variables ```HF_ENDPOINT``` when building docker image.


### 2.2 Launch server
```
docker run \
    -e port=<PORT> \
    -p <PORT>:<PORT> \
    -d \
    vllm-xft-llama2-7b:<TAG> 
```

```Note```: Here, \<PORT\> can be any vaild port, for example:1234. If not specifid, default port is 8000.

### 2.3 Check service
```
curl http://<IP>:<PORT>/v1/models | jq

{
...
  "object": "list",
  "data": [
    {
      "id": "/data/Llama-2-7b-chat-hf-xft/",
      "object": "model",
      "created": 1714458490,
      "owned_by": "vllm",
...
....
```
Which mean the service is running and the model name is ```/data/Llama-2-7b-chat-hf-xft/```

### 2.4 Client test
```
 curl http://<IP>:<PORT>/v1/completions -H "Content-Type: application/json" -d '{
    "model": "/data/Llama-2-7b-chat-hf-xft/",
    "prompt": "Once upon a time",
    "max_tokens": 41,
    "temperature": 0.5
}' | jq

{
...
  "model": "/data/Llama-2-7b-chat-hf-xft/",
  "choices": [
    {
      "index": 0,
      "text": ", in a far-off kingdom, there lived a young prince named Leo. Leo was a kind and gentle soul, loved by all who knew him. He was particularly fond of animals, and spent much",
...
```