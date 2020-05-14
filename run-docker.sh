#!/bin/bash

 docker run -it --rm -p 8080:8080 \
  -v ${OSSIM_DATA}/LiDAR_Test:/input \
  -v ${PWD}/output:/output \
  lidar-converter-server
