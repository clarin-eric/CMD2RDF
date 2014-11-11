package nl.knaw.dans.cmd2rdf.webapps.ui.service;

import java.util.List;

import nl.knaw.dans.cmd2rdf.webapps.ui.secure.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class UserService {
	
	private static Logger LOG = LoggerFactory.getLogger(UserService.class);
    private static List<User> usersInDatabase = Lists.newArrayList(
            new User("Akmi", "akmi"),
            new User("DANS", "dans"),
            new User("admin", "admin"),
            new User("KNAW", "1nst1tut3"));


    public User findByLoginAndPassword(String login, String password) {

        for (User user : usersInDatabase) {
            if(user.getPassword().equals(password) & user.getLogin().equals(login)) {
            	LOG.debug("#### " + user.getLogin() + " ####");
                return user;
            }
        }
        return null;
    }
}
