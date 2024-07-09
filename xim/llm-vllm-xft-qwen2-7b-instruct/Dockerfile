FROM opea/llm-vllm-xft:0.0.3

COPY requirements.txt /root/vllm-xft/requirements.txt
RUN pip install --no-cache-dir --upgrade pip && \
    pip install --no-cache-dir -r /root/vllm-xft/requirements.txt

COPY Qwen2-7B-Instruct /root/vllm-xft/Qwen2-7B-Instruct
COPY Qwen2-7B-Instruct-xft /root/vllm-xft/Qwen2-7B-Instruct-xft
COPY comps /root/vllm-xft/comps
COPY llm.py run.sh /root/vllm-xft/

RUN chmod +x /root/vllm-xft/run.sh

WORKDIR /root/vllm-xft/
RUN pip install ray
ENTRYPOINT ["/root/vllm-xft/run.sh"]

