FROM python:3.10-slim 

RUN apt update && apt install -y numactl

# install required python packages
RUN pip install modelscope torchaudio onnxruntime funasr_onnx torch_complex omegaconf kaldiio librosa hydra-core funasr jieba fastapi
RUN pip install -U rotary_embedding_torch
RUN pip install numpy==1.26.4

# Download required models
RUN python -c 'from modelscope import snapshot_download; \
              snapshot_download("iic/speech_paraformer-large-vad-punc_asr_nat-zh-cn-16k-common-vocab8404-pytorch")'

RUN python -c 'from funasr_onnx import Paraformer; \
              model_dir = "damo/speech_paraformer-large_asr_nat-zh-cn-16k-common-vocab8404-pytorch"; \
              Paraformer(model_dir, batch_size=1, quantize=True, intra_op_num_threads=1)'

WORKDIR /app/
COPY . .

EXPOSE 5000

CMD [ "./start.sh"]
