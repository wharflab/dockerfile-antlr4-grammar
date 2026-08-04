# check=skip=all
# escape=\
# syntax=docker/dockerfile:1
FROM alpine:3.22
RUN printf '%s\n' attached\

continuation
RUN --mount=type=cache,\

target=/var/cache true
RUN printf '%s\n' "double quoted\
continuation"
RUN printf '%s\n' 'single quoted\
continuation'
RUN ["printf", "%s\n", "exec\
continuation"]
