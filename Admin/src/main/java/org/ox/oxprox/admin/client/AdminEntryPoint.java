package org.ox.oxprox.admin.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.Viewport;
import org.ox.oxprox.admin.client.event.LoggedinEvent;
import org.ox.oxprox.admin.client.gin.AppGinjector;
import org.ox.oxprox.admin.client.service.LoginService;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 16/06/2014
 */

public class AdminEntryPoint implements EntryPoint {

    private static final SimpleContainer CONTAINER = new SimpleContainer();

    @Override
    public void onModuleLoad() {
        init();

        final Viewport viewport = new Viewport();
        viewport.add(CONTAINER, new MarginData(0));
        RootLayoutPanel.get().add(viewport);
    }

    private void init() {
        setPanel();

        final EventBus eventBus = AppGinjector.injector.getEventBus();
        eventBus.addHandler(LoggedinEvent.TYPE, new LoggedinEvent.Handler() {
            @Override
            public void update(LoggedinEvent p_event) {
                AppGinjector.injector.getLoginService().setLoggedinUser(p_event.getLoggedinUser());
                setPanel();
            }
        });
    }

    private void setPanel() {
        final LoginService loginService = AppGinjector.injector.getLoginService();
        if (loginService.isLoggedIn()) {
            CONTAINER.setWidget(AppGinjector.injector.getMainPanel());
            CONTAINER.forceLayout();
        } else {
            CONTAINER.setWidget(AppGinjector.injector.getLoginPanel());
            CONTAINER.forceLayout();
        }
    }

    public static SimpleContainer getContainer() {
        return CONTAINER;
    }
}
