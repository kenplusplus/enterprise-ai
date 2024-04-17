FROM python:3.10-slim as ipex_base

RUN apt-get update && apt-get install git -y ffmpeg wget
RUN pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cpu
RUN pip install intel-extension-for-pytorch==2.2.0 oneccl_bind_pt \
        --trusted-host ipex-1711374217.us-west-2.elb.amazonaws.com \
        --extra-index-url https://ipex-1711374217.us-west-2.elb.amazonaws.com/release-whl/stable/cpu/us/

# ===

FROM ipex_base as base

RUN mkdir -p ~/.cache/torch/hub/checkpoints/
RUN wget -O ~/.cache/torch/whisperx-vad-segmentation.bin \
        https://whisperx.s3.eu-west-2.amazonaws.com/model_weights/segmentation/0b5b3216d60a2d32fc086b47ea8c67589aaeb26b7e07fcbe620d6d0b83e209ea/pytorch_model.bin
RUN wget -O ~/.cache/torch/hub/checkpoints/wav2vec2_fairseq_large_lv60k_asr_ls960.pth \
        https://download.pytorch.org/torchaudio/models/wav2vec2_fairseq_large_lv60k_asr_ls960.pth
RUN wget -O ~/.cache/torch/hub/checkpoints/wav2vec2_fairseq_base_ls960_asr_ls960.pth \
        https://download.pytorch.org/torchaudio/models/wav2vec2_fairseq_base_ls960_asr_ls960.pth

RUN mkdir -p /root/.cache/huggingface/hub/
ADD .model_cache/models--pyannote--segmentation-3.0 /root/.cache/huggingface/hub/models--pyannote--segmentation-3.0/
ADD .model_cache/models--pyannote--speaker-diarization-3.0 /root/.cache/huggingface/hub/models--pyannote--speaker-diarization-3.0/
ADD .model_cache/models--pyannote--wespeaker-voxceleb-resnet34-LM /root/.cache/huggingface/hub/models--pyannote--wespeaker-voxceleb-resnet34-LM/

ADD .model_cache/models--hbredin--wespeaker-voxceleb-resnet34-LM /root/.cache/huggingface/hub/models--hbredin--wespeaker-voxceleb-resnet34-LM/
ADD .model_cache/models--jonatasgrosman--wav2vec2-large-xlsr-53-chinese-zh-cn /root/.cache/huggingface/hub/models--jonatasgrosman--wav2vec2-large-xlsr-53-chinese-zh-cn/
ADD .model_cache/models--Systran--faster-whisper-small /root/.cache/huggingface/hub/models--Systran--faster-whisper-small/

RUN mkdir -p /root/.cache/torch/pyannote
RUN ln -s /root/.cache/huggingface/hub/models--pyannote--segmentation-3.0 /root/.cache/torch/pyannote/models--pyannote--segmentation-3.0
RUN ln -s /root/.cache/huggingface/hub/models--pyannote--speaker-diarization-3.0 /root/.cache/torch/pyannote/models--pyannote--speaker-diarization-3.0
RUN ln -s /root/.cache/huggingface/hub/models--pyannote--wespeaker-voxceleb-resnet34-LM /root/.cache/torch/pyannote/models--pyannote--wespeaker-voxceleb-resnet34-LM

# ===

FROM base

ARG pip_mirror="-i https://mirrors.aliyun.com/pypi/simple/"

RUN apt-get install procps -y

WORKDIR /app/
COPY . .
RUN pip3 install -r ./requirements.txt
RUN pip3 install ./whisperx/ ${pip_mirror}

EXPOSE 5000

CMD [ "./start.sh"]
