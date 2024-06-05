from funasr_onnx import Paraformer
import time
import os

home_path=os.path.expanduser('~')

model_dir = os.path.join(home_path,".cache/modelscope/hub/damo/speech_paraformer-large_asr_nat-zh-cn-16k-common-vocab8404-pytorch")

model = Paraformer(model_dir, batch_size=1, quantize=True, intra_op_num_threads=1,local_files_only=True )

wav_path = ['asr_example_en.wav']

result = model(wav_path)
st=time.time()
for _ in range(100):
    result = model(wav_path)
et=time.time()
print("time is ",(et-st)/100)
print(result)