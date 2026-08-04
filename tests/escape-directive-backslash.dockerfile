# check=skip=all
# escape=\
# syntax=docker/dockerfile:1
FROM alpine:3.22
RUN printf '%s\n' attached\

continuation
RUN --mount=type=cache,\

target=/var/cache true
