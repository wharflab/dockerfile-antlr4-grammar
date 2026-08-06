# escape=`
FROM scratch
RUN []
RUN [""]
RUN ["plain", "two words"]
RUN ["continued`
value"]
SHELL ["/bin/sh", "-c"]
