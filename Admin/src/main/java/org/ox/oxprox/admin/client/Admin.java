package org.ox.oxprox.admin.client;

import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.box.MessageBox;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 17/06/2014
 */

public class Admin {
    public static final Margins DEFAULT_MARGINS = new Margins(6);

    private Admin() {
    }

    public static MessageBox showInformation(String s) {
        MessageBox d = new MessageBox("Information", s);
        d.show();
        return d;
    }
}
