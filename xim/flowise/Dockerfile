From ubuntu:22.04
ARG DEBIAN_FRONTEND=noninteractive

ARG XFLOWISE_REPO="https://github.com/intel-sandbox/xflowise"
ARG XFLOWISE_VER="a0415212e53ca880c3512b49d9165df23337d8d5"

RUN apt update
RUN apt install -y git nodejs curl build-essential cmake wget

RUN cd /root && git clone https://github.com/oneapi-src/oneDNN.git \
    && cd oneDNN && mkdir build && cd build && cmake -DCMAKE_INSTALL_PREFIX=/usr/ .. && make install -j

RUN cd /root && wget https://repo.anaconda.com/miniconda/Miniconda3-py310_23.5.2-0-Linux-x86_64.sh \
    && chmod a+x Miniconda3-py310_23.5.2-0-Linux-x86_64.sh \
    && bash ./Miniconda3-py310_23.5.2-0-Linux-x86_64.sh -b
RUN export PATH="/root/miniconda3/bin:$PATH" && conda install -c pytorch mkl=2023 blas=1.0=mkl

ENV LD_LIBRARY_PATH="$LD_LIBRARY_PATH:/root/miniconda3/lib/"
ENV PATH="/root/.nvm/versions/node/v21.7.2/bin:$PATH"

WORKDIR /root

RUN --mount=type=secret,id=.netrc,dst=/root/.netrc \
    git clone ${XFLOWISE_REPO} Flowise && \
    cd Flowise && \
    git reset --hard ${XFLOWISE_VER}

RUN cd /root/Flowise && curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash \
    && export NVM_DIR="$HOME/.nvm" && [ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh" \
    && nvm install 21.7.2 && nvm use 21 \
    && npm i -g pnpm && pnpm install && pnpm build

WORKDIR /root/Flowise/

CMD [ "pnpm", "start" ]
