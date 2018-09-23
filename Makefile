image_name=kondaurov/scalapb_gen:json_schema
image_name_deps=kondaurov/scalapb_gen:json_schema_deps

build_deps:
	docker build -t ${image_name_deps} -f Dockerfile_deps .

build:
	docker build -t ${image_name} -f Dockerfile .