package nl.knaw.dans.cmd2rdf.conversion.action;

/**
 * @author Eko Indarto
 *
 */

import java.util.Map;

public interface IAction {
	public void startUp(Map<String, String> vars) throws ActionException;
	public Object execute(String path,Object object) throws ActionException;
	public void shutDown() throws ActionException;
	public String name();
}
