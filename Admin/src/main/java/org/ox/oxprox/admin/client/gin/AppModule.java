package org.ox.oxprox.admin.client.gin;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import org.ox.oxprox.admin.client.service.LoginService;
import org.ox.oxprox.admin.client.service.ServiceAsync;
import org.ox.oxprox.admin.client.ui.ClientDetailsPanel;
import org.ox.oxprox.admin.client.ui.ClientTab;
import org.ox.oxprox.admin.client.ui.LoginPanel;
import org.ox.oxprox.admin.client.ui.MainPanel;
import org.ox.oxprox.admin.client.ui.OpDetailsPanel;
import org.ox.oxprox.admin.client.ui.OpTab;
import org.ox.oxprox.admin.client.ui.ScriptTab;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 17/06/2014
 */

public class AppModule extends AbstractGinModule {

    protected void configure() {
        bind(LoginPanel.class).in(Singleton.class);
        bind(LoginService.class).in(Singleton.class);
        bind(MainPanel.class).in(Singleton.class);
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(ServiceAsync.class).in(Singleton.class);
        bind(OpTab.class).in(Singleton.class);
        bind(ClientTab.class).in(Singleton.class);
        bind(ScriptTab.class).in(Singleton.class);
        bind(ClientDetailsPanel.class).in(Singleton.class);
        bind(OpDetailsPanel.class).in(Singleton.class);
    }
}
