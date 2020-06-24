package lidar.converter.potree

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.QueryValue

@Controller( "/potree-converter" )
class PotreeConverterController
{
	PotreeConverterService potreeConverterService
	
	PotreeConverterController( PotreeConverterService potreeConverterService )
	{
		this.potreeConverterService = potreeConverterService
	}

	@Post( '/run' )
	HttpResponse<String> run( @QueryValue String inputFileName )
	{
		def inputFile = inputFileName as File
		String message = potreeConverterService.run( inputFile )
		
		HttpResponse.ok( message )
	}
}
