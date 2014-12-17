package nl.knaw.dans.cmd2rdf.webapps.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import nl.knaw.dans.cmd2rdf.webapps.ui.pages.HomePage;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionPage extends Cmd2RdfBasePage {

	private static final long serialVersionUID = 593717625018028083L;
	private static Logger LOG = LoggerFactory.getLogger(ExceptionPage.class);
	// My model for the form data
	FormExceptionProvider formProvider;
	
	// Mapped wicket:id 
	private static final String wicketIdformSendException="formSendException";
	private static final String wicketIdformTarget="formTarget";
	private static final String wicketIdformComments="formComments";
	private static final String wicketIdmessageDetails="messageDetails";
	private static final String wicketIdsubmitForm="submitForm";

	public ExceptionPage(Exception e) {
		formProvider = new FormExceptionProvider();
		formProvider.setTarget("Administrator");
		formProvider.setComments("Please provide here any useful information...");
		StringBuilder sb = new StringBuilder();
		sb.append(e.getClass().toString()+"\n");
		// Get the full stacktrace from exception and append to the string
		// Thanks to ripper234 (ref: http://stackoverflow.com/questions/1292858/getting-full-string-stack-trace-including-inner-exception)
		sb.append(joinStackTrace(e));
		// Add fullstacktrace as a form's field (readonly)
		formProvider.setAdditional(sb.toString());
		add(makeExceptionForm());
	}

	private Form<?> makeExceptionForm() {

		Form<?> form = new Form<Void>(wicketIdformSendException);

		FormComponent<?> tfTarget = new TextField<String>(
				wicketIdformTarget, new PropertyModel<String>(
						formProvider, "target"));
		form.add(tfTarget);
	
		final FormComponent<?> taComments = new TextArea<String>(wicketIdformComments, new PropertyModel<String>(formProvider, "comments"));
		form.add(taComments);

		final FormComponent<?> taAdditional = new TextArea<String>(wicketIdmessageDetails, new PropertyModel<String>(formProvider, "additional"));
		form.add(taAdditional);

		form.add(new Button(wicketIdsubmitForm) {
			private static final long serialVersionUID = -3553700128326728526L;

			@Override
			public void onSubmit() {
//				LOG.debug(taComments.getDefaultModelObjectAsString());
//				LOG.debug(taAdditional.getDefaultModelObjectAsString());
				setResponsePage(HomePage.class);
			}
		});
		return form;
	}
	
	public static String joinStackTrace(Throwable e) {
	    StringWriter writer = null;
	    try {
	        writer = new StringWriter();
	        joinStackTrace(e, writer);
	        return writer.toString();
	    }
	    finally {
	        if (writer != null)
	            try {
	                writer.close();
	            } catch (IOException e1) {
	                // ignore
	            }
	    }
	}

	public static void joinStackTrace(Throwable e, StringWriter writer) {
	    PrintWriter printer = null;
	    try {
	        printer = new PrintWriter(writer);

	        while (e != null) {

	            printer.println(e);
	            StackTraceElement[] trace = e.getStackTrace();
	            for (int i = 0; i < trace.length; i++)
	                printer.println("\tat " + trace[i]);

	            e = e.getCause();
	            if (e != null)
	                printer.println("Caused by:\r\n");
	        }
	    }
	    finally {
	        if (printer != null)
	            printer.close();
	    }
	}
}