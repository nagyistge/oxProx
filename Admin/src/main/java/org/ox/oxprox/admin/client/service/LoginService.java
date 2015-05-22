package org.ox.oxprox.admin.client.service;

import org.ox.oxprox.admin.shared.User;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 16/06/2014
 */

public class LoginService {

    private User loggedinUser;

    public boolean isLoggedIn() {
        return loggedinUser != null;
    }

    public User getLoggedinUser() {
        return loggedinUser;
    }

    public void setLoggedinUser(User loggedinUser) {
        this.loggedinUser = loggedinUser;
    }
}
