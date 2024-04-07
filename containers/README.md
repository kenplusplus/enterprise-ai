# Build Container

```shell
# Default build script
./containers/build.sh

# Only build whisper server, without push to registry
./containers/build.sh -c ken-whisper-server -a build

# Only build TTS server, without push to registry
./containers/build.sh -c ken-opentts-server -a build
```

The complete instruction is:

```shell
usage: build.sh [OPTION]...
    -a <build|download|publish|save|all>  all is default, which not include save. Please execute save explicity if need.
    -r <registry prefix> the prefix string for registry
    -c <container name> same as directory name
    -g <tag> container image tag
    -f Clean build
```
