FROM alpine:3.22
RUN echo hello
RUN ["printf", "%s\n", "hello"]
RUN ["printf", "\uD83D\uDE00"]
ONBUILD RUN echo built
