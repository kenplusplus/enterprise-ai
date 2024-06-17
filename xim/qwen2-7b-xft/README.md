# vllm-xft-qwen2-7b

## 1. Introduction
This service is base on qwen2-7b and vllm-xft
The model is from: https://www.modelscope.cn/models/qwen/Qwen2-7B-Instruct/summary

1.2 Test from client

```shell
curl http://localhost:8000/v1/models | jq
```

## 2. Development

### 2.1 Build Docker image
```shell
docker build --build-arg http_proxy --build-arg https_proxy -f Dockerfile -t vllm-xft-qwen2-7b:latest .
```

### 2.2 Launch server samples
```shell
docker pull ccr-registry.caas.intel.com/opea-xim/vllm-xft-qwen2-7b:latest
docker run -itd --rm -p 8000:8000 vllm-xft-qwen2-7b
docker run -itd --rm -e xft_dtype=fp16 -e kv_cache_dtype=int8 -p 8000:8000 vllm-xft-qwen2-7b
```
Note: Can set ```xft_dtype``` and ```kv_cache_dtype``` by ```docker run -e ```

### 2.3 Client test
- Restful API by curl
```shell
curl http://localhost:8000/v1/models | jq
curl http://localhost:8000/v1/chat/completions    -H "Content-Type: application/json"   -d     '{"model": "Qwen2-7B-xft", "messages": [{"role": "user", "content": "How are you?"}], "temperature":"0.9" }' | jq
```
