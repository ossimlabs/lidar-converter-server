package lidar.converter.entwine

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.QueryValue

@Controller( "/entwine-converter" )
class EntwineConverterController
{
	EntwineConverterService entwineConverterService
	
	EntwineConverterController( EntwineConverterService entwineConverterService )
	{
		this.entwineConverterService = entwineConverterService
	}

	@Post( '/run' )
	HttpResponse<String> run( @QueryValue String inputFileName )
	{
		def inputFile = inputFileName as File
		String message = entwineConverterService.run( inputFile )
		
		HttpResponse.ok( message )
	}
}