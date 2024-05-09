From ubuntu:22.04
ARG DEBIAN_FRONTEND=noninteractive

#COPY ./server-ipex.py /root/

RUN cd /root
RUN apt update && apt install -y git nodejs curl build-essential cmake wget
RUN cd /root && git clone https://github.com/FlowiseAI/Flowise.git \
    && cd Flowise && git checkout 43b22476e30ea0831a01d6a93febd5678772a93a
COPY ./xFlowise_patch0508.patch /root/Flowise/
RUN cd /root/Flowise/ && git apply ./xFlowise_patch0508.patch && mkdir -p ./packages/components/nodes/vectorstores/xFaiss/langchain/node/
COPY ./xfaiss-node.node /root/Flowise/packages/components/nodes/vectorstores/xFaiss/langchain/node/
RUN cd /root/Flowise && curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash \
    && export NVM_DIR="$HOME/.nvm" && [ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh" \
    && nvm install 21.7.2 && nvm use 21 \
    && npm i -g pnpm && pnpm install && pnpm build

RUN cd /root && git clone https://github.com/oneapi-src/oneDNN.git \
    && cd oneDNN && mkdir build && cd build && cmake -DCMAKE_INSTALL_PREFIX=/usr/ .. && make install -j

RUN cd /root && wget https://repo.anaconda.com/miniconda/Miniconda3-py310_23.5.2-0-Linux-x86_64.sh \
    && chmod a+x Miniconda3-py310_23.5.2-0-Linux-x86_64.sh \
    && bash ./Miniconda3-py310_23.5.2-0-Linux-x86_64.sh -b
RUN export PATH="/root/miniconda3/bin:$PATH" && conda install -c pytorch mkl=2023 blas=1.0=mkl

ENV LD_LIBRARY_PATH="$LD_LIBRARY_PATH:/root/miniconda3/lib/"
ENV PATH="/root/.nvm/versions/node/v21.7.2/bin:$PATH"
WORKDIR /root/Flowise/

CMD [ "pnpm", "start" ]
