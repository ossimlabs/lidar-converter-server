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

    String run(File inputFile) {
        String outputFile = "/entwine/${FilenameUtils.getBaseName(inputFile.name)}"
        String outputLocation = new File(outputDirectory, outputFile)

        def cmd = [
                'entwine', 'build',
                '-i', "${inputFile}",
                '-o', "${outputLocation}"
        ]

        println cmd.join(' ')

        zipService.run(outputLocation, FilenameUtils.getBaseName(inputFile.name))

        def process = cmd.execute()
        def stdout = new StringWriter()
        def stderr = new StringWriter()

        process.consumeProcessOutput(stdout, stderr)

        def exitCode = process.waitFor()

        println "exitCode: ${exitCode}"

        if (exitCode == 0) {
            zipService.run(outputLocation, FilenameUtils.getBaseName(inputFile.name))

            Map<String, Object> lidarProduct = [
                    ingest_date: Instant.now().toString(),
                    keyword    : FilenameUtils.getBaseName(inputFile.name),
                    s3_link    : outputFile as String,
                    bbox       : pdalService.getBboxWkt(inputFile)
            ]

            lidarIndexerClient.postLidarProduct(lidarProduct)

            return stdout.toString()
        } else {
            return stderr.toString()
        }
    }

}
