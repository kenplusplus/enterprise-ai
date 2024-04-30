From ubuntu:22.04
ARG DEBIAN_FRONTEND=noninteractive

COPY ./server-ipex.py /root/

RUN apt update && apt install -y python3 python3-pip 
RUN pip3 install torch==2.2.2 torchvision --index-url https://download.pytorch.org/whl/cpu 
RUN pip3 install intel-extension-for-pytorch==2.2.0
RUN pip3 install transformers huggingface-cli 
RUN pip3 install llmspec mosec

RUN cd /root/ && export HF_ENDPOINT=https://hf-mirror.com && huggingface-cli download --resume-download BAAI/bge-large-zh --local-dir /root/bge-large-zh

ENV EMB_MODEL="/root/bge-large-zh/"
CMD ["python3", "/root/server-ipex.py"]
