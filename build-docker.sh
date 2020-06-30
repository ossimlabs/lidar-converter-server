#!/bin/bash

gradle assemble
#docker build -t lidar-converter-server .

gradle jibDockerBuild

