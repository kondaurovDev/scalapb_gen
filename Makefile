image_name=kondaurov/scalapb_gen:json_schema
image_name_deps=kondaurov/scalapb_gen:deps

build_deps:
	docker build -t ${image_name_deps} -f Dockerfile_deps .

build:
	docker build -t ${image_name} -f Dockerfile .

ssh:
	docker run -it --rm --entrypoint=/bin/sh ${image_name}

generate:
	docker run --rm \
	 -v $(PWD):/project \
	 -e PROTO_DIR=/project/test_protos/src/main/protobuf \
	 -e OUT_DIR=/project/out \
	 kondaurov/scalapb_gen:json_schema