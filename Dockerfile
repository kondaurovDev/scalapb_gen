FROM kondaurov/scalapb_gen:deps as builder

WORKDIR /project

ADD . .

MAINTAINER Kondaurov Alexander <kondaurov.dev@gmail.com>

RUN sbt generator/stage

FROM kondaurov/jre-alpine:8 as generator

ADD sh /scripts

COPY --from=builder /project/generator/target/universal/stage /app

ENTRYPOINT ["/bin/sh", "/scripts/generate.sh"]