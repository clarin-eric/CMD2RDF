package nl.knaw.dans.clarin.playground;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.knaw.dans.clarin.cmd2rdf.exception.ActionException;

public class Apps3 {

	public static void main(String[] args) {
		//String value = "$eko$test123$indarto$";
		String value="0m[35;1m[12:04]";
		System.out.println("begin");
		//Pattern p = Pattern.compile("\\$(.*?)\\$");
		Pattern p = Pattern.compile("\\[(\\d{2}:\\d{2})\\]");
		Matcher m = p.matcher(value);
		 if (m.find( )) {
			 System.out.println(m.group());
			 System.out.println("Found value: " + m.group(0) );
	         System.out.println("Found value: " + m.group(1) );
		} else {
			System.out.println("NO MATCH");
		}
			System.out.println("end");
			
			String isqlCommand = "/usr/local/Cellar/virtuoso/7.1.0/bin/isql 1111 dba dba exec=\"ld_dir_all('/Users/akmi/eko-cmdi-test/rdf-output/', '*.rdf', 'http://eko.indarto/eko.rdf');\"";
			System.out.println(isqlCommand);
			//isqlPath + "/isql " + port + " " + username + " " + password + " exec=\"rdf_loader_run();\"";
			executeIsql(isqlCommand);
			
	}
	
	
	private static void executeIsql(String isqlCommand){
		String[] str = {"/usr/local/Cellar/virtuoso/7.1.0/bin/isql","1111", "dba", "dba", "exec=\"ld_dir_all('/Users/akmi/eko-cmdi-test/rdf-output/', '*.rdf', 'http://eko.indarto/eko.rdf');\""};
		String[] str2 = {"/Users/akmi/zko.sh","/usr/local/Cellar/virtuoso/7.1.0","1111", "dba", "dba", "/Users/akmi/eko-cmdi-test/rdf-output"};
		
		StringBuffer output = new StringBuffer();
		Process process;
		try {
			process = Runtime.getRuntime().exec(str2);
			int x = process.waitFor();
			System.out.println("process..." + x);
//			int x=100;
//			while (x != 0) {
//				x = process.waitFor();
//				
//				System.out.println("process..." + x);
//				InputStream is = process.getInputStream();
//				if (is != null)
//				//System.out.println(getStringFromInputStream(is));
//				System.out.println("eko");
//			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}
			System.out.println(output.toString());
	 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getStringFromInputStream(InputStream is) {
		 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while (br != null && (line = br.readLine()) != null) {
				sb.append(line);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
 
		return sb.toString();
 
	}
}
