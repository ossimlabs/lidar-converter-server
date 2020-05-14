package lidar.converter.entwine

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.HttpStatus

@Controller( "/entwine-converter" )
class EntwineConverterController
{
	EntwineConverterService entwineConverterService
	
	EntwineConverterController( EntwineConverterService entwineConverterService )
	{
		this.entwineConverterService = entwineConverterService
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
		String message = entwineConverterService.run( inputFile )
		
		HttpResponse.ok( message )
	}
}