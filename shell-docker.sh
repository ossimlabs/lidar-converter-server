#!/bin/sh

# docker run -it --rm -p 8080:8080 \
#  -v ${OSSIM_DATA}/LiDAR_Test:/input \
#  -v ${PWD}/output:/output \
#  lidar-converter-server

docker run -it --rm -p 8888:8080 \
  -v ${OSSIM_DATA}/LiDAR_Test:/input \
  -v ${PWD}/output:/output \
  --entrypoint=sh \
  nexus-docker-public-hosted.ossim.io/lidar-converter-server

