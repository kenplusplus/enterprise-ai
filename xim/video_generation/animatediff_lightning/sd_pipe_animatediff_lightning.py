import torch
import argparse
import time
import psutil
import os
from diffusers import AnimateDiffPipeline, MotionAdapter, EulerDiscreteScheduler
from diffusers.utils import export_to_gif, export_to_video
from safetensors.torch import load_file
from .pipeline_animatediff_ipex import AnimateDiffPipelineIpex

device = "cpu"
dtype = torch.float32
prompt_example = "beautiful island of Bali, the shing sun, the beach with light wave"


class AnimatedDiffLightning:
    def __init__(self, step = 4, num_frames = 16, adapter_path = "/home/models/AnimateDiff-Lightning/", base_model_path = "/home/models/epiCRealism", fps = 8, dtype = torch.float32, device = "cpu"):
        self.step = step
        self.num_frames = num_frames
        self.adapter_path = adapter_path
        self.base_model_path = base_model_path
        self.ckpt = f"animatediff_lightning_{step}step_diffusers.safetensors"
        self.fps = fps
        self.dtype = dtype
        self.device = device
        self.video_dir = os.environ.get('VIDEO_DIR')
        
        
        self.height = 512
        self.width = 512
        
        self.adapter = MotionAdapter().to(self.device, self.dtype)            
        print(self.adapter_path)
        self.adapter.load_state_dict(load_file(self.adapter_path + self.ckpt, device=self.device))
        
        self.pipe = AnimateDiffPipelineIpex.from_pretrained(self.base_model_path, motion_adapter=self.adapter, torch_dtype=self.dtype).to(self.device)
        self.pipe.scheduler = EulerDiscreteScheduler.from_config(self.pipe.scheduler.config, timestep_spacing="trailing", beta_schedule="linear")

        self.pipe.prepare_for_ipex(self.dtype, prompt=prompt_example, guidance_scale=1.0, num_inference_steps=self.step, num_frames=self.num_frames, height=self.height, width=self.width)

    def generate(self, label = "shot1", prompt = prompt_example):
        with torch.no_grad(), torch.cpu.amp.autocast(enabled = True if (self.dtype == torch.bfloat16) else False, dtype = self.dtype):
            
            output = self.pipe(prompt=prompt, guidance_scale=1.0, num_inference_steps=self.step, num_frames=self.num_frames, height=self.height, width=self.width) 
            
            print("pipe generate done!")
            
            filename = f"{label}_fps{self.fps}.mp4"
            
            print(self.video_dir, filename)
            
            export_to_video(output.frames[0], os.path.join(self.video_dir, filename), fps = self.fps)
            
            print("pipe export video done!")
            
            return filename
            

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--bf16', default=False, action='store_true', help="FP32 - Default")
    parser.add_argument("--step", default=4, type=int, choices=[1, 2, 4, 8], help="Options is [1,2,4,8]")
    parser.add_argument("--num_frames", default=16, type=int, help="num of frames for the generated video")
    parser.add_argument("--fps", default=8, type=int, help="fps for the generated video")
    parser.add_argument("--prompt", default="beautiful island of Bali, the shing sun, the beach with light wave", type=str, help="Input Prompt")

    args = parser.parse_args()
    
    adapter_path = "/home/models/AnimateDiff-Lightning/"
    base_model_path = "/home/models/epiCRealism"
    
    gen_pipe = AnimatedDiffLightning(step = args.step, num_frames = args.num_frames, adapter_path = adapter_path, base_model_path = base_model_path, fps = args.fps, dtype = torch.bfloat16, device = "cpu")
    
    filename = gen_pipe.generate()
    
    '''    
    step = args.step  # Options: [1,2,4,8]
    num_frames = args.num_frames
    adapter_path = "/home/models/AnimateDiff-Lightning/"
    ckpt = f"animatediff_lightning_{step}step_diffusers.safetensors"
    # Choose to your favorite base model.
    base_model_path = "/home/models/epiCRealism"
    
    adapter = MotionAdapter().to(device, dtype)
    adapter.load_state_dict(load_file(adapter_path+ckpt, device=device))
    # pipe = AnimateDiffPipeline.from_pretrained(base_model_path, motion_adapter=adapter, torch_dtype=dtype).to(device)
    pipe = AnimateDiffPipelineIpex.from_pretrained(base_model_path, motion_adapter=adapter, torch_dtype=dtype).to(device)
    pipe.scheduler = EulerDiscreteScheduler.from_config(pipe.scheduler.config, timestep_spacing="trailing", beta_schedule="linear")
    
    data_type = torch.bfloat16 if args.bf16 else torch.float32

    pipe.prepare_for_ipex(data_type, prompt=args.prompt, guidance_scale=1.0, num_inference_steps=step, num_frames=num_frames, height=512, width=512)
    
    # generator = torch.Generator(device).manual_seed(4)
    with torch.no_grad(), torch.cpu.amp.autocast(enabled=args.bf16, dtype=torch.bfloat16):
        t1 = time.time()
        output = pipe(prompt=args.prompt, guidance_scale=1.0, num_inference_steps=step, num_frames=num_frames, height=512, width=512) #generator=generator
        t2 = time.time()
        print('animatediff inference latency: {:.3f} sec'.format(t2-t1))

        post_fix = "bf16" if args.bf16 else "fp32"
        #export_to_gif(output.frames[0], "animation_"+post_fix+"_"+str(step)+"step_"+str(num_frames)+"frames_"+str(args.fps)+"fps.gif", fps=args.fps)
        export_to_video(output.frames[0], "animation_"+post_fix+"_"+str(step)+"step_"+str(num_frames)+"frames_"+str(args.fps)+"fps.mp4", fps=args.fps)
        print("video saved successfully.")
    '''
    
