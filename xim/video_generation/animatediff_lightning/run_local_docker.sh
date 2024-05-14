PROMPT="beautiful island of Bali, the shing sun, the beach with light wave"

numactl --localalloc --physcpubind=0-47 python sd_pipe_animatediff_lightning.py --step 8 --num_frames 16 --fps 8 --bf16 --prompt "$PROMPT"