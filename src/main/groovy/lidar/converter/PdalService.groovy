package lidar.converter

import javax.inject.Singleton
import groovy.json.JsonSlurper
import geoscript.geom.Bounds
import geoscript.proj.Projection

@Singleton
class PdalService {
  String getBboxWkt( File inputFile ) {
    def metadata = "pdal info --metadata ${ inputFile }".execute().text
    def json = new JsonSlurper().parseText( metadata )
    def proj = new Projection( json?.metadata?.spatialreference )
    def coords = [ 'minx', 'miny', 'maxx', 'maxy' ].collect { json?.metadata[ it ] }
    def wkt = new Bounds( *coords, proj ).reproject( 'epsg:4326' )?.polygon?.wkt

    return wkt
  }

  String getMaterial( File lidarFile ) {
    def cmd = [
        'pdal', 'info',
        lidarFile
    ]

    def p = cmd.execute()
    def stdout = new StringBuilder()
    def stderr = new StringBuilder()

    p.consumeProcessOutput( stdout, stderr )

    if ( !p.waitFor() ) {
      //println stdout
      def json = new JsonSlurper().parseText( stdout.toString() )
      def channels = json?.stats?.statistic*.name

      if ( channels.containsAll( [ 'Red', 'Green', 'Blue' ] ) ) {
        return 'RGB'
      } else {
        return 'ELEVATION'
      }
    } else {
      System.err.println( stderr )
    }
  }
}