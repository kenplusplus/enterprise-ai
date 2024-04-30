FROM gar-registry.caas.intel.com/cpio/vllm-xft-base:latest AS build
ARG HF_ENDPOINT
ARG HF_TOKEN

# Set the environment variable
ENV HF_TOKEN=$HF_TOKEN
ENV HF_ENDPOINT=$HF_ENDPOINT

# Prepare download model
RUN git config --global credential.helper store
RUN echo /usr/bin/python3.8/bin/huggingface-cli login --token $HF_TOKEN --add-to-git-credential

# Download model
RUN mkdir -p /data/Qwen-7B-Chat
RUN /usr/bin/python3.8/bin/huggingface-cli download --resume-download --local-dir-use-symlinks False  Qwen/Qwen-7B-Chat --local-dir /data/Qwen-7B-Chat

# Convert model
RUN python -c 'import xfastertransformer as xft; xft.QwenConvert().convert("/data/Qwen-7B-Chat","/data/Qwen-7B-Chat-xft")'

# Cleanup
RUN mkdir /data/configs  &&  \
    cp /data/Qwen-7B-Chat/*.json /data/configs/ && \
    cp /data/Qwen-7B-Chat/*.py /data/configs/ && \
    cp /data/Qwen-7B-Chat/*.tiktoken /data/configs/
RUN rm /root/.cache/huggingface/ /data/Qwen-7B-Chat/ -fr


# Copy files to target image
FROM gar-registry.caas.intel.com/cpio/vllm-xft-base:latest

COPY --from=build /data /data

ARG port=8000
ENV port=${port}


CMD ( python -m vllm.entrypoints.openai.api_server --model /data/Qwen-7B-Chat-xft/ --port ${port} --tokenizer /data/configs/ --trust-remote-code --xft_dtype "bf16")

