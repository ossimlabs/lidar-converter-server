package lidar.converter.entwine

import lidar.converter.PdalService

import java.time.Instant
import javax.inject.Singleton
import lidar.converter.LidarIndexerClient
import io.micronaut.context.annotation.Value

@Singleton
class EntwineConverterService
{
	LidarIndexerClient lidarIndexerClient
	PdalService pdalService


	@Value('${lidar.converter.entwine.inputDirectory}')
	String inputDirectory

	@Value('${lidar.converter.entwine.outputDirectory}')
	String outputDirectory


	EntwineConverterService(LidarIndexerClient lidarIndexerClient, PdalService pdalService) {
		this.lidarIndexerClient = lidarIndexerClient
		this.pdalService = pdalService
	}

	String run( File inputFile )
	{
		def cmd = [
			'entwine', 'build',
			'-i', "${inputDirectory}/${inputFile.name}",
			'-o', "${outputDirectory}/${ inputFile.name }"
		]

		println cmd.join( ' ' )
		
		def process = cmd.execute()
		def stdout = new StringWriter()
		def stderr = new StringWriter()
		
		process.consumeProcessOutput( stdout, stderr )
		
		def exitCode = process.waitFor()

		println "exitCode: ${exitCode}"
				
		if ( exitCode == 0 )
		{
			Map<String,Object> lidarProduct = [
				ingest_date: Instant.now().toString(),
				keyword: 'entwine',
				s3_link:  "/output/${ inputFile.name }" as String,
				bbox: pdalService.getBboxWkt(inputFile)
			]

			lidarIndexerClient.postLidarProduct(lidarProduct)

			return stdout.toString()
		}
		else
		{
			return stderr.toString()
		}
	}
	
}