FROM intel/xfastertransformer:latest

#Install git
RUN apt -o Acquire::Check-Date=false update && apt-get install -y git

#install python packages
RUN pip install --no-cache-dir vllm-xft  "huggingface_hub[cli]"