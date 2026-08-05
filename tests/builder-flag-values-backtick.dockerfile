# escape=`
FROM --platform="linux/amd64" alpine:3.22
RUN --mount="type=secret,id=a`"b" true
RUN --mount='type=secret,id=a``b' true
RUN --mount='type=secret,id=a\b' true
RUN --mount=type=cache,`
target=/var/cache true
RUN --mount=type=cache,`
# continuation comment
true
RUN -- echo --mount="not a flag"
RUN --"" --mount="not a flag"
RUN --'' --mount='not a flag'
RUN --""'' --mount="not a flag"
RUN echo --mount="not a flag"
RUN --""x true
RUN --''x true
RUN --"
RUN --'
CMD -- --option="quoted value"
ADD --chown="1000:1000" source /add/
COPY --chmod="0755" source /dest/
HEALTHCHECK --interval="30s" CMD true
