# This is a comment
FROM alpine:latest

# Environment variables
ENV MY_NAME="John Doe" \
    MY_DOG=Rex \
    MY_CAT=Fluffy

RUN apk add --no-cache curl

COPY . /app

WORKDIR /app

# Exec form
RUN ["echo", "Hello World"]

# Shell form
RUN echo Hello [World]

EXPOSE 8080

ENTRYPOINT ["curl"]
CMD ["--help"]

ONBUILD RUN echo "Building..."

HEALTHCHECK --interval=5m --timeout=3s \
  CMD curl -f http://localhost/ || exit 1
