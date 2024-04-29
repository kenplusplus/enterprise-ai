#!/bin/bash

set -ex

SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"
MODEL_CACHE="${MODEL_CACHE:="${SCRIPT_DIR}/.model_cache"}"

DOWNLOAD_PARAMS="--resume-download --cache-dir ${MODEL_CACHE} --local-dir-use-symlinks False --repo-type model"

mkdir -p ${MODEL_CACHE}

huggingface-cli login --token ${hf_token}
huggingface-cli download ${DOWNLOAD_PARAMS} pyannote/speaker-diarization-3.0
huggingface-cli download ${DOWNLOAD_PARAMS} hbredin/wespeaker-voxceleb-resnet34-LM
huggingface-cli download ${DOWNLOAD_PARAMS} pyannote/segmentation-3.0
export HF_ENDPOINT="https://hf-mirror.com"
huggingface-cli download ${DOWNLOAD_PARAMS} jonatasgrosman/wav2vec2-large-xlsr-53-chinese-zh-cn
huggingface-cli download ${DOWNLOAD_PARAMS} Systran/faster-whisper-small
huggingface-cli download ${DOWNLOAD_PARAMS} pyannote/wespeaker-voxceleb-resnet34-LM

unset HF_ENDPOINT
