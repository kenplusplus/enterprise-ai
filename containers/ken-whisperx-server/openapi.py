import whisperx
from typing import Annotated
from fastapi import FastAPI, Form, File
from tempfile import NamedTemporaryFile
import os
from io import BytesIO
import dotenv
import logging
import torch

# Check if NVIDIA GPU is available
torch.cuda.is_available()
DEVICE = "cuda" if torch.cuda.is_available() else "cpu"
compute_type = "int8" # change to "int8" if low on GPU mem (may reduce accuracy)

# Load the Whisper model:
# 1. Transcribe with original whisper (batched)
whisperx_engine = whisperx.load_model("small", device=DEVICE, compute_type=compute_type)

# 3. Assign speaker labels
diarize_model = whisperx.DiarizationPipeline(device=DEVICE)
align_model_en_model, align_model_en_metadata = whisperx.load_align_model('en', device=DEVICE)
align_model_zh_model, align_model_zh_metadata = whisperx.load_align_model('zh', device=DEVICE)

align_models = {
    'en': {
        'model': align_model_en_model,
        'meta': align_model_en_metadata
    },
    'zh': {
        'model': align_model_zh_model,
        'meta': align_model_zh_metadata
    }
}

app = FastAPI()

@app.post("/v1/audio/transcriptions")
def create_transcription(file: Annotated[bytes, File()],
                         model: Annotated[str, Form()] = 'whipser-1',
                         language: Annotated[str | None, Form()] = None,
                         prompt: Annotated[str | None, Form()] = None):
    temp = NamedTemporaryFile()
    temp.write(file)

    print("Step1: whisperx transcribe...")
    audio = whisperx.load_audio(temp.name)
    result = whisperx_engine.transcribe(audio)

    language = result["language"]
    if language not in align_models:
        print("not find the language %s" % language)
    print("Step2: Align with language model %s..." % language)
    result = whisperx.align(result["segments"],
                            align_models[language]['model'],
                            align_models[language]['meta'],
                            audio,
                            DEVICE,
                            return_char_alignments=False)

    # add min/max number of speakers if known
    print("Step3: Diarize the speakers ...")
    diarize_segments = diarize_model(audio)
    # diarize_model(audio, min_speakers=min_speakers, max_speakers=max_speakers)
    print("Step4: Assign workers ...")
    result = whisperx.assign_word_speakers(diarize_segments, result)

    return {
        "text": result['segments'][0]['text'],
        "segments": result['segments']
    }
