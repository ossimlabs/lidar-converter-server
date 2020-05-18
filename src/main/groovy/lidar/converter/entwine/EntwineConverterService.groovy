package lidar.converter.entwine

import javax.inject.Singleton

@Singleton
class EntwineConverterService
{
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