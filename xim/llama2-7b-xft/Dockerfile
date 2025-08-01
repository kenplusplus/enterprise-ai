FROM ccr-registry.caas.intel.com/opea-xim/vllm-xft-base:latest AS build

ARG HF_ENDPOINT
ARG HF_TOKEN

# Set the environment variable
ENV HF_TOKEN=$HF_TOKEN
ENV HF_ENDPOINT=$HF_ENDPOINT

# Prepare download model
RUN git config --global credential.helper store
RUN echo /usr/bin/python3.8/bin/huggingface-cli login --token $HF_TOKEN --add-to-git-credential

# Download model
RUN mkdir -p /data/Llama-2-7b-chat-hf
RUN /usr/bin/python3.8/bin/huggingface-cli download --resume-download --local-dir-use-symlinks False  meta-llama/Llama-2-7b-chat-hf --local-dir /data/Llama-2-7b-chat-hf

# Convert model
RUN python -c 'import xfastertransformer as xft; xft.LlamaConvert().convert("/data/Llama-2-7b-chat-hf","/data/Llama-2-7b-chat-hf-xft")'

# Cleanup
RUN mkdir /data/configs  &&  \
    cp /data/Llama-2-7b-chat-hf/*.json /data/configs
RUN rm /root/.cache/huggingface/ /data/Llama-2-7b-chat-hf/ -fr


# Copy files to target image
FROM ccr-registry.caas.intel.com/opea-xim/vllm-xft-base:latest

COPY --from=build /data /data

ARG xft_dtype="bf16"
ARG kv_cache_dtype="fp16"

ENV xft_dtype=${xft_dtype} \
    kv_cache_dtype=${kv_cache_dtype}

EXPOSE 8000

WORKDIR /data

CMD ( python -m vllm.entrypoints.openai.api_server --port 8000 --model Llama-2-7b-chat-hf-xft --tokenizer /data/configs/ --trust-remote-code --dtype ${xft_dtype} --kv-cache-dtype ${kv_cache_dtype} )

