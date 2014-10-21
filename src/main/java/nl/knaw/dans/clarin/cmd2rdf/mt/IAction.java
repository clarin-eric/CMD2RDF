package nl.knaw.dans.clarin.cmd2rdf.mt;

/**
 * @author Eko Indarto
 *
 */

import java.util.Map;

import nl.knaw.dans.clarin.cmd2rdf.exception.ActionException;

public interface IAction {
	public void startUp(Map<String, String> vars) throws ActionException;
	public Object execute(String path,Object object) throws ActionException;
	public void shutDown() throws ActionException;
	public String name();
}
