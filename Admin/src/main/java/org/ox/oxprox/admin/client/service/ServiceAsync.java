package org.ox.oxprox.admin.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.ox.oxprox.admin.shared.Client;
import org.ox.oxprox.admin.shared.OP;
import org.ox.oxprox.admin.shared.User;

import java.util.List;

public interface ServiceAsync {

    void login(String value, String value1, AsyncCallback<User> async);

    void getOpList(AsyncCallback<List<OP>> async);

    void getClientList(AsyncCallback<List<Client>> async);
}
