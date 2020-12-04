package lidar.converter

import io.micronaut.http.HttpResponse

import javax.inject.Singleton

import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient

@Singleton
class LidarIndexerClient 
{
	@Value('${lidar.indexer.endpoint}')
	String lidarIndexerEndpoint

	@Value('${lidar.indexer.postPath}')
	String lidarIndexerPostPath

	@Value('${lidar.indexer.putPath}')
	String lidarIndexerPutPath


    def postLidarProduct(Map<String,Object> lidarProduct) 
    {
        HttpClient client = HttpClient.create(lidarIndexerEndpoint?.toURL())
		println "Posting: ${lidarIndexerEndpoint} with path ${lidarIndexerPostPath}"

		def result = client.toBlocking().exchange(
			HttpRequest.POST(lidarIndexerPostPath, lidarProduct),
			Map
		)

		println "POST: ${result.getBody().get().id}"
		return result.getBody().get().id
    }

	def putLidarProduct(Map<String,Object> lidarProduct, String id)
	{
		HttpClient client = HttpClient.create(lidarIndexerEndpoint?.toURL())
		println "Posting: ${lidarIndexerEndpoint} with path ${lidarIndexerPutPath}"

		def putResult = client.toBlocking().exchange(
				HttpRequest.PUT("${lidarIndexerPutPath}/${id}", lidarProduct),
				Map
		)
		println "Pupt: ${putResult.getBody()}"
	}
}