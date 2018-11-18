FROM kondaurov/jre-alpine:8

RUN \
    addgroup -g 1000 bill && \
    adduser -u 1000 -G bill -D bill

USER bill

WORKDIR /home/bill

ADD --chown=bill:bill docker/sh scripts
ADD --chown=bill:bill bin app/bin
ADD --chown=bill:bill lib app/lib

ENTRYPOINT ["/bin/sh", "/home/bill/scripts/generate.sh"]