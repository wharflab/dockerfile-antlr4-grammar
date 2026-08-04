FROM alpine:3.22

RUN --mount=type=cache,target=/var/cache/apk --network=none apk add curl
RUN --mount="type=secret,id=api key" \
    --security=insecure \
    printf '%s\n' "--network=host"
RUN --network=none ["sh", "-c", "echo ready"]
RUN -- printf '%s\n' --mount=not-a-builder-flag
