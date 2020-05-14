package lidar.converter.potree

import javax.inject.Singleton

@Singleton
class PotreeConverterService
{
	String run( File inputFile )
	{
		def cmd = [
			'/usr/local/bin/PotreeConverter',
			'--source', "/input/${ inputFile.name }",
			'--outdir', "/output/${ inputFile.name }",
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
			return stdout.toString()
		}
		else
		{
			return stderr.toString()
		}
	}
}
