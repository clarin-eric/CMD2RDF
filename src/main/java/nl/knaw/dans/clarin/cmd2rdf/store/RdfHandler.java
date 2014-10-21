package nl.knaw.dans.clarin.cmd2rdf.store;


public interface RdfHandler {
	public boolean save(byte[] bytes, String uri);
	public boolean delete (String filename);
}
