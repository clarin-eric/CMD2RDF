package nl.knaw.dans.cmd2rdf.batch;

/**
 * @author Eko Indarto
 *
 */

import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.knaw.dans.cmd2rdf.config.xmlmapping.Action;
import nl.knaw.dans.cmd2rdf.config.xmlmapping.Config;
import nl.knaw.dans.cmd2rdf.config.xmlmapping.Jobs;
import nl.knaw.dans.cmd2rdf.config.xmlmapping.Profile;
import nl.knaw.dans.cmd2rdf.config.xmlmapping.Property;
import nl.knaw.dans.cmd2rdf.config.xmlmapping.Record;
import nl.knaw.dans.cmd2rdf.conversion.action.ActionException;
import nl.knaw.dans.cmd2rdf.conversion.action.ActionStatus;
import nl.knaw.dans.cmd2rdf.conversion.action.IAction;
import nl.knaw.dans.cmd2rdf.conversion.action.WorkerCallable;
import nl.knaw.dans.cmd2rdf.conversion.db.ChecksumDb;
import nl.knaw.dans.cmd2rdf.conversion.util.Misc;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.directmemory.DirectMemory;
import org.apache.directmemory.cache.CacheService;
import org.easybatch.core.api.AbstractRecordProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class JobProcessor  extends AbstractRecordProcessor<Jobs> {
	private static final Logger log = LoggerFactory.getLogger(JobProcessor.class);
	private final Pattern pattern = Pattern.compile("\\{(.*?)\\}");
	private static final String URL_DB = "urlDB";
	private static volatile Map<String, String> GLOBAL_VARS = new LinkedHashMap<String, String>();
	private static volatile CacheService<Object, Object> cacheService;
	private static int TOTAL_NUM_PROCESSED_PATHS;
	

	public void processRecord(Jobs job)
			throws Exception {
		setupGlolbalConfiguration(job);
		initiateCacheService();
		doPrepare(job.getPrepare().getActions());
		doProcessRecord(job.records);
		doCleanup(job.getCleanup().getActions());
		doProcessProfile(job.profiles);
		doProcessComponent(job.components);
		closeCacheService();
		log.info("TOTAL NUMBER OF PROCESSED PATHS: " + TOTAL_NUM_PROCESSED_PATHS);
	}
	private void doProcessComponent(List<Profile> components) throws ClassNotFoundException, InstantiationException,
	IllegalAccessException, NoSuchFieldException,
	NoSuchMethodException, InvocationTargetException, ActionException {
		for (Profile component:Misc.emptyIfNull(components)) {
			log.info("###### PROCESSING OF Components : " + component.getDesc());
			Collection<File> files = FileUtils.listFiles(new File( Misc.subtituteGlobalValue(GLOBAL_VARS, component.getXmlSource()))
														,  new WildcardFileFilter(component.getFilter()), null);
			List<IAction> actions = new ArrayList<IAction>();
			List<Action> list = component.getActions();
			for (Action act : list) {
				IAction clazzAction = startUpAction(act);				
				actions.add(clazzAction);
			}
			
			for (File f:files) {
				Object o = f;
				for(IAction action : actions) {
					 o =action.execute(f.getAbsolutePath(), o);
				}
			}
	         
			for(IAction action : actions) {
				action.shutDown();
			}
		}
		
	}
	private void doProcessProfile(List<Profile> profiles) throws ClassNotFoundException, InstantiationException,
	IllegalAccessException, NoSuchFieldException,
	NoSuchMethodException, InvocationTargetException, ActionException {
		for (Profile profile:Misc.emptyIfNull(profiles)) {
			log.info("###### PROCESSING OF Profiles : " + profile.getDesc());
			Collection<File> files = FileUtils.listFiles(new File( Misc.subtituteGlobalValue(GLOBAL_VARS, profile.getXmlSource()))
														,  new WildcardFileFilter(profile.getFilter()), null);
			List<IAction> actions = new ArrayList<IAction>();
			List<Action> list = profile.getActions();
			for (Action act : list) {
				IAction clazzAction = startUpAction(act);				
				actions.add(clazzAction);
			}
			
			for (File f:files) {
				Object o = f;
				for(IAction action : actions) {
					 o =action.execute(f.getAbsolutePath(), o);
				}
			}
	         
			for(IAction action : actions) {
				action.shutDown();
			}
		}
		
	}
	private void setupGlolbalConfiguration(Jobs job)
			throws IntrospectionException, 
					IllegalAccessException,
					InvocationTargetException {
		log.info("Setup the global configuration");
		Config c = job.getConfig();
		List<Property> props = c.getProperty();
		for (Property prop:props) {
			GLOBAL_VARS.put(prop.name, prop.value);
		}
		//iterate through map, find whether map values contain {val}
		for (Map.Entry<String, String> e : GLOBAL_VARS.entrySet()) {
			String pVal = e.getValue();
			Matcher m = pattern.matcher(pVal);
			if (m.find()) {
				String globalVar = m.group(1);
				if (GLOBAL_VARS.containsKey(globalVar)) {
					log.info("pKey: " + e.getKey() + "\tpVal: " + pVal);
					pVal = pVal.replace(m.group(0), GLOBAL_VARS.get(globalVar));
					log.info("pKey: " + e.getKey() + "\tnew pVal: " + pVal);
					GLOBAL_VARS.put(e.getKey(), pVal);
				}
			}
		}
	}
	
	private void doPrepare(List<Action> list) 
			throws ClassNotFoundException,
					InstantiationException, IllegalAccessException,
					NoSuchFieldException, NoSuchMethodException,
					InvocationTargetException, ActionException {
		log.info("Execute prepare actions.");
		List<IAction> actions = new ArrayList<IAction>();
		for (Action act : list) {
			IAction clazzAction = startUpAction(act);				
			actions.add(clazzAction);
		}
		for(IAction action : actions) {
			action.execute(null,null);
		}
		for(IAction action : actions) {
			action.shutDown();
		}
	}
	
	private void doProcessRecord(List<Record> records)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, NoSuchFieldException,
			NoSuchMethodException, InvocationTargetException, ActionException {
		log.info("Execute records.");	
		
		fillInCacheService();
		
		for(Record r: Misc.emptyIfNull(records)) {
			log.info("###### PROCESSING OF RECORD : " + r.getDesc() + "\tNumber of threads: " + r.getnThreads());
			List<List<String>> allPaths = new ArrayList<List<String>>();
			
			if (r.getXmlSource().contains(URL_DB)) {
				String urlDB = subtituteGlobalValue(r.getXmlSource());
				ChecksumDb cdb = new ChecksumDb(urlDB);
				if (r.getXmlLimitSizeMin() != null) {
					try {
						Integer.parseInt(r.getXmlLimitSizeMin());
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException("ERROR: xmlLimitSizeMin value is not integer. " + e.getMessage());
					}
				}
					
				if (r.getXmlLimitSizeMax() != null) {
					try {
						Integer.parseInt(r.getXmlLimitSizeMax());
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException("ERROR: xmlLimitSizeMax value is not integer. " + e.getMessage());
					}
				}
				
				if (r.getXmlLimitSizeMax() == null) {
					if (!Misc.convertToActionStatus(r.getFilter()).equals(ActionStatus.NEW_UPDATE))
						throw new ActionException("ERROR: NOT IMPLEMENT YET");
					Map<String, Integer> pathsAndSizes = cdb.getRecords(Misc.convertToActionStatus(r.getFilter()), r.getXmlLimitSizeMin());
					//List<String> pathsbigzise = new ArrayList<String>();
					List<String> mbpaths = new ArrayList<String>();
					int mB10 = 0;
					int count = 0;
					for (Map.Entry<String, Integer> e : pathsAndSizes.entrySet()) {
						int size = e.getValue();
						if (size > 10485760) {
							List<String> alist = new ArrayList<String>();
							alist.add(e.getKey());
							allPaths.add(alist);
							count++;
						} else {
							//collect the files until the total size about 10 MB or number of files: 50 
							mB10 += size;
							mbpaths.add(e.getKey());
							count++;
						}
						if ((mB10 > 26214400) || (mbpaths.size() > 50)) {
							//reset he files until the total size about 10 MB or number of files: 50 
							mB10 = 0;
							allPaths.add(mbpaths);
							mbpaths = new ArrayList<String>();
						}
					}
					
					//check whether the total number of paths are correct
					if (pathsAndSizes.size() != count) {
							log.error("========== ERRORS ========");
							log.error("ERROR - The pathsAndSizes: " + pathsAndSizes + "\tcount: " + count);
							throw new ActionException("FATAL Errors: The pathsAndSizes: " + pathsAndSizes + "\tcount: " + count);
					}
					
					if (!mbpaths.isEmpty())
						allPaths.add(mbpaths);
					
				} else {
					List<String> paths = cdb.getRecords(Misc.convertToActionStatus(r.getFilter()), r.getXmlLimitSizeMin(), r.getXmlLimitSizeMax());
					allPaths.add(paths);
				}
		    	
		    	cdb.closeDbConnection();
			}
			if (allPaths != null && !allPaths.isEmpty()) {
				for (List<String> paths : allPaths) {
					log.info(">>>>> Number of processing files: " + paths.size() );
					TOTAL_NUM_PROCESSED_PATHS+=paths.size();
					executeRecords(r, paths);
				}
			
			} else {
				//Skip record
				log.info("###### SKIPPED: '" +  r.getDesc() + "'");
			}
		}
	}
	private void executeRecords(Record r, List<String> paths)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, NoSuchFieldException,
			NoSuchMethodException, InvocationTargetException, ActionException {
		List<IAction> actions = new ArrayList<IAction>();
		List<Action> list = r.getActions();
		//Start Action
		for (Action act : list) {
			IAction clazzAction = startUpAction(act);				
			actions.add(clazzAction);
		}
		
		//Execute Action
		if (r.getnThreads()>0) 
			doCallableAction(r, paths, actions);
		else {
			for(IAction action : actions) {
				for (String path:paths)
					action.execute(path,null);
			}
		}
		 
		//Shutdown Action
		for(IAction action : actions) {
			action.shutDown();
		}
		if (r.getCleanup() != null && r.getCleanup().getActions() != null)
			doCleanup(r.getCleanup().getActions());
	}
	
	
	private void doCallableAction(Record r, List<String> paths,
			List<IAction> actions) {
		log.info("Multithreading is on, number of threads: " + r.getnThreads());
		int n=0;
		int i=0;
		List<List<String>> paths2 = Misc.split(paths, r.getnThreads());
		int totalfiles = paths.size();
		log.info("==============================================================");
		log.info("Numbers of files: " + totalfiles);
		log.info("Numbers op splitsing: " + paths2.size());
		log.info("==============================================================");
		for (List<String> pth : paths2) {
			n++;
			ExecutorService executor = Executors.newCachedThreadPool();
			log.info(">>>>>>>>>>>>>[" + n + "]Number of processed records files: " + pth.size() );
			//List<Future<String>> futures = new ArrayList<Future<String>>();
			for (String p : pth) {
				i++;
				 Callable<String> worker = new WorkerCallable(p, actions, i);
				 executor.submit(worker);
//			     Future<String> future = executor.submit(worker);
//			     futures.add(future);
//			     try {
//					log.debug(future.get());
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (ExecutionException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
//			log.debug("============ FUTURE SIZE: " + futures.size() + "\tPATHS SIZE: " + paths.size() );
//			for (Future<String> future:futures) {
//				try {
//					log.debug("### " + future.get() );
//				} catch (InterruptedException   e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (ExecutionException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
			executor.shutdown();
			while (!executor.isTerminated()) {}
			log.info("===Finished "+ pth.size() + " threads===");
			totalfiles=totalfiles-pth.size();
			log.info("REMAINS: " + totalfiles);
		}
	}
	
	
	
	
	private void closeCacheService() {
		log.info("closeCacheService: CLOSE CacheService. It contains " + cacheService.entries() + " items.");
		cacheService.clear();
        try {
			cacheService.close();
		} catch (IOException e) {
			log.error("ERROR caused by IOException, msg:  " + e.getMessage());
		}
	}
	private void fillInCacheService() {
		log.info("fillInCacheService");
		String profilesCacheDir = GLOBAL_VARS.get("profilesCacheDir");
		 Collection<File> profiles = FileUtils.listFiles(new File(profilesCacheDir),new String[] {"xml"}, true);
		 for (File profile:profiles) {
			 loadFromFile(profile.getName(), profile);
		 }
		 
	}
	private void initiateCacheService() {
		 cacheService = new DirectMemory<Object, Object>()
				    .setNumberOfBuffers( 100 )
				    .setSize( 15000000 )
				    .setInitialCapacity( 10000 )
				    .setConcurrencyLevel( 4 )
				    .newCacheService();
		 cacheService.scheduleDisposalEvery(30,TimeUnit.MINUTES);
		 
		 
	}
	
	private void loadFromFile(String key, File file) {
		//log.debug("Load " + key + " to Cache Service. It contains " + cacheService.entries() + " items.");
		//log.debug("Read cache from file and put in the cache service. Key:  " + key + "\tFile abspath: " + file.getAbsolutePath());
		try {
			byte[] bytes = FileUtils.readFileToByteArray(file);
			cacheService.putByteArray(key, bytes);
		} catch (IOException e) {
			log.error("FATAL ERROR: could not put the profile (key: '" + key + "') to the cache. Caused by IOException, msg: " + e.getMessage());
		}  
		//log.debug("loadFromFile: Adding new item to CacheService. Now it contains " + cacheService.entries() + " items.");
	} 
	
	
	private void doCleanup(List<Action> list) 
				throws ClassNotFoundException, InstantiationException, 
					IllegalAccessException, NoSuchFieldException, 
					NoSuchMethodException, InvocationTargetException, ActionException {
		log.info("Execute cleanup part.");	
		List<IAction> actions = new ArrayList<IAction>();
		for (Action act : list) {
			log.debug(act.getName());
			IAction clazzAction = startUpAction(act);		
			if (clazzAction == null)
				log.error("FATAL ERROR: " + act.getName() + " is null.");
			else 
				actions.add(clazzAction);
		}
		Object o = null;
		for(IAction action : actions) {
			
			 o =action.execute(null, o);
		}
		for(IAction action : actions) {
			action.shutDown();
		}
	}

	
	@SuppressWarnings("rawtypes")
	private IAction startUpAction(Action act)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, NoSuchFieldException,
			NoSuchMethodException, InvocationTargetException, ActionException {
		log.info("Startup of " + act.getClazz().getName());
		log.info("Description: " + act.getName());
		@SuppressWarnings("unchecked")
		Class<IAction> clazz = (Class<IAction>) Class.forName(act.getClazz().getName());
		Constructor[] constructors = clazz.getConstructors(); 
		for (Constructor c:constructors) {
			Class[] parameterTypes = c.getParameterTypes();
			if (parameterTypes.length == 0) {
				IAction clazzAction = clazz.newInstance();
				clazzAction.startUp(nl.knaw.dans.cmd2rdf.config.util.Misc.mergeVariables(JobProcessor.GLOBAL_VARS,act.getClazz().getProperty()));
				return clazzAction;
			} else if (parameterTypes.length == 1 && (parameterTypes[0].isInstance(cacheService))) {
				log.info("USING CACHE SERVICE - hashcode: " + cacheService.hashCode() + " ENTRIES: " + cacheService.entries());
				Constructor<IAction> ctor = clazz.getDeclaredConstructor(CacheService.class);
			    ctor.setAccessible(true);
			    IAction clazzAction = ctor.newInstance(cacheService);
				clazzAction.startUp(nl.knaw.dans.cmd2rdf.config.util.Misc.mergeVariables(JobProcessor.GLOBAL_VARS,act.getClazz().getProperty()));
				return clazzAction;
				
			}
		}
		return null;
	}

	private String subtituteGlobalValue(String pVal) {
		Matcher m = pattern.matcher(pVal);
		if (m.find()) {
			String globalVar = m.group(1);
			if (GLOBAL_VARS.containsKey(globalVar)) {
				pVal = pVal.replace(m.group(0),
						GLOBAL_VARS.get(globalVar));
			}
		}
		return pVal;
	}
}