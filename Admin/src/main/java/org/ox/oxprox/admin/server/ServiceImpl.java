package org.ox.oxprox.admin.server;

import com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.ox.oxprox.admin.client.service.Service;
import org.ox.oxprox.admin.shared.Client;
import org.ox.oxprox.admin.shared.OP;
import org.ox.oxprox.admin.shared.User;
import org.ox.oxprox.service.ClientService;
import org.ox.oxprox.service.OpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 16/06/2014
 */
@Singleton
public class ServiceImpl extends RemoteServiceServlet implements Service {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceImpl.class);

    @Inject
    OpService opService;
    @Inject
    ClientService clientService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    public User login(String username, String password) {
        if (username.equals("test") && password.equals("test")) {
            return new User("test", "test");
        }
        return null;
    }

    @Override
    public List<OP> getOpList() {
        try {
            return Convertor.convertOps(opService.getAllOps());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return Lists.newArrayList();
    }

    @Override
    public List<Client> getClientList() {
        return Convertor.convertClients(clientService.getAllClients());
    }
}
