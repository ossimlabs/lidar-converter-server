package lidar.converter.entwine

import lidar.converter.PdalService
import org.apache.commons.io.FilenameUtils

import java.time.Instant
import javax.inject.Singleton
import lidar.converter.LidarIndexerClient
import io.micronaut.context.annotation.Value

@Singleton
class EntwineConverterService {
    LidarIndexerClient lidarIndexerClient
    PdalService pdalService

    @Value('${lidar.converter.entwine.outputDirectory}')
    String outputDirectory

    EntwineConverterService(LidarIndexerClient lidarIndexerClient, PdalService pdalService) {
        this.lidarIndexerClient = lidarIndexerClient
        this.pdalService = pdalService
    }

    String run(File inputFile) {
        def outputFile = "/entwine/${FilenameUtils.getBaseName(inputFile.name)}"

        def cmd = [
                'entwine', 'build',
                '-i', "${inputFile}",
                '-o', "${new File(outputDirectory, outputFile)}"
        ]

        println cmd.join(' ')

        def process = cmd.execute()
        def stdout = new StringWriter()
        def stderr = new StringWriter()

        process.consumeProcessOutput(stdout, stderr)

        def exitCode = process.waitFor()

        println "exitCode: ${exitCode}"

        if (exitCode == 0) {
            Map<String, Object> lidarProduct = [
                    ingest_date: Instant.now().toString(),
                    keyword    : 'entwine',
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
