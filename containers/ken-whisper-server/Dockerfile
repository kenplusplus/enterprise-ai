FROM python:3.10-slim

WORKDIR /python-docker

RUN apt-get update && apt-get install git -y ffmpeg wget

COPY . .
RUN pip3 install -r requirements.txt -i https://mirrors.bfsu.edu.cn/pypi/web/simple/
RUN pip3 install ./whisper/

# Download the pre-trained model
RUN mkdir -p ~/.cache/whisper/ && \
        wget --directory-prefix=~/.cache/whisper/ https://openaipublic.azureedge.net/main/whisper/models/ed3a0b6b1c0edf879ad9b11b1af5a0e6ab5db9205f891f668f8b0e6c6326e34e/base.pt && \
        wget --directory-prefix=~/.cache/whisper/ https://openaipublic.azureedge.net/main/whisper/models/9ecf779972d90ba49c06d968637d720dd632c55bbf19d441fb42bf17a411e794/small.pt && \
        wget --directory-prefix=~/.cache/whisper/ https://openaipublic.azureedge.net/main/whisper/models/345ae4da62f9b3d59415adc60127b97c714f32e89e936602e85993674d08dcb1/medium.pt

ENV FLASK_APP=app.py
EXPOSE 5000

CMD [ "python3", "-m" , "flask", "run", "--host=0.0.0.0"]
