# syntax=docker/dockerfile:1
# EsCaPe = `
FROM alpine:3.22
RUN echo hello
RUN echo `"two  words"
RUN ["printf", "%s\n", "hello"]
RUN ["printf", "\uD83D\uDE00"]
ONBUILD RUN echo built
