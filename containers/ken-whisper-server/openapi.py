from typing import Annotated
from fastapi import FastAPI, Form, File
import whisper
from tempfile import NamedTemporaryFile
import os
from io import BytesIO
import dotenv
import logging
import torch

# Check if NVIDIA GPU is available
torch.cuda.is_available()
DEVICE = "cuda" if torch.cuda.is_available() else "cpu"

# Load the Whisper model:
whisper_engine = whisper.load_model("small", device=DEVICE)

app = FastAPI()

@app.post("/v1/audio/transcriptions")
def create_transcription(file: Annotated[bytes, File()],
                         model: Annotated[str, Form()] = 'whipser-1',
                         language: Annotated[str | None, Form()] = None,
                         prompt: Annotated[str | None, Form()] = None):
    temp = NamedTemporaryFile()
    temp.write(file)
    result = whisper_engine.transcribe(temp.name)
    return {
        "text": result['text']
    }
