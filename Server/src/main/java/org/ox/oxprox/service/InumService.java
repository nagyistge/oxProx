package org.ox.oxprox.service;

import com.google.inject.Inject;
import org.ox.oxprox.conf.Configuration;
import org.xdi.util.INumGenerator;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 12/03/2014
 */

public class InumService {

    @Inject
    Configuration conf;

    public static final String SEPARATOR = "!";
    public static final String OP_SUFFIX = SEPARATOR + "BBBB";
    public static final String SCRIPT_SUFFIX = SEPARATOR + "BBBC";

    private String generate(String suffix) {
        return conf.getInumPrefix() + suffix + SEPARATOR + INumGenerator.generate(2);
    }

    public String generateOp() {
        return generate(OP_SUFFIX);
    }

    public String generateScript() {
        return generate(SCRIPT_SUFFIX);
    }
}
