FROM python:3.10-slim as ipex_base

RUN apt-get update && apt-get install git -y ffmpeg wget procps unzip

#RUN pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cpu
#RUN pip install intel-extension-for-pytorch==2.2.0 oneccl_bind_pt --trusted-host ipex-1711374217.us-west-2.elb.amazonaws.com --extra-index-url https://ipex-1711374217.us-west-2.elb.amazonaws.com/release-whl/stable/cpu/us/
RUN wget -O /checkpoints.zip https://myshell-public-repo-hosting.s3.amazonaws.com/checkpoints_1226.zip

FROM ipex_base

RUN mkdir -p /root/.cache/torch/hub/ && \
        cd /root/.cache/torch/hub/ && \
        wget -O snakers4_silero-vad_master.zip https://github.com/snakers4/silero-vad/zipball/master && \
        unzip snakers4_silero-vad_master.zip && \
        mv snakers4-silero-vad-*/ snakers4-silero-vad-master/

WORKDIR /app

COPY . .
ARG pip_mirror=""
RUN pip3 install -r ./OpenVoice/requirements.txt

RUN mv /checkpoints.zip ./OpenVoice/ && cd ./OpenVoice && unzip -o checkpoints.zip


EXPOSE 5500

CMD [ "./start.sh"]
