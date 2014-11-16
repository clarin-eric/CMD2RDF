package nl.knaw.dans.cmd2rdf.webapps.ui.secure.view;

import nl.knaw.dans.cmd2rdf.webapps.ui.secure.Cmd2RdfSecureApplication;

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
		
		Form<Void> form = new Form<Void>("form");
        add(form);
        final TextArea<String> confTextArea = new TextArea<String>("confTextArea", new Model<String>(Cmd2RdfSecureApplication.cofigReader.getRawXmlContent()));
        confTextArea.setEscapeModelStrings(false);
        form.add(confTextArea);
      
        
        final Label xmlLastMod = new Label("xmlLastMod", new Model<String>(Cmd2RdfSecureApplication.cofigReader.getConfigFileLastModifiedDate()));
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
