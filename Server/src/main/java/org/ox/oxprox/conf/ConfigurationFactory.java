package org.ox.oxprox.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdi.util.properties.FileConfiguration;

import java.io.File;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 24/12/2013
 */

public class ConfigurationFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationFactory.class);

    private static final String BASE_DIR = System.getProperty("catalina.home") != null ?
            System.getProperty("catalina.home") : "";

    private static final String DIR = BASE_DIR + File.separator + "conf" + File.separator;

    public static final String CONFIG_FILE_NAME = "oxProx-config.json";
    public static final String LDAP_CONFIG_FILE_NAME = "oxProx-ldap.properties";

    public static final String CONFIG_FILE_LOCATION = DIR + CONFIG_FILE_NAME;
    private static final String LDAP_CONFIG_FILE_LOCATION = DIR + LDAP_CONFIG_FILE_NAME;

    private static volatile Configuration CONF = null;

    private static class LdapConfHolder {
        private static final FileConfiguration CONF = createLdapConfiguration();

        private static FileConfiguration createLdapConfiguration() {
            LOG.info("LDAP configuration file location: {}", LDAP_CONFIG_FILE_LOCATION);
            return new FileConfiguration(ConfigurationFactory.LDAP_CONFIG_FILE_NAME);
        }
    }

    private ConfigurationFactory() {
    }

    public static FileConfiguration getLdapConfiguration() {
        return LdapConfHolder.CONF;
    }

    public static Configuration getConfiguration() {
        return CONF;
    }

}
