package org.ox.oxprox.admin.shared;

import java.io.Serializable;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 17/06/2014
 */

public class User implements Serializable {

    private String login;
    private String fullname;

    public User() {
    }

    public User(String login, String fullname) {
        this.login = login;
        this.fullname = fullname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
