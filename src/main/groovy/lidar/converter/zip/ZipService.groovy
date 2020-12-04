package lidar.converter.zip

import javax.inject.Singleton

import lidar.converter.LidarIndexerClient

@Singleton
class ZipService {

    LidarIndexerClient lidarIndexerClient

    ZipService(LidarIndexerClient lidarIndexerClient) {
        this.lidarIndexerClient = lidarIndexerClient
    }

    String run(String dirToZip, String zipFileOutputName, Map lidarProduct){
        lidarProduct.put("status", "Zipping")
        lidarIndexerClient.putLidarProduct(lidarProduct, lidarProduct.get("id") as String)

        println "Directory to be zipped: ${dirToZip}"
        println "Zip file output name: ${zipFileOutputName}"

        def ant = new AntBuilder()
        ant.zip(destfile: "${dirToZip}/${zipFileOutputName}.zip", basedir: dirToZip)

    }

}