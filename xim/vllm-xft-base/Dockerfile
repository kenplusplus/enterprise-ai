FROM intel/xfastertransformer:latest

ARG XFT_VER="1.7.1"
ARG VLLM_XFT_VER="0.4.2.0"

#Install git
RUN apt -o Acquire::Check-Date=false update && apt-get install git git-lfs -y

#install python packages
RUN pip install xfastertransformer==${XFT_VER}
RUN pip install --no-cache-dir vllm-xft==${VLLM_XFT_VER}  "huggingface_hub[cli]"  modelscope