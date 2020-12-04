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
	String lidarIndexerEndopint

	@Value('${lidar.indexer.path}')
	String lidarIndexerPath


    def postLidarProduct(Map<String,Object> lidarProduct) 
    {
        HttpClient client = HttpClient.create(lidarIndexerEndopint?.toURL())

		def result = client.toBlocking().exchange(
			HttpRequest.POST(lidarIndexerPath, lidarProduct),
			Map
		)

		println "POST: ${result.getBody().get().id}"
		return result.getBody().get().id
    }

	def putLidarProduct(Map<String,Object> lidarProduct, String id)
	{
		HttpClient client = HttpClient.create(lidarIndexerEndopint?.toURL())

		def putResult = client.toBlocking().exchange(
				HttpRequest.PUT("/lidarProducts/updateById/${id}", lidarProduct),
				Map
		)
		println "Pupt: ${putResult.getBody()}"
	}
}