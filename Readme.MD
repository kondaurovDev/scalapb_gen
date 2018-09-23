Generate code from your proto files.

### json schema

Generate typescript file with json schemas.

    docker pull kondaurov/scalapb_gen:json_schema
	docker run --rm \
	 -v $(PWD):/project \
	 -e PROTO_DIR=/project/test_protos/src/main/protobuf \
	 -e OUT_DIR=/project/out \
	 kondaurov/scalapb_gen:json_schema