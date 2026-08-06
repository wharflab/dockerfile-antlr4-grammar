FROM scratch
RUN []
RUN [""]
RUN ["plain", "two words"]
RUN ["\"", "\\", "\/", "\b", "\f", "\n", "\r", "\t"]
RUN ["continued\
value"]
CMD ["--help"]
ENTRYPOINT ["/entrypoint"]
ADD ["source", "/add/"]
COPY ["source", "/copy/"]
VOLUME ["/data", "/cache"]
SHELL ["/bin/sh", "-c"]
HEALTHCHECK CMD ["probe", "--ready"]
