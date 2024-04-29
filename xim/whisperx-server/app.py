import whisperx
import torch
from flask import Flask, abort, request
from tempfile import NamedTemporaryFile

#device = "cuda"
batch_size = 16 # reduce if low on GPU mem
compute_type = "int8" # change to "int8" if low on GPU mem (may reduce accuracy)

# Check if NVIDIA GPU is available
torch.cuda.is_available()
DEVICE = "cuda" if torch.cuda.is_available() else "cpu"

# 1. Transcribe with original whisper (batched)
model = whisperx.load_model("small", device=DEVICE, compute_type=compute_type)
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

app = Flask(__name__)

@app.route("/")
def hello():
    return "Whisper Hello World!"

@app.route('/whisperx', methods=['POST'])
def handler():
    if not request.files:
        # If the user didn't submit any files, return a 400 (Bad Request) error.
        abort(400)

    # For each file, let's store the results in a list of dictionaries.
    final_results = []

    # Loop over every file that the user submitted.
    for filename, handle in request.files.items():
        temp = NamedTemporaryFile()
        handle.save(temp)

        audio = whisperx.load_audio(temp.name)
        result = model.transcribe(audio, batch_size=batch_size)
        print(result["segments"]) # before alignment

        # 2. Align whisper output
        language = result["language"]
        if language not in align_models:
            print("not find the language %s" % language)

        print("========> Language found: %s", language)

        result = whisperx.align(result["segments"],
                                align_models[language]['model'],
                                align_models[language]['meta'],
                                audio,
                                DEVICE,
                                return_char_alignments=False)
        print("========> After align")

        # add min/max number of speakers if known
        diarize_segments = diarize_model(audio)
        # diarize_model(audio, min_speakers=min_speakers, max_speakers=max_speakers)
        print("========> After diarize_model")
        result = whisperx.assign_word_speakers(diarize_segments, result)
        print(diarize_segments)
        print(result.keys())
        print(result['word_segments'])
        final_results.append({
            'filename': filename,
            'segments': result["segments"]
        })

    return {'results': final_results}
