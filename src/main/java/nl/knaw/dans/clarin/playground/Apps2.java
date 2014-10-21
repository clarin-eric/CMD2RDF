package nl.knaw.dans.clarin.playground;

import java.math.BigInteger;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.knaw.dans.clarin.cmd2rdf.util.HttpConnectionManager;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.javasimon.Stopwatch;
import org.joda.time.Period;

//import virtuoso.jena.driver.VirtuosoUpdateFactory;
//import virtuoso.jena.driver.VirtuosoUpdateRequest;

public class Apps2 {

	private static final int MAX_LINE = 10;
	
	public static void main(String[] args) {
		// get us some stopwatch Simon
		Stopwatch stopwatch = SimonManager.getStopwatch(Apps2.class.getName()+".eko-indarto");
		Split split = stopwatch.start(); // start the stopwatch
		System.out.println("Hello world, " + stopwatch); // print it

		String s = "Hello,I am a good person, yes I am,No I'am not ,  oh ya ? , ,  oh ya ? ,  oh ya ? ,  oh ya ? ";
		String ss[] = s.split(",");
		System.out.println(ss.length);
		String s2 = "Hello,I am a good person, yes I am,No I'am not ,  oh ya ? , ,  oh ya ? ,  oh ya ? ,  oh ya ? ";
		String ss2[] = s2.split(",");
		Collection<String> cl = new ArrayList<String>();
		int b=0;
		for (String sss : ss) {
			System.out.println(sss + "\tlength: " + sss.length());
			System.out.println(sss + "\tlength: " + sss.trim().length());
			if(!sss.trim().isEmpty());
				cl.add(sss.trim());
				
			split.setAttribute(sss, b++);
		}
		
		System.out.println("======= size: " + cl.size());
		
		List<String> ll = new ArrayList<String>();
		for (String sss2:ss2) {
			if (!sss2.trim().isEmpty())
				ll.add(sss2);
		}
		System.out.println("------- size: " + ll.size());
		cl.addAll(ll);
		
		System.out.println(">>>>>>> " + cl.size());
		
		List<String> list = new ArrayList<String>();
		list.add("one");
		list.add("two");
		list.add("three");
		list.add("four");
		list.add("five");
		list.add("six");
		list.add("seven");
		list.add("eight");
		list.add("nine");
		list.add("ten");
		list.add("eleven");
		
		int max = list.size();
		int numberOfElements = 3;
		
		List<List<String>> subSets = ListUtils.partition(list, numberOfElements);
		
		int i=0;
		for (List<String> l:subSets) {
			i++;
			System.out.println("size of : i[" + i + "]: " + l.size());
		} 
		
		
		String x = "EKO INDARTO";
		String[] y = x.split(",");
		System.out.println("y length: " + y.length);
		
		
		 Period period = new Period(3667023);
	     System.out.println("Seconds: " + period.getSeconds());
	     System.out.println("Hour: " + period.getHours());
	     System.out.println("Min: " + period.getMinutes());
	     System.out.println("Milis: " + period.getMillis());
	     
	     
	     int len = 34;
         int x1 = Math.round(len/MAX_LINE);
         int y1 = len%MAX_LINE;
         int z1 = x1*MAX_LINE;
         for (int k=0; k<x1; k++) {
       	  for (int j=0; j<MAX_LINE; j++) {
       		  System.out.println("k*j: " + ((k*MAX_LINE) + j));
       	  }
       	  
         }
         System.out.println("======");
         if (y1 > 0) {
             for (int k=z1+1; k<z1+y1; k++) {
           	 System.out.println("z: " + k);
             } 
         }
         split.stop(); // stop it

         System.out.println("Result: " + stopwatch); // here we print our stopwatch again
         
         String ssssss = FileUtils.byteCountToDisplaySize(BigInteger.valueOf(4*1024));
         System.out.println(ssssss);
         
	}
	
	 private class Partition<T> extends AbstractList<List<T>> {

		    final List<T> list;
		    final int numberOfElements;

		    Partition(List<T> list, int numberOfElements) {
		      this.list = list;
		      this.numberOfElements = numberOfElements;
		    }

		    @Override
		    public List<T> get(int index) {
		      int listSize = size();
		      if (listSize < 0)
		        throw new IllegalArgumentException("negative size: " + listSize);
		      if (index < 0)
		        throw new IndexOutOfBoundsException(
		            "index " + index + " must not be negative");
		      if (index >= listSize)
		        throw new IndexOutOfBoundsException(
		            "index " + index + " must be less than size " + listSize);
		      int start = index * numberOfElements;
		      int end = Math.min(start + numberOfElements, list.size());
		      return list.subList(start, end);
		    }

		    @Override
		    public int size() {
		      return (list.size() + numberOfElements - 1) / numberOfElements;
		    }

		    @Override
		    public boolean isEmpty() {
		      return list.isEmpty();
		    }
		    
		   
		    
		    
		  }

}
