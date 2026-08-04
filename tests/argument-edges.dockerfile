FROM "base image" AS build
ARG QUOTED="two words" ESCAPED=two\ words
ARG SPLIT=foo\  bar
LABEL quoted="two words" escaped=two\ words
ENV LEGACY two words
ENV QUOTED="two words" ESCAPED=two\ words
RUN printf '%s\n' "two words" two\ words
RUN ["\q"]
CMD ['echo']
COPY "source file" /quoted/
COPY source\ file /escaped/
VOLUME "/data dir"
HEALTHCHECK CMD test -f "/tmp/ready file"
