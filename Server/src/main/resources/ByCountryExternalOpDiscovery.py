from org.ox.oxprox.external import ExternalOpDiscovery
from org.ox.oxprox.external import ExternalOpDiscoveryMode
from org.apache.commons.lang import StringUtils


class PythonExternalOpDiscovery(ExternalOpDiscovery):

    def getMode(self):
        return ExternalOpDiscoveryMode.INTERACTIVE

    def initPage(self, step, context):
        return None

    def isAllowed(self, op, context):
        if (StringUtils.equalsIgnoreCase(op.getCountry(), context.getHttpRequest().getParameter('country'))):
            return True
        return False

    def getPageForStep(self, step):
        if (step == 1):
            return "/discovery/bycountry/bycountry.xhtml"
        if (step == 2):
            return "/discovery/bycountry/discovery.xhtml"

    def getStepsCount(self):
        return 2