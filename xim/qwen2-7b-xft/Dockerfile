FROM ccr-registry.caas.intel.com/opea-xim/vllm-xft-base:latest AS build

ARG HF_ENDPOINT
ARG HF_TOKEN

# Set the environment variable
ENV HF_TOKEN=$HF_TOKEN
ENV HF_ENDPOINT=$HF_ENDPOINT

# Prepare download model
RUN git config --global credential.helper store
RUN echo /usr/bin/python3.8/bin/huggingface-cli login --token $HF_TOKEN --add-to-git-credential

# Download model Qwen2-7B-Instruct
RUN mkdir -p /data/Qwen2-7B-Instruct && \
    python -c 'from modelscope import snapshot_download; snapshot_download("qwen/Qwen2-7B-Instruct", local_dir="/data/Qwen2-7B-Instruct")'

# Convert model
RUN python -c 'import xfastertransformer as xft; xft.Qwen2Convert().convert("/data/Qwen2-7B-Instruct","/data/Qwen2-7B-xft")'

# Cleanup
RUN mkdir /data/configs  &&  \
    cp /data/Qwen2-7B-Instruct/*.json /data/configs
RUN rm /data/Qwen2-7B-Instruct -fr

# Copy files to target image
FROM ccr-registry.caas.intel.com/opea-xim/vllm-xft-base:latest

COPY --from=build /data /data

ARG xft_dtype="bf16"
ARG kv_cache_dtype="fp16"

ENV xft_dtype=${xft_dtype} \
    kv_cache_dtype=${kv_cache_dtype}

EXPOSE 8000

WORKDIR /data

CMD ( python -m vllm.entrypoints.openai.api_server --port 8000 --model Qwen2-7B-xft --tokenizer /data/configs --trust-remote-code --dtype ${xft_dtype} --kv-cache-dtype ${kv_cache_dtype} )


