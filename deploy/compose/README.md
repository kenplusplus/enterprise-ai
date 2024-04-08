# Docker Compose

## Quick Start

1. Download model and put into the directory `./model`. For example:

```shell
huggingface-cli download --resume-download --local-dir ./model --local-dir-use-symlinks False THUDM/ChatGLM2-6B
```

2. Start services

```shell
docker-compose -f ./chatbot.yml up
```
