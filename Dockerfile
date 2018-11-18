FROM kondaurov/sbt-alpine:jdk8sbt1.2.1_compiled as builder

WORKDIR /project

ADD . .

ENV protocVersion=3.6.1

MAINTAINER Kondaurov Alexander <kondaurov.dev@gmail.com>

RUN sbt generator/stage

FROM kondaurov/jre-alpine:8 as generator

RUN \
    addgroup -g 1000 bill && \
    adduser -u 1000 -G bill -D bill

USER bill

WORKDIR /home/bill

ADD --chown=bill:bill  sh scripts

COPY --chown=bill:bill --from=builder /project/generator/target/universal/stage app

ENTRYPOINT ["/bin/sh", "/home/bill/scripts/generate.sh"]