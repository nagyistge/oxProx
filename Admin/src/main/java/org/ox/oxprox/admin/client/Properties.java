package org.ox.oxprox.admin.client;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import org.ox.oxprox.admin.shared.Client;
import org.ox.oxprox.admin.shared.OP;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 18/06/2014
 */

public class Properties {

    public static final ClientProperties CLIENT = GWT.create(ClientProperties.class);
    public static final OpProperties OP = GWT.create(OpProperties.class);

    public interface ClientProperties extends PropertyAccess<Client> {

        ModelKeyProvider<Client> dn();

        ValueProvider<Client, String> displayName();

        ValueProvider<Client, String> inum();

    }

    public interface OpProperties extends PropertyAccess<OP> {

        ModelKeyProvider<OP> dn();

        ValueProvider<OP, String> displayName();

        ValueProvider<OP, String> inum();

        ValueProvider<OP, String> country();

        ValueProvider<OP, String> city();

    }
}
