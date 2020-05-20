FROM nexus-docker-public-hosted.ossim.io/lidar-converter-alpine:latest
USER root
RUN apk add --no-cache openjdk11-jre-headless
USER 1001
COPY build/libs/lidar-converter-server-*-all.jar run_java.sh ./
EXPOSE 8080
CMD ./run_java.sh
