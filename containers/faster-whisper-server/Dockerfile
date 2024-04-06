FROM python:3.10-slim as ipex_base

RUN apt-get update && apt-get install git -y ffmpeg wget
RUN pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cpu
RUN pip install intel-extension-for-pytorch==2.2.0 oneccl_bind_pt \
        --trusted-host ipex-1711374217.us-west-2.elb.amazonaws.com \
        --extra-index-url https://ipex-1711374217.us-west-2.elb.amazonaws.com/release-whl/stable/cpu/us/

# ===

FROM ipex_base as base

ARG hf_token=""
ARG pip_mirror="-i https://mirrors.aliyun.com/pypi/simple/"

RUN pip install huggingface_hub ${pip_mirror}
#RUN huggingface-cli login --token ${hf_token}

#RUN huggingface-cli download --resume-download --repo-type model pyannote/speaker-diarization-3.1
#RUN huggingface-cli download --resume-download --repo-type model pyannote/segmentation-3.0

#RUN huggingface-cli download --resume-download --repo-type model jonatasgrosman/wav2vec2-large-xlsr-53-chinese-zh-cn
#RUN huggingface-cli download --resume-download --repo-type model Systran/faster-whisper-large-v3
#RUN huggingface-cli download --resume-download --repo-type model Systran/faster-whisper-base
ENV HF_ENDPOINT="https://hf-mirror.com"
RUN huggingface-cli download --resume-download --repo-type model Systran/faster-whisper-tiny
#RUN huggingface-cli download --resume-download --repo-type model pyannote/wespeaker-voxceleb-resnet34-LM
#RUN mkdir -p /root/.cache/torch/pyannote
#RUN ln -s /root/.cache/huggingface/hub/models--pyannote--segmentation-3.0 /root/.cache/torch/pyannote/models--pyannote--segmentation-3.0
#RUN ln -s /root/.cache/huggingface/hub/models--pyannote--speaker-diarization-3.1 /root/.cache/torch/pyannote/models--pyannote--speaker-diarization-3.1
#RUN ln -s /root/.cache/huggingface/hub/models--pyannote--wespeaker-voxceleb-resnet34-LM /root/.cache/torch/pyannote/models--pyannote--wespeaker-voxceleb-resnet34-LM

#RUN mkdir -p ~/.cache/torch/hub/checkpoints/
#RUN wget -O ~/.cache/torch/whisperx-vad-segmentation.bin \
#        https://whisperx.s3.eu-west-2.amazonaws.com/model_weights/segmentation/0b5b3216d60a2d32fc086b47ea8c67589aaeb26b7e07fcbe620d6d0b83e209ea/pytorch_model.bin
#RUN wget -O ~/.cache/torch/hub/checkpoints/wav2vec2_fairseq_large_lv60k_asr_ls960.pth \
#        https://download.pytorch.org/torchaudio/models/wav2vec2_fairseq_large_lv60k_asr_ls960.pth
#RUN wget -O ~/.cache/torch/hub/checkpoints/wav2vec2_fairseq_base_ls960_asr_ls960.pth \
#        https://download.pytorch.org/torchaudio/models/wav2vec2_fairseq_base_ls960_asr_ls960.pth

# ===

FROM base

RUN apt-get install procps -y

WORKDIR /app/
COPY . .
RUN pip3 install -r ./faster-whisper/requirements.openaiapi.txt ${pip_mirror}
RUN pip3 install ./faster-whisper/ ${pip_mirror}


ENV COMPUTE_TYPE="int8"
ENV DEVICE_TYPE="cpu"
ENV MODEL_SIZE="tiny"
EXPOSE 5000

CMD [ "./start.sh"]
