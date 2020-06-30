package lidar.converter.potree

import lidar.converter.PdalService

import java.time.Instant
import javax.inject.Singleton
import lidar.converter.LidarIndexerClient
import io.micronaut.context.annotation.Value

@Singleton
class PotreeConverterService
{
	LidarIndexerClient lidarIndexerClient
	PdalService pdalService

	@Value('${lidar.converter.potree.inputDirectory}')
	String inputDirectory

	@Value('${lidar.converter.potree.outputDirectory}')
	String outputDirectory

	PotreeConverterService(LidarIndexerClient lidarIndexerClient, PdalService pdalService) {
		this.lidarIndexerClient = lidarIndexerClient
		this.pdalService = pdalService
	}

	String run( File inputFile )
	{
		def cmd = [
			'/usr/local/bin/PotreeConverter',
			'--source', "${inputDirectory}/${ inputFile.name }",
			'--outdir', "${outputDirectory}/${ inputFile.name }",
			'--generate-page', 'index',
			'--material', 'RGB',
			'--edl-enabled',
			'--output-format', 'LAS',
			'--overwrite'
		]
		
		println cmd.join( ' ' )
		
		def process = cmd.execute()
		def stdout = new StringWriter()
		def stderr = new StringWriter()
		
		process.consumeProcessOutput( stdout, stderr )
		
		def exitCode = process.waitFor()
		
		if ( exitCode == 0 )
		{
			Map<String,Object> lidarProduct = [
				ingest_date: Instant.now().toString(),
				keyword: 'potree',
				s3_link:  "${outputDirectory}/${ inputFile.name }" as String,
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
