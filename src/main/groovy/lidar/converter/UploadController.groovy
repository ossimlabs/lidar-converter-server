package lidar.converter

import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.MediaType
import lidar.converter.entwine.EntwineConverterService
import lidar.converter.potree.PotreeConverterService

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Controller
class UploadController {

    @Value('${lidar.converter.inputDirectory}')
    String inputDirectory

    PotreeConverterService potreeConverterService
    EntwineConverterService entwineConverterService

    UploadController(PotreeConverterService potreeConverterService, EntwineConverterService entwineConverterService) {
        this.potreeConverterService = potreeConverterService
        this.entwineConverterService = entwineConverterService
    }

    @Post(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_PLAIN)
    HttpResponse<String> uploadBytes(byte[] file, String fileName, String fileType) {
        try {

            File tmpFile = new File("${inputDirectory}/${fileName}")
            Path path = Paths.get(tmpFile.absolutePath)
            Files.write(path, file)

            if (fileType == "potree") {
                println "Potree uploaded." // TODO: logger
                potreeConverterService.run(tmpFile)

            } else {
                println "Entwine uploaded" // TODO: logger
                entwineConverterService.run(tmpFile)
            }

            HttpResponse.ok("File uploaded successfully.")

        } catch (IOException exception) {
            println exception // TODO: logger
            HttpResponse.badRequest("Upload Failed")

        }
    }


}
