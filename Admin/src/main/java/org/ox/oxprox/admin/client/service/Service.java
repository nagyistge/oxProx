package org.ox.oxprox.admin.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.ox.oxprox.admin.shared.Client;
import org.ox.oxprox.admin.shared.OP;
import org.ox.oxprox.admin.shared.User;

import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 16/06/2014
 */

@RemoteServiceRelativePath("oxproxadmin")
public interface Service extends RemoteService {

    User login(String username, String password);

    List<OP> getOpList();

    List<Client> getClientList();
}
