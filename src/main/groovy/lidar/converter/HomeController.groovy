package lidar.converter

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.HttpResponse


@Controller("/")
class HomeController {

    @Get("/")
    HttpResponse index() {
        return HttpResponse.redirect('/swagger-ui'.toURI())
    }
}