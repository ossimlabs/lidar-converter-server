package lidar.converter.potree

import lidar.converter.PdalService
import lidar.converter.zip.ZipService
import org.apache.commons.io.FilenameUtils

import java.time.Instant
import javax.inject.Singleton
import lidar.converter.LidarIndexerClient
import io.micronaut.context.annotation.Value

@Singleton
class PotreeConverterService {
    LidarIndexerClient lidarIndexerClient
    PdalService pdalService
    ZipService zipService

    @Value('${lidar.converter.potree.outputDirectory}')
    String outputDirectory

    PotreeConverterService(LidarIndexerClient lidarIndexerClient, PdalService pdalService, ZipService zipService) {
        this.lidarIndexerClient = lidarIndexerClient
        this.pdalService = pdalService
        this.zipService = zipService
    }

    String run(File inputFile, Map lidarProduct ) {

        lidarProduct.put("status", "Converting")
        lidarIndexerClient.putLidarProduct(lidarProduct, lidarProduct.get("id") as String)

        String inputFileFullPath = inputFile.getAbsolutePath()

        def outputFile = "/potree/${FilenameUtils.getBaseName(inputFile.name)}"
        String outputLocation = new File(outputDirectory, outputFile)

        def cmd = [
                '/usr/local/bin/PotreeConverter',
                '--source', "${inputFileFullPath}",
                '--outdir', "${outputLocation}",
                '--generate-page', 'index',
                '--material', 'RGB',
                '--edl-enabled',
                '--output-format', 'LAS',
                '--overwrite'
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
//                    type: 'Potree',
//                    bbox       : pdalService.getBboxWkt(inputFile)
//            ]

            // Removing this for now to save some processing time until we need to display footprints on a map
            // lidarProduct.put("bbox", pdalService.getBboxWkt(inputFile))

            lidarProduct.put("keyword", FilenameUtils.getBaseName(inputFile.name))
            lidarProduct.put("status", "Success")
            lidarIndexerClient.putLidarProduct(lidarProduct, lidarProduct.get("id") as String)

            sleep(3000)

            lidarProduct.put("status", "Completed")
            lidarIndexerClient.putLidarProduct(lidarProduct, lidarProduct.get("id") as String)

            println stdout.toString()

            // Clean up after the file has been successfully converted
            inputFile.delete()

            return stdout.toString()
        } else {
            inputFile.delete()
            return stderr.toString()

        }
    }
}
