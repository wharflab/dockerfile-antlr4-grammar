FROM --platform=linux/amd64 alpine:3.22 AS source

RUN --mount=type=cache,target=/var/cache/apk --network=none apk add curl
RUN --mount="type=secret,id=api key" \
    --security=insecure \
    printf '%s\n' "--network=host"
RUN --network=none ["sh", "-c", "echo ready"]
RUN -- printf '%s\n' --mount=not-a-builder-flag
RUN "--" echo quoted separator
RUN --mount=type=cache "--" echo quoted separator after flag
RUN --mount='type=secret,id=a\b' true
RUN --mount=type=cache,\
# continuation comment
    echo continued
CMD -- echo command separator

ADD --chown=1000:1000 source /opt/source/
COPY --from=source --chown=1000:1000 /bin/busybox /bin/busybox
COPY --chmod="0755" ["source", "/dest/"]
COPY -- source /separator-dest/
HEALTHCHECK --interval=30s --timeout=3s CMD true
