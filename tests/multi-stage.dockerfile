FROM golang:1.16-alpine AS build
WORKDIR /src
COPY . .
RUN go build -o /bin/hello .

FROM alpine:latest
COPY --from=build /bin/hello /bin/hello
ENTRYPOINT ["/bin/hello"]
