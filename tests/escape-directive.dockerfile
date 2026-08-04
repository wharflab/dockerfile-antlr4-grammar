# syntax=docker/dockerfile:1
# EsCaPe = `
# check=skip=all
FROM alpine:3.22
RUN printf `"two  words`"
RUN printf '%s\n' first `
# continuation comment
    second
RUN printf '%s\n' 'C:\Program Files\app'
RUN --mount="type=secret,id=a`"b" true
RUN --mount=type=cache,`

target=/var/cache true
RUN printf '%s\n' attached`

continuation
