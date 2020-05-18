# lidar-converter-server
Server that allows users to convert LIDAR data using PotreeConverter remotely

## Requirements:
   - Must have built the docker image for [lidar-converter](https://github.com/ossimlabs/lidar-converter)
   
## Build Steps:
  - `gradle assemble`
  - `./build-docker.sh`
  - `./run-docker.sh`
   
#### Web API Usage

##### Entwine:
- `curl -XPOST http://localhost:8080/entwine-converter/run?inputFile=/input/FTSTORY_Potree_Viewshed_Imagery_sampled.las`
 
##### POTREE
``
