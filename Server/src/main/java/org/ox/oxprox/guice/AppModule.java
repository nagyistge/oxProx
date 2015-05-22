package org.ox.oxprox.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.gluu.site.ldap.LDAPConnectionProvider;
import org.gluu.site.ldap.OperationsFacade;
import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.gluu.site.ldap.persistence.exception.LdapMappingException;
import org.ox.oxprox.conf.Configuration;
import org.ox.oxprox.conf.ConfigurationFactory;
import org.ox.oxprox.conf.JsonFileConfiguration;
import org.ox.oxprox.ldap.Conf;
import org.ox.oxprox.service.*;
import org.ox.oxprox.ws.AuthorizationWS;
import org.ox.oxprox.ws.DiscoveryWS;
import org.ox.oxprox.ws.OpPreProcessInterceptor;
import org.ox.oxprox.ws.RedirectWS;
import org.ox.oxprox.ws.TokenWS;
import org.ox.oxprox.ws.UserInfoWS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdi.util.Util;
import org.xdi.util.properties.FileConfiguration;
import org.xdi.util.security.PropertiesDecrypter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Main app guice module.
 *
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 17/02/2014
 */

public class AppModule extends AbstractModule {

    private static final Logger LOG = LoggerFactory.getLogger(AppModule.class);

    @Override
    protected void configure() {
        bind(HttpService.class).in(Singleton.class);
        bind(UmaService.class).in(Singleton.class);
        bind(ErrorService.class).in(Singleton.class);
        bind(OicDiscoveryService.class).in(Singleton.class);
        bind(AatService.class).in(Singleton.class);
        bind(InumService.class).in(Singleton.class);
        bind(ScriptService.class).in(Singleton.class);
        bind(CookieService.class).in(Singleton.class);
        bind(ClientService.class).in(Singleton.class);
        bind(AccessTokenMappingService.class).in(Singleton.class);
        bind(PythonService.class);
        bind(OpPreProcessInterceptor.class);
        bind(RedirectWS.class);
        bind(AuthorizationWS.class);
        bind(UserInfoWS.class);
        bind(DiscoveryWS.class);
        bind(TokenWS.class);
    }

    @Provides
    @Singleton
    public LdapEntryManager provideLdapManager() {
        final FileConfiguration fileConfiguration = ConfigurationFactory.getLdapConfiguration();
        final Properties props = PropertiesDecrypter.decryptProperties(fileConfiguration.getProperties());
        final LDAPConnectionProvider connectionProvider = new LDAPConnectionProvider(props);
        return new LdapEntryManager(new OperationsFacade(connectionProvider));
    }

    @Provides
    @Singleton
    public JsonFileConfiguration provideJsonConfiguration() {
        try {
            LOG.info("Configuration file location: {}", getConfigFileLocation());
            final InputStream stream = Configuration.class.getClassLoader().getResourceAsStream(getConfigFileLocation());
            return Util.createJsonMapper().readValue(stream, JsonFileConfiguration.class);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public String getConfigFileLocation() {
        return ConfigurationFactory.CONFIG_FILE_LOCATION;
    }

    @Provides
    @Singleton
    public Configuration provideConfiguration(LdapEntryManager ldapManager, JsonFileConfiguration jsonFileConfiguration) throws IOException {
        final String dn = ConfigurationFactory.getLdapConfiguration().getString("configurationEntryDN");
        try {
            final Conf conf = ldapManager.find(Conf.class, dn);
            if (conf != null) {
                return Util.createJsonMapper().readValue(conf.getConf(), Configuration.class);
            }
        } catch (LdapMappingException e) {
            LOG.trace(e.getMessage(), e);
            LOG.info("Unable to find configuration in LDAP, try to create configuration entry in LDAP... ");
            if (ConfigurationFactory.getLdapConfiguration().getBoolean("createLdapConfigurationEntryIfNotExist")) {
                if (jsonFileConfiguration != null) {
                    final Conf c = new Conf();
                    c.setDn(ConfigurationFactory.getLdapConfiguration().getString("configurationEntryDN"));
                    c.setConf(Util.createJsonMapper().writeValueAsString(jsonFileConfiguration));
                    try {
                        ldapManager.persist(c);
                        LOG.info("Configuration entry is created in LDAP.");
                    } catch (Exception ex) {
                        LOG.error(e.getMessage(), ex);
                    }
                    return jsonFileConfiguration;
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        LOG.error("Failed to create configuration.");
        return null;
    }
}
