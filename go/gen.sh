#!/bin/bash
set -e
out=$1/demo
rm -rf ${out}
mkdir -p ${out}
set -x
protoc \
  --go_out=${out} \
  --go_opt=paths=source_relative \
  --go-grpc_out=${out} \
  --go-grpc_opt=paths=source_relative \
  -I ../java/src/main/proto \
  hello.proto
