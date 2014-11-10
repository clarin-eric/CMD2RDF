package nl.knaw.dans.cmd2rdf.webui.secure.model;

import java.io.Serializable;

public class User implements Serializable {

    private String login;
    private String password;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
