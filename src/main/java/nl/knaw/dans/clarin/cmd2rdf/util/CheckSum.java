package nl.knaw.dans.clarin.cmd2rdf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
 
public class CheckSum {
 
  public static Map<String, String> digest(String basefolder, Collection<File> listFiles) throws IOException{
	  Map<String, String> digestedFiles = new LinkedHashMap<String, String>();
	  for (File file : listFiles) {
			String relativePath = file.getAbsolutePath().replace(basefolder, "");
			InputStream is = new FileInputStream(file);
		    digestedFiles.put(relativePath, DigestUtils.md5Hex(is));
		}
	
    return digestedFiles;
  }
}
 