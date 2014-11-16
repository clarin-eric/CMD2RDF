package nl.knaw.dans.cmd2rdf.webapps.ui.secure.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import nl.knaw.dans.cmd2rdf.webapps.rest.sparql.JerseyGetClient;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * @author akmi
 * 
 */
public class TabStatisticsPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1973574682018245001L;

	public TabStatisticsPanel(String id, final String cmd2rdfHome) {
		super(id);
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
		add(new Label("now", sdf.format(new Date())));
		
		JerseyGetClient jgc = new JerseyGetClient();
		add(new Label("totalrecords", jgc.getNumberOfTotalRecords()));
		add(new Label("rdfrecords", jgc.getNumberRDFRecords()));
		
		
	}
}
