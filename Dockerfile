FROM nexus-docker-public-hosted.ossim.io/lidar-converter:latest
USER root
#RUN yum install -y java-1.8.0-openjdk-headless
RUN yum install -y java-11-openjdk-headless
USER 1001
COPY build/libs/lidar-converter-server-*-all.jar run_java.sh ./
EXPOSE 8080
CMD ./run_java.sh
