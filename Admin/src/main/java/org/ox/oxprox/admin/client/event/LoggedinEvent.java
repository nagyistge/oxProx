package org.ox.oxprox.admin.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import org.ox.oxprox.admin.shared.User;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 17/06/2014
 */

public class LoggedinEvent extends GwtEvent<LoggedinEvent.Handler> {


    public static interface Handler extends EventHandler {

        void update(LoggedinEvent p_event);
    }

    public static final Type<Handler> TYPE = new Type<Handler>();

    private final User loggedinUser;

    public LoggedinEvent(User loggedinUser) {
        this.loggedinUser = loggedinUser;
    }

    public User getLoggedinUser() {
        return loggedinUser;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.update(this);
    }
}
