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

    String run(File inputFile) {
        def outputFile = "/potree/${FilenameUtils.getBaseName(inputFile.name)}"
        String outputLocation = new File(outputDirectory, outputFile)

        def cmd = [
                '/usr/local/bin/PotreeConverter',
                '--source', "${inputFile}",
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
            zipService.run(outputLocation, FilenameUtils.getBaseName(inputFile.name))

            Map<String, Object> lidarProduct = [
                    ingest_date: Instant.now().toString(),
                    keyword    : FilenameUtils.getBaseName(inputFile.name),
                    s3_link    : outputFile as String,
                    bbox       : pdalService.getBboxWkt(inputFile)
            ]

            lidarIndexerClient.postLidarProduct(lidarProduct)
            println stdout.toString()

            return stdout.toString()
        } else {
            return stderr.toString()
        }
    }
}
