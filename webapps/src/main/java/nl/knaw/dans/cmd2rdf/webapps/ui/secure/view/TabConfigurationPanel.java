package nl.knaw.dans.cmd2rdf.webapps.ui.secure.view;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import nl.knaw.dans.cmd2rdf.webapps.util.Misc;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * @author akmi
 * 
 */
public class TabConfigurationPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1973574682018245001L;

	public TabConfigurationPanel(String id, final String cmd2rdfHome) {
		super(id);
		String xmlConfig = "";
		String fileLastMod="";
		File file = new File(Misc.getEnvValue("job_xml_path"));
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
		 
		fileLastMod = sdf.format(file.lastModified());
		try {
			xmlConfig = FileUtils.readFileToString(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		
		Form<Void> form = new Form<Void>("form");
        add(form);
        final TextArea<String> confTextArea = new TextArea<String>("confTextArea", new Model<String>(xmlConfig));
        confTextArea.setEscapeModelStrings(false);
        form.add(confTextArea);
      
        
        final Label xmlLastMod = new Label("xmlLastMod", new Model<String>(fileLastMod));
        xmlLastMod.setOutputMarkupId(true);
        form.add(xmlLastMod);
        
        final Label errorMessage = new Label("errorMessage", new Model<String>(""));
        errorMessage.setOutputMarkupId(true);
        form.add(errorMessage);
        
     // add a button that can be used to submit the form via ajax
        form.add(new AjaxButton("ajax-button", form)
        {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
            	
            	String xmlConfTextArea= confTextArea.getDefaultModelObjectAsString();
            	
            	
            	errorMessage.setDefaultModelObject("Saved is successfull!");
            	
            	target.add(errorMessage);
            	
            	
            	xmlLastMod.setDefaultModelObject("indarto");
            	target.add(xmlLastMod);
            	
            }

        }.setEnabled(false));

	}

}
