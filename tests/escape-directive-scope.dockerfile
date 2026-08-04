# An ordinary comment closes the parser-directive block.
# escape=`
FROM alpine:3.22
RUN printf `"two  words`"
RUN printf '%s\n' first \
    second
