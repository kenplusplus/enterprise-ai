from fastapi import FastAPI, HTTPException, Request
from fastapi.responses import FileResponse, JSONResponse
import os

from moviepy.editor import VideoFileClip, CompositeVideoClip, TextClip, ColorClip,ImageClip
from urllib.parse import quote
from pydantic import BaseModel

from animatediff_lightning import AnimatedDiffLightning


# create video directory
VIDEO_DIR = "videos"
os.environ['VIDEO_DIR'] = VIDEO_DIR

print(os.environ['VIDEO_DIR'])

os.makedirs(VIDEO_DIR, exist_ok=True)


class VideoCreate(BaseModel):
    label : str   #unique name, will be used a video file name
    text : str
    duration: int = 5

class ModelParam(BaseModel):
    step: int = 4
    num_frames: int = 16
    adapter_path: str = "/home/models/AnimateDiff-Lightning/"
    base_model_path: str = "/home/models/epiCRealism"
    fps: int = 8
    dtype: str = "bfloa16"
    device: str = "cpu"

def get_torch_dtype(dtype):
    import torch
    if dtype == "bfloa16":
        return torch.bfloat16
    elif dtype == "float32":
        return torch.float32
    else:
        raise ValueError("not support data type")
        

app = FastAPI()

default_param = ModelParam()


#duraion = num_frames / fps
AnimatedDiffLightningPipe = AnimatedDiffLightning( step = default_param.step,
                                                num_frames = default_param.num_frames,
                                                adapter_path = default_param.adapter_path,
                                                base_model_path = default_param.base_model_path,
                                                fps = default_param.fps,
                                                dtype = get_torch_dtype(default_param.dtype),
                                                device = default_param.device)




# test gen func with moviepy
def generate_video(label: str, text: str, duration: int = 5, size: int = 480):
    width, height = size, size  
    fill_color = (255, 255, 255)  # background
    text_color = (0, 0, 0)  # text
    text_size = 24  # font size

    # white background
    background = ColorClip(size=(width, height), color=fill_color).set_duration(duration)
    
    # text 
    text_clip = TextClip(
        text,
        fontsize=text_size,
        color='white',
        bg_color='black',
        size=(width, height),
        font='Arial-Bold'
    )
    text_clip = text_clip.set_position('center').set_duration(duration)
    

    final_clip = CompositeVideoClip([background, text_clip]).set_fps(24)

    # 生成视频文件名
    #video_filename = f"{hash(text)}.mp4"
    video_filename = f"{label}.mp4"
    
    # 保存视频文件
    final_clip.write_videofile(os.path.join(VIDEO_DIR, video_filename), codec='libx264')
    return video_filename

@app.post("/create_video/")
#async def create_video(request: Request, text: str, duration: int = 5):
async def create_video(request: Request, video_data: VideoCreate):
    
    print(f"receive a post request {video_data.text} , {video_data.duration}")
    try:
        #video_filename = generate_video(label = video_data.label, text = video_data.text, duration = video_data.duration)
        
        
        video_filename = AnimatedDiffLightningPipe.generate(label=video_data.label, prompt=video_data.text)
        
        print("finish gen!")
        #server_host = os.environ.get('SERVER_HOST', 'localhost')
       
        server_host = request.headers.get("host", "localhost")
        
        print(server_host)
        
        server_port = os.environ.get('SERVER_PORT', 8000)
        
        #video_url = f"http://{server_host}:{server_port}/download_video/{quote(video_filename)}"
        video_url = f"http://{server_host}/download_video/{quote(video_filename)}"
         
        return JSONResponse(content={"label":video_data.label, "download_url": video_url})
    
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/download_video/{video_filename}")
async def download_video(video_filename: str):
    video_path = os.path.join(VIDEO_DIR, video_filename)
    if os.path.exists(video_path):
        print(f"response download request for {video_filename}")
        return FileResponse(video_path)
    else:
        raise HTTPException(status_code=404, detail="Video not found")
