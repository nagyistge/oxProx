from org.ox.oxprox.external import ExternalOpDiscovery
from org.ox.oxprox.external import ExternalOpDiscoveryMode


class PythonExternalOpDiscovery(ExternalOpDiscovery):

    def getMode(self):
        return ExternalOpDiscoveryMode.INTERACTIVE

    def initPage(self, step, context):
        return None

    def isAllowed(self, op, context):
        return True

    def getPageForStep(self, step):
        return "/discovery.xhtml"

    def getStepsCount(self):
        return 1
