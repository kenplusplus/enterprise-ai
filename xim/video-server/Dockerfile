From ubuntu:20.04
ARG DEBIAN_FRONTEND=noninteractive

RUN apt update && apt install -y python3 python3-pip wget fontconfig fonts-noto 
RUN pip3 install flask uuid

ENV HTTP_PROXY=http://child-prc.intel.com:913
ENV HTTPS_PROXY=http://child-prc.intel.com:913
ENV NO_PROXY=intel.com,.intel.com,localhost,127.0.0.1

COPY ./video_server.py /root/
COPY ./ffmpeg /root/
RUN chmod a+x /root/ffmpeg
COPY ./ffmpeg-lib /root/
RUN mkdir -p /root/sample
COPY ./sample /root/sample/

ENV  LD_LIBRARY_PATH=/root/ffmpeg-lib/:$LD_LIBRARY_PATH 
WORKDIR /root/

CMD ["python3", "/root/video_server.py"] 
