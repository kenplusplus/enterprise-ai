FROM ipex-llm:2.2.0

# You can follow the official step to create ipex-llm:2.2.0 container via following
# steps:
#
# cd ${CURR_DIR}/intel-extension-for-pytorch/
# git checkout v2.2.0+cpu
# git submodule sync
# git submodule update --init --recursive
# DOCKER_BUILDKIT=1 docker build -f examples/cpu/inference/python/llm/Dockerfile -t ipex-llm:2.2.0 .
#

USER ubuntu

ADD .condarc /home/ubuntu/miniconda3/.condarc
RUN /home/ubuntu/miniconda3/bin/conda init bash
COPY --chown=ubuntu:ubuntu fastchat/ /home/ubuntu/fastchat
COPY --chown=ubuntu:ubuntu pip.conf /home/ubuntu/.pip/pip.conf
COPY --chown=ubuntu:ubuntu start-chat.sh /home/ubuntu/start-chat.sh

RUN /home/ubuntu/miniconda3/envs/py310/bin/python -m pip install -e /home/ubuntu/fastchat/".[model_worker,webui]"
RUN /home/ubuntu/miniconda3/envs/py310/bin/python -m pip install plotly prometheus-client

RUN echo "/home/ubuntu/miniconda3/bin/conda init bash" >> ./.bashrc
RUN echo "/home/ubuntu/miniconda3/bin/conda activate py310" >> ./.bashrc
