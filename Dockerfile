FROM kondaurov/scalapb_gen:deps as builder

WORKDIR /project

ADD . .

ENV protocVersion=3.6.1

#RUN \
# wget https://github.com/protocolbuffers/protobuf/releases/download/v${protocVersion}/protoc-${protocVersion}-linux-x86_64.zip && \
# unzip *.zip && mv protoc* /protoc

MAINTAINER Kondaurov Alexander <kondaurov.dev@gmail.com>

RUN sbt generator/stage

FROM kondaurov/jre-alpine:8 as generator

ADD sh /scripts

COPY --from=builder /project/generator/target/universal/stage /app
COPY --from=builder /project/generator/target/universal/stage /bin/protoc

ENTRYPOINT ["/bin/sh", "/scripts/generate.sh"]