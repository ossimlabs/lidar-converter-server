package lidar.converter.entwine

import lidar.converter.PdalService
import lidar.converter.zip.ZipService
import org.apache.commons.io.FilenameUtils

import java.time.Instant
import javax.inject.Singleton
import lidar.converter.LidarIndexerClient
import io.micronaut.context.annotation.Value

@Singleton
class EntwineConverterService {
    LidarIndexerClient lidarIndexerClient
    PdalService pdalService
    ZipService zipService

    @Value('${lidar.converter.entwine.outputDirectory}')
    String outputDirectory

    EntwineConverterService(LidarIndexerClient lidarIndexerClient, PdalService pdalService, ZipService zipService) {
        this.lidarIndexerClient = lidarIndexerClient
        this.pdalService = pdalService
        this.zipService = zipService
    }

    String run(File inputFile, Map lidarProduct) {

        lidarProduct.put("status", "Converting")
        lidarIndexerClient.putLidarProduct(lidarProduct, lidarProduct.get("id") as String)

        String outputFile = "/entwine/${FilenameUtils.getBaseName(inputFile.name)}"
        String outputLocation = new File(outputDirectory, outputFile)

        def cmd = [
                'entwine', 'build',
                '-i', "${inputFile}",
                '-o', "${outputLocation}"
        ]

        println cmd.join(' ')

        def process = cmd.execute()
        def stdout = new StringWriter()
        def stderr = new StringWriter()

        process.consumeProcessOutput(stdout, stderr)

        def exitCode = process.waitFor()

        println "exitCode: ${exitCode}"

        if (exitCode == 0) {
            zipService.run(outputLocation, FilenameUtils.getBaseName(inputFile.name), lidarProduct)

//            Map<String, Object> lidarProduct = [
//                    ingest_date: Instant.now().toString(),
//                    keyword    : FilenameUtils.getBaseName(inputFile.name),
//                    s3_link    : outputFile as String,
//                    bbox       : pdalService.getBboxWkt(inputFile)
//            ]

            // Removing this for now to save some processing time until we need to display footprints on a map
            // lidarProduct.put("bbox", pdalService.getBboxWkt(inputFile))

            lidarProduct.put("keyword", FilenameUtils.getBaseName(inputFile.name))
            lidarProduct.put("s3_link", outputFile as String)
            lidarProduct.put("status", "Success")
            lidarIndexerClient.putLidarProduct(lidarProduct, lidarProduct.get("id") as String)

            sleep(3000)

            lidarProduct.put("status", "Completed")
            lidarIndexerClient.putLidarProduct(lidarProduct, lidarProduct.get("id") as String)

            return stdout.toString()
        } else {
            return stderr.toString()
        }
    }

}
