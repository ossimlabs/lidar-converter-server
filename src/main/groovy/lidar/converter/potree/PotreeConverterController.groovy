package lidar.converter.potree

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue

@Controller("/potree-converter")
class PotreeConverterController {
    PotreeConverterService potreeConverterService

    PotreeConverterController(PotreeConverterService potreeConverterService) {
        this.potreeConverterService = potreeConverterService
    }

    @Post(uri = '/run', produces = [MediaType.TEXT_PLAIN])
    HttpResponse<String> run(@QueryValue String inputFileName) {
        def inputFile = inputFileName as File
        String message = potreeConverterService.run(inputFile)

        HttpResponse.ok(message)
    }
}
