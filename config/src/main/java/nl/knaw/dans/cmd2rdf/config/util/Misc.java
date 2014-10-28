/**
 * 
 */
package nl.knaw.dans.cmd2rdf.config.util;

/**
 * @author Eko Indarto
 *
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.knaw.dans.cmd2rdf.config.xmlmapping.Property;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Misc {
	private static final Logger log = LoggerFactory.getLogger(Misc.class);
	private final static Pattern pattern = Pattern.compile("\\{(.*?)\\}");
	
	public static <T> Iterable<T> emptyIfNull(Iterable<T> iterable) {
	    return iterable == null ? Collections.<T>emptyList() : iterable;
	}
	
	public static String subtituteGlobalValue(Map<String, String> globalVars, String pVal) {
		log.debug("Subtitute global value to local variable.");
		Matcher m = pattern.matcher(pVal);
		if (m.find()) {
			String globalVar = m.group(1);
			if (globalVars.containsKey(globalVar)) {
				pVal = pVal.replace(m.group(0),
						globalVars.get(globalVar));
				log.debug("pVal contains global, pVal: "
						+ pVal);
			}
		}
		return pVal;
	}

	public static Map<String, String> mergeVariables(
			Map<String, String> globalVars, List<Property> localVars) {
		Map<String,String> vars = new HashMap<String,String>(globalVars);
		log.debug("Merge variables.");
		for (Property arg : Misc.emptyIfNull(localVars)) {
			String pName = arg.name;
			String pVal = arg.value;
			pVal = Misc.subtituteGlobalValue(globalVars, pVal);
			vars.put(pName, pVal);
		}
		return vars;
	}

	
	
	public static <T> List<List<T>> split(List<T> list, final int length) {
		List<List<T>> parts = new ArrayList<List<T>>();
		final int size = list.size();
		for (int i = 0; i < size; i += length) {
			parts.add(new ArrayList<T>(list.subList(i,
					Math.min(size, i + length))));
		}
		return parts;
	}
	
//	public static int safeLongToInt(long l) {
//	    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
//	        throw new IllegalArgumentException
//	            (l + " cannot be cast to int without changing its value.");
//	    }
//	    return (int) l;
//	}
}
