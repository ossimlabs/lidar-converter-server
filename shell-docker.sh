#!/bin/sh

# docker run -it --rm -p 8080:8080 \
#  -v ${OSSIM_DATA}/LiDAR_Test:/input \
#  -v ${PWD}/output:/output \
#  lidar-converter-server

PREFIX=nexus-docker-public-hosted.ossim.io
#PREFIX=hercules

docker run -it --rm -p 8888:8080 \
  -v ${OSSIM_DATA}/LiDAR_Test:/input \
  -v ${PWD}/output:/output \
  --entrypoint=sh \
  $PREFIX/lidar-converter-server

