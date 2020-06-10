package lidar.converter.entwine

import java.time.Instant
import javax.inject.Singleton
import lidar.converter.LidarIndexerClient

@Singleton
class EntwineConverterService
{
	LidarIndexerClient lidarIndexerClient

	EntwineConverterService(LidarIndexerClient lidarIndexerClient) {
		this.lidarIndexerClient = lidarIndexerClient
	}

	String run( File inputFile )
	{
		def cmd = [
			'entwine', 'build',
			'-i', "/input/${inputFile.name}",
			'-o', "/output/${ inputFile.name }"
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
				s3_link:  "/output/${ inputFile.name }" as String
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