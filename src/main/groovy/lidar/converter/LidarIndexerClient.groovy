package lidar.converter

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

		String result = client.toBlocking().exchange(
			HttpRequest.POST(lidarIndexerPath, lidarProduct),
			Map
		)

		println "POST: ${result}"
    }
}