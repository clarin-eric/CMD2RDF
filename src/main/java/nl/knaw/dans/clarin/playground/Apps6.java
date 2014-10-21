package nl.knaw.dans.clarin.playground;

import nl.knaw.dans.clarin.cmd2rdf.exception.ActionException;
import nl.knaw.dans.clarin.cmd2rdf.util.ActionStatus;
import nl.knaw.dans.clarin.cmd2rdf.util.Misc;

public class Apps6 {

	public static void main(String[] args) {
		ActionStatus s;
		try {
			s = Misc.convertToActionStatus("NEW      UPDATE    ");
			System.out.println(s.name());
		} catch (ActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String word = "NEW     UPDATE dengan cara seperti itu     ys l    ";
	      String[] words=word.trim().split(" ");
	      System.out.println(words.length);
	      StringBuffer sb = new StringBuffer();
	      for (int i=0; i<words.length; i++) {
	    	  if (words[i].length() > 0){
	    		  sb.append(words[i]);
	    		  if (i<words.length-1)
		    		  sb.append("_");
	    	  }
	    	  
	    		  
	      }
	     System.out.println(sb.length());
	     System.out.println(sb.toString().trim().length());
	     System.out.println(sb.toString());
	}

}
