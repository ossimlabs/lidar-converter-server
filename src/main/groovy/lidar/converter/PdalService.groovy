package lidar.converter

import javax.inject.Singleton
import groovy.json.JsonSlurper
import geoscript.geom.Bounds
import geoscript.proj.Projection

@Singleton
class PdalService {
    String getBboxWkt(File inputFile) {
        def metadata = "pdal info --metadata ${inputFile}".execute().text
        def json = new JsonSlurper().parseText(metadata)
        def proj = new Projection(json?.metadata?.spatialreference)
        def coords = ['minx', 'miny', 'maxx', 'maxy'].collect { json?.metadata[it] }
        def wkt = new Bounds(*coords, proj).reproject('epsg:4326')?.polygon?.wkt

        return wkt
    }
}