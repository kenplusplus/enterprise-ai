From ubuntu:20.04
ARG DEBIAN_FRONTEND=noninteractive

RUN apt update && apt install -y python3 python3-pip
RUN pip3 install flask uuid

COPY ./video_server.py /root/
COPY ./ffmpeg /root/
RUN chmod a+x /root/ffmpeg
COPY ./ffmpeg-lib /root/
RUN mkdir -p /root/sample
COPY ./sample /root/sample/

ENV  LD_LIBRARY_PATH=/root/ffmpeg-lib/:$LD_LIBRARY_PATH 
WORKDIR /root/

CMD ["python3", "/root/video_server.py"] 
