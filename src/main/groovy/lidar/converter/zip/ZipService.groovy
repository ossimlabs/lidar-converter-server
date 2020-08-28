package lidar.converter.zip

import javax.inject.Singleton

@Singleton
class ZipService {

    String run(String dirToZip, String zipFileOutputName){

        println "Directory to be zipped: ${dirToZip}"
        println "Zip file output name: ${zipFileOutputName}"

        def ant = new AntBuilder()
        ant.zip(destfile: "${dirToZip}/${zipFileOutputName}.zip", basedir: dirToZip)

    }

}