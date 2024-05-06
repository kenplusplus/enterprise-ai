FROM python:3.10-slim as ipex_base

RUN pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cpu
RUN pip install intel-extension-for-pytorch==2.2.0 oneccl_bind_pt --trusted-host ipex-1711374217.us-west-2.elb.amazonaws.com --extra-index-url https://ipex-1711374217.us-west-2.elb.amazonaws.com/release-whl/stable/cpu/us/

FROM ipex_base

RUN apt-get update && apt-get install git -y ffmpeg wget procps
RUN mkdir -p /root/.cache/whisper/
#RUN wget --directory-prefix=/root/.cache/whisper/ https://openaipublic.azureedge.net/main/whisper/models/ed3a0b6b1c0edf879ad9b11b1af5a0e6ab5db9205f891f668f8b0e6c6326e34e/base.pt
RUN wget --directory-prefix=/root/.cache/whisper/ https://openaipublic.azureedge.net/main/whisper/models/9ecf779972d90ba49c06d968637d720dd632c55bbf19d441fb42bf17a411e794/small.pt
#RUN wget --directory-prefix=/root/.cache/whisper/ https://openaipublic.azureedge.net/main/whisper/models/345ae4da62f9b3d59415adc60127b97c714f32e89e936602e85993674d08dcb1/medium.pt

WORKDIR /python-docker

COPY . .

ARG pip_mirror="-i https://mirrors.aliyun.com/pypi/simple/"
RUN pip3 install -r requirements.txt ${pip_mirror}
RUN pip3 install ./whisper/

# Download the pre-trained model

ENV FLASK_APP=app.py
EXPOSE 5000

CMD [ "./start.sh"]
