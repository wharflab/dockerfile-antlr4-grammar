FROM alpine:3.22 AS build
ARG VERSION=latest
LABEL title="argument parity" empty=
ENV LEGACY value with spaces
ENV ONE=1 TWO="two words" EMPTY=
RUN printf '%s\n' one \
    two
CMD echo ready
ENTRYPOINT /usr/bin/start --foreground
EXPOSE 8080 8443/tcp
ADD source /opt/source/
COPY source /opt/copy/
VOLUME /data /cache
USER app:app
WORKDIR /work directory
STOPSIGNAL SIGTERM
HEALTHCHECK CMD test -f /tmp/ready
