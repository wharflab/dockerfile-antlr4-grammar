# escape=`
FROM scratch
RUN []
RUN [""]
RUN ["plain", "two words"]
RUN ["\"", "\\", "\/", "\b", "\f", "\n", "\r", "\t"]
RUN ["continued`
value"]
SHELL ["/bin/sh", "-c"]
