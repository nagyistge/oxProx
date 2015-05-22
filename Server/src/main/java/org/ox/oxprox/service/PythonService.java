package org.ox.oxprox.service;

import org.gluu.site.ldap.persistence.util.ReflectHelper;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdi.util.StringHelper;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 12/03/2014
 */

public class PythonService {

    private static final Logger LOG = LoggerFactory.getLogger(PythonService.class);

    /*
      * Initialize singleton instance during startup
      */
    public void initPythonInterpreter() {
        try {
            PythonInterpreter.initialize(getPreProperties(), getPostProperties(), null);
        } catch (Exception ex) {
            LOG.error("Failed to initialize PythonInterpreter", ex);
        }
    }

    private Properties getPreProperties() {
        Properties props = System.getProperties();
        Properties clonedProps = (Properties) props.clone();
        clonedProps.setProperty("java.class.path", ".");
        clonedProps.setProperty("java.library.path", "");
        clonedProps.remove("javax.net.ssl.trustStore");
        clonedProps.remove("javax.net.ssl.trustStorePassword");

        return clonedProps;
    }

    private Properties getPostProperties() {
        Properties props = getPreProperties();

        String catalinaTmpFolder = System.getProperty("java.io.tmpdir") + File.separator + "python" + File.separator + "cachedir";
        props.setProperty("python.cachedir", catalinaTmpFolder);

        String pythonHome = System.getenv("PYTHON_HOME");
        if (StringHelper.isNotEmpty(pythonHome)) {
            props.setProperty("python.home", pythonHome);
        }

        // Register custom python modules
        String oxAuthPythonModulesPath = System.getProperty("catalina.home") + File.separator + "conf" + File.separator + "python";
        props.setProperty("python.path", oxAuthPythonModulesPath);

        return props;
    }

    public <T> T loadPythonScript(String scriptName, String scriptPythonType, Class<T> scriptJavaType, PyObject[] constructorArgs) {
        if (StringHelper.isEmpty(scriptName)) {
            return null;
        }

        PythonInterpreter interpret = new PythonInterpreter();
        try {
            interpret.execfile(scriptName);
        } catch (Exception ex) {
            throw new RuntimeException(String.format("Failed to load python file '%s'", scriptName), ex);
        }

        return loadPythonScript(scriptPythonType, scriptJavaType, constructorArgs, interpret);
    }

    public <T> T loadPythonScript(InputStream scriptFile, String scriptPythonType, Class<T> scriptJavaType, PyObject[] constructorArgs) {
        if (scriptFile == null) {
            return null;
        }

        PythonInterpreter interpret = new PythonInterpreter();
        try {
            interpret.execfile(scriptFile);
        } catch (Exception ex) {
            throw new RuntimeException(String.format("Failed to load python file '%s'", scriptFile), ex);
        }

        return loadPythonScript(scriptPythonType, scriptJavaType, constructorArgs, interpret);
    }

    @SuppressWarnings("unchecked")
    private <T> T loadPythonScript(String scriptPythonType, Class<T> scriptJavaType, PyObject[] constructorArgs, PythonInterpreter interpret) {
        PyObject scriptPythonTypeObject = interpret.get(scriptPythonType);
        if (scriptPythonTypeObject == null) {
            return null;
        }

        PyObject scriptPythonTypeClass;
        try {
            scriptPythonTypeClass = scriptPythonTypeObject.__call__(constructorArgs);
        } catch (Exception ex) {
            throw new RuntimeException(String.format("Failed to initialize python class '%s'", scriptPythonType), ex);
        }

        Object scriptJavaClass = scriptPythonTypeClass.__tojava__(scriptJavaType);
        if (!ReflectHelper.assignableFrom(scriptJavaClass.getClass(), scriptJavaType)) {
            return null;

        }

        return (T) scriptJavaClass;
    }

}
