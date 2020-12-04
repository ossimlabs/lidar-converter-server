package lidar.converter

import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.MediaType
import lidar.converter.entwine.EntwineConverterService
import lidar.converter.potree.PotreeConverterService
import lidar.converter.LidarIndexerClient
import org.apache.commons.io.FilenameUtils

import javax.validation.constraints.NotNull
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant

@Controller
class UploadController {

    @Value('${lidar.converter.inputDirectory}')
    String inputDirectory

    PotreeConverterService potreeConverterService
    EntwineConverterService entwineConverterService
    LidarIndexerClient lidarIndexerClient

    UploadController(PotreeConverterService potreeConverterService, EntwineConverterService entwineConverterService, LidarIndexerClient lidarIndexerClient) {
        this.potreeConverterService = potreeConverterService
        this.entwineConverterService = entwineConverterService
        this.lidarIndexerClient = lidarIndexerClient
    }

    @NotNull
    @Post(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_PLAIN)
    HttpResponse<String> uploadBytes(byte[] file, String fileName, String fileType) {

        String fileExtension = FilenameUtils.getExtension(fileName).trim()

        // Only convert lidar ".las" or ".laz" files
        if(fileExtension == "las" || fileExtension == "laz"){

            try {

                File tmpFile = new File("${inputDirectory}/${fileName}")
                Path path = Paths.get(tmpFile.absolutePath)
                Files.write(path, file)

                Map<String, Object> lidarProduct = [
                        ingest_date: Instant.now().toString(),
                        keyword: fileName,
                        type: fileType,
                        status: 'Uploading',
                        bbox: null
                ]

                String recordId = lidarIndexerClient.postLidarProduct(lidarProduct)
                println "Created a new lidar indexer record with id: ${recordId}"

                lidarProduct.put("id", recordId)

                //sleep(10000)

                Thread.start{
                    if (fileType == "potree") {
                        println "Potree uploaded." // TODO: logger
                        potreeConverterService.run(tmpFile, lidarProduct)

                    } else {
                        println "Entwine uploaded" // TODO: logger
                        entwineConverterService.run(tmpFile, lidarProduct)
                    }
                }

                HttpResponse.ok("File uploaded successfully.")

            } catch (IOException exception) {
                println exception // TODO: logger
                HttpResponse.badRequest("Upload Failed")

            }
        } else {
            HttpResponse.badRequest("File type not allowed")
        }


    }


}
