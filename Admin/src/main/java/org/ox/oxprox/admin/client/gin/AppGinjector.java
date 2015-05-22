package org.ox.oxprox.admin.client.gin;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
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

@GinModules(AppModule.class)
public interface AppGinjector extends Ginjector {

    public static final AppGinjector injector = GWT.create(AppGinjector.class);

    LoginPanel getLoginPanel();

    LoginService getLoginService();

    MainPanel getMainPanel();

    ClientDetailsPanel getClientDetailsPanel();

    OpDetailsPanel getOpDetailsPanel();

    EventBus getEventBus();

    ServiceAsync getService();

    OpTab getOpTab();

    ClientTab getClientTab();

    ScriptTab getScriptTab();
}
