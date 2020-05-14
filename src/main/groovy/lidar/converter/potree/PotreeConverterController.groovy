package lidar.converter.potree

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.HttpStatus


@Controller( "/potree-converter" )
class PotreeConverterController
{
	PotreeConverterService potreeConverterService
	
	PotreeConverterController( PotreeConverterService potreeConverterService )
	{
		this.potreeConverterService = potreeConverterService
	}
	
	@Get( "/" )
	HttpStatus index()
	{
		return HttpStatus.OK
	}
	
	@Post( '/run' )
	HttpResponse<String> run( HttpRequest request )
	{
		def inputFile = request.parameters.getFirst( 'inputFile' ).orElse( null ) as File
		String message = potreeConverterService.run( inputFile )
		
		HttpResponse.ok( message )
	}
}
