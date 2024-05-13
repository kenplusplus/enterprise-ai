#!/bin/bash

set -ex

function ctrl_c() {
    echo "exit..."
    exit 0
}
# trap ctrl-c and call ctrl_c()
trap ctrl_c INT


uvicorn video_gen_api:app --host 0.0.0.0 --port 5000