FROM python:3.10-slim as model_cache

RUN apt-get update && apt-get install git -y ffmpeg wget

RUN mkdir -p ~/.cache/whisper/ && \
        wget --directory-prefix=~/.cache/whisper/ https://openaipublic.azureedge.net/main/whisper/models/ed3a0b6b1c0edf879ad9b11b1af5a0e6ab5db9205f891f668f8b0e6c6326e34e/base.pt && \
        wget --directory-prefix=~/.cache/whisper/ https://openaipublic.azureedge.net/main/whisper/models/9ecf779972d90ba49c06d968637d720dd632c55bbf19d441fb42bf17a411e794/small.pt && \
        wget --directory-prefix=~/.cache/whisper/ https://openaipublic.azureedge.net/main/whisper/models/345ae4da62f9b3d59415adc60127b97c714f32e89e936602e85993674d08dcb1/medium.pt

FROM model_cache as ipex_base

RUN pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cpu
RUN pip install intel-extension-for-pytorch==2.2.0 oneccl_bind_pt --trusted-host ipex-1711374217.us-west-2.elb.amazonaws.com --extra-index-url https://ipex-1711374217.us-west-2.elb.amazonaws.com/release-whl/stable/cpu/us/

FROM ipex_base

WORKDIR /python-docker

COPY . .

ENV PIP_MIRROR=""
RUN pip3 install -r requirements.txt ${PIP_MIRROR}
RUN pip3 install ./whisper/

RUN apt-get install procps -y

# Download the pre-trained model

ENV FLASK_APP=app.py
EXPOSE 5000

CMD [ "./start.sh"]
