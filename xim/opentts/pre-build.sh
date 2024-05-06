#!/bin/bash

current_directory=$(readlink -f "$(dirname "${BASH_SOURCE[0]}")")


pushd ${current_directory}/opentts
make all
popd
