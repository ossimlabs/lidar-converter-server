package lidar.converter.potree

import lidar.converter.PdalService
import org.apache.commons.io.FilenameUtils

import java.time.Instant
import javax.inject.Singleton
import lidar.converter.LidarIndexerClient
import io.micronaut.context.annotation.Value

@Singleton
class PotreeConverterService {
    LidarIndexerClient lidarIndexerClient
    PdalService pdalService

    @Value('${lidar.converter.potree.outputDirectory}')
    String outputDirectory

    PotreeConverterService(LidarIndexerClient lidarIndexerClient, PdalService pdalService) {
        this.lidarIndexerClient = lidarIndexerClient
        this.pdalService = pdalService
    }

    String run(File inputFile) {
        def outputFile = "/potree/${FilenameUtils.getBaseName(inputFile.name)}"

        def cmd = [
                '/usr/local/bin/PotreeConverter',
                '--source', "${inputFile}",
                '--outdir', "${new File(outputDirectory, outputFile)}",
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

        if (exitCode == 0) {
            Map<String, Object> lidarProduct = [
                    ingest_date: Instant.now().toString(),
                    keyword    : 'potree',
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
