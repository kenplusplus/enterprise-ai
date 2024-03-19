FROM python:3.10-slim

WORKDIR /python-docker

RUN apt-get update && apt-get install git -y ffmpeg wget

COPY requirements.txt requirements.txt
RUN pip3 install -r requirements.txt
RUN pip3 install "git+https://github.com/openai/whisper.git"

COPY . .
RUN mkdir -p ~/.cache/whisper/ && \
        wget --directory-prefix=~/.cache/whisper/ https://openaipublic.azureedge.net/main/whisper/models/ed3a0b6b1c0edf879ad9b11b1af5a0e6ab5db9205f891f668f8b0e6c6326e34e/base.pt

ENV FLASK_APP=app.py
EXPOSE 5000

CMD [ "python3", "-m" , "flask", "run", "--host=0.0.0.0"]
