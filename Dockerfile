FROM kondaurov/scalapb:json_schema_deps as builder

WORKDIR /project

ADD . .

MAINTAINER Kondaurov Alexander <kondaurov.dev@gmail.com>

RUN sbt generator/stage

FROM kondaurov/jre-alpine:8

ADD sh /scripts

COPY --from=builder /project/generator/target/universal/stage /app

ENTRYPOINT ["/scripts/generate.sh"]