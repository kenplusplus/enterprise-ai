#!/bin/sh

# Copyright (C) 2024 Intel Corporation
# SPDX-License-Identifier: Apache-2.0

# convert the model to fastertransformer format

unset http_proxy
unset https_proxy
unset all_proxy
unset HTTP_PROXY
unset HTTPS_PROXY

# serving with vllm
python -m vllm.entrypoints.openai.api_server \
        --model ./Qwen2-7B-Instruct-xft \
        --tokenizer ./Qwen2-7B-Instruct \
        --dtype w8a8 \
        --kv-cache-dtype int8 \
        --served-model-name xft \
        --host 0.0.0.0 \
        --port 18688 \
        --trust-remote-code &

# run llm microservice wrapper
python llm.py
