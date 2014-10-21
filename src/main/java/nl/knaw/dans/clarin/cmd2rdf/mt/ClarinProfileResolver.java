/**
 * 
 */
package nl.knaw.dans.clarin.cmd2rdf.mt;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import nl.knaw.dans.clarin.cmd2rdf.exception.ActionException;
import nl.knaw.dans.clarin.cmd2rdf.util.HttpConnectionManager;

import org.apache.commons.io.FileUtils;
import org.apache.directmemory.cache.CacheService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eko Indarto
 *
 */
public class ClarinProfileResolver implements URIResolver {
	private static final Logger errLog = LoggerFactory.getLogger("errorlog");
	private static final Logger log = LoggerFactory.getLogger(ClarinProfileResolver.class);
	private String basePath;
	private static CacheService<Object, Object> cacheService;
	private String registry;
	public ClarinProfileResolver(String basePath, String registry, CacheService<Object, Object> cacheService) throws ActionException {
		createCacheTempIfAbsent(basePath);
		this.registry = registry;
		this.basePath = basePath;
		ClarinProfileResolver.cacheService = cacheService;
	}

	private boolean createCacheTempIfAbsent(String basePath) throws ActionException {
		boolean success = false;
		File dir = new File(basePath);
		if (!dir.exists()) {
			success = dir.mkdir();
			if (!success)
				throw new ActionException("ERROR: Cannot create cache directory '" + basePath + "'.");
			else
				log.info("Cache directory is created: " + basePath);
		}
		return success;
	}

	  
	public Source resolve(String href,String base) throws TransformerException {
		if (href.contains("p_1360230992133")) {
			log.info("href: " + href);
			log.info("href size: " + href.length());
			try {
				String decoded = URLDecoder.decode(href, "UTF-8");
				log.info("decode: " + decoded);
				log.info("decode size: " + decoded.length());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		log.debug("Profile URI: " + href);
		log.debug("xsl base: " + base);
		String filename = null;
		File file = null;
		Object oHref =  getHrefAsURLorFile(href);
		if (oHref instanceof URL) {
			filename = href.replace(registry+"/rest/registry/profiles/", "");//TODO: Don't use hard coded!!!
			filename = filename.replace("/xml", ".xml");
			filename = filename.replace(":", "_");
			file = new File(basePath + "/" + filename);
		} else {
			file = (File)oHref;
			filename = file.getAbsolutePath();
		}
		log.debug("resolve: CacheService contains " + cacheService.entries() + " items.");
		System.out.println("===== Filename: " + filename);
	    if (cacheService.retrieveByteArray(filename) != null) {
	    	InputStream is = null;
	    	log.debug("Using profile from the cache service. Key: " + filename + "\tValue: " + href);
	    	synchronized (cacheService) {
	    		byte b[] = (byte[]) cacheService.retrieveByteArray(filename);
		    	is = new ByteArrayInputStream(b);
			}
	    	
	    	 return new StreamSource(is);
	    } else {
	    	log.debug(filename + " is not in the cache service.");
	    	if (file.exists()) {
		    	return loadFromFile(filename, file);
		    } else {
		    	return fetchAndWriteToCache(href, filename);
		    }
	    }
	}

	private StreamSource fetchAndWriteToCache(String href, String filename) {
		log.debug("Download profile '" + filename + "' from registry. HREF: " + href);
		final ReadWriteLock rwl = new ReentrantReadWriteLock();
		try {
			if (href.contains("p_1360230992133")) {
				log.debug(">>>>>>>>>> THIS PART is BAD!!!");;
				href = "http://catalog.clarin.eu/ds/ComponentRegistry/rest/registry/profiles/clarin.eu:cr1:p_1360230992133/xml";
				filename = "clarin.eu_cr1_p_1360230992133.xml";
			}
			
			byte b[] = readHref(href);
			if (b == null) {
				errLog.debug("ERROR while reading " + href + ". It contains null value.");
			} else {
				InputStream is = new ByteArrayInputStream(b);
				cacheService.putByteArray(filename, b);
				log.debug(cacheService.entries()
						+ " put to cache service and save it as file: " + filename);
				rwl.writeLock().lock();
				FileUtils.writeByteArrayToFile(new File(basePath + "/" + filename),
						b);
			
			return new StreamSource(is);
			}
		} catch (IOException e) {
			log.error("ERROR: Caused by IOException, msg: " + e.getMessage());
			e.printStackTrace();
		} finally {
			rwl.writeLock().unlock(); // Unlock write
			log.debug("fetchAndWriteToCache: Adding new item to CacheService. Now it contains " + cacheService.entries() + " items.");
		}
		return null;
	}
	
	private byte[] readHref(String href) throws IOException {
		Object oHref = getHrefAsURLorFile(href);
		if (oHref == null) {
			log.error("FATAL ERROR '" + href + "' is NULL.");
		} else if (oHref instanceof URL) {
			HttpRequestBase base = new HttpGet(oHref.toString());
		    HttpResponse response = HttpConnectionManager.execute(base);
		    HttpEntity entity = response.getEntity();
		    
		    byte[] b = EntityUtils.toByteArray(entity);
			return b;
		} else if (oHref instanceof File) {
			File f = (File)oHref;
			byte[] b = FileUtils.readFileToByteArray(f);
			return b;
		}
		return null;
	}
	
	private Object getHrefAsURLorFile(String href) {
		Object o = null;
		try {
			o = new URL(href);
			log.debug(href + " is an URL.");
		} catch (MalformedURLException e) {
			log.info("'" + href + "' is NOT URL.");
		} 
		
		if (o == null) {
			log.debug(href + " is a FILE.");
			o = new File(href);
		} 
			
		return o;
	}

	private StreamSource loadFromFile(String filename, File file) {
		StreamSource stream = null;
		final ReadWriteLock rwl = new ReentrantReadWriteLock();
		rwl.readLock().lock();
		log.debug("Read cache from file and put in the cache service. Filename:  " + filename + "\tFile abspath: " + file.getAbsolutePath());
		
		try {
			byte[] bytes = FileUtils.readFileToByteArray(file);
			cacheService.putByteArray(filename, bytes);
//			Map<Object, Pointer<Object>> map = cacheService.getMap();
//			Set<Object> set = map.keySet();
//			int x=0;
//			for (Iterator<Object> i= set.iterator(); i.hasNext();) {
//				log.debug("###[ "+ x++ + "]: " + i.next());
//			}
			InputStream is = new ByteArrayInputStream(bytes);
			stream = new StreamSource(is);
			return stream; 
		} catch (IOException e) {
			log.error("FATAL ERROR: could not put the profile (filename: '" + filename + "') to the cache. Caused by IOException, msg: " + e.getMessage());
		}  finally {
		      rwl.readLock().unlock(); //Unlock read
		      log.debug("loadFromFile: Adding new item to CacheService. Now it contains " + cacheService.entries() + " items.");
		}
		return stream;
	}

}
