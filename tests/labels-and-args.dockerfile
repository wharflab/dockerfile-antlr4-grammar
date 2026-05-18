ARG VERSION=latest
FROM ubuntu:${VERSION}
LABEL maintainer="me@example.com"
LABEL version="1.0"
ARG USERNAME=guest
USER ${USERNAME}
WORKDIR /home/${USERNAME}
