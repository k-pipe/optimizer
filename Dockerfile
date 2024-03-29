FROM ghcr.io/graalvm/native-image-community:21-muslib AS build
#FROM ghcr.io/graalvm/native-image-community:21 AS build
ADD src ./
RUN javac org/kpipe/Optimizer.java
RUN native-image org.kpipe.Optimizer --static --libc=musl -march=compatibility

FROM alpine:3.18.4
COPY --from=build /app/org.kpipe.optimizer /bin/optimizer
RUN