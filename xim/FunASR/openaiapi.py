from typing import Annotated
from fastapi import FastAPI, Form, File

from funasr_onnx import Paraformer
import time
import numpy as np
from tempfile import NamedTemporaryFile

import os
from io import BytesIO
import dotenv
import logging

LOGGER = logging.getLogger(__name__)

home_path=os.path.expanduser('~')
model_dir = os.path.join(home_path,".cache/modelscope/hub/damo/speech_paraformer-large_asr_nat-zh-cn-16k-common-vocab8404-pytorch")
funasr_model = Paraformer(model_dir, batch_size=1, quantize=True, intra_op_num_threads=1,local_files_only=True )

app = FastAPI()

@app.post("/v1/audio/transcriptions")
def create_transcription(file: Annotated[bytes, File()],
                         model: Annotated[str, Form()] = 'funasr',
                         language: Annotated[str | None, Form()] = None,
                         prompt: Annotated[str | None, Form()] = None):
    temp = NamedTemporaryFile()
    temp.write(file)
    result = funasr_model([temp.name])

    return {
        "text": "".join(result[0]['preds'][0])
    }
