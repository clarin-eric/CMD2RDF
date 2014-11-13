package nl.knaw.dans.cmd2rdf.batch;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        try {
			String s = FileUtils.readFileToString(new File("/Users/akmi/eko-cmdi-test/conf/cmd2rdf-jobs-mpi-profiles-components.xml"));
			System.out.println(s);
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
