#!/usr/bin/env sh

set -e

: "${PROTO_DIR?Need to set PROTO_DIR}"
: "${OUT_DIR?Need to set OUT_DIR}"

echo "start generating scala code"
echo "input dir: ${PROTO_DIR}"
echo "out dir: ${OUT_DIR}"

files=`find ${PROTO_DIR} -name "*.proto" | paste -sd " " -`

/bin/bash /root/scalapbc/bin/scalapbc -v360 -I=${PROTO_DIR} --json-schema_out=${SCALA_OUT} ${files}