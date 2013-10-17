import org.cloudfoundry.gradle.tasks.AbstractCloudFoundryTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.GradleException

abstract class AbstractGreenBlueTask extends AbstractCloudFoundryTask {

    protected String current
    protected String next

    protected void withGreenBlue(String tmpName, Closure cl) {
        def appName = project.cloudfoundry.application
        project.cloudfoundry.application = "${appName}-${tmpName}"
        cl(appName)
        project.cloudfoundry.application = appName
    }

    @TaskAction
    protected void identifyGreenBlue() {
        // First find matching applications
        def apps

        withCloudFoundryClient {
            def appsMatchingURL = client.applications.findAll { it.uris.any { it =~ "${application}-${space}" } }
            apps = appsMatchingURL.groupBy {
                if (it.name =~ '-green') {
                    'green'
                } else if (it.name =~ '-blue') {
                    'blue'
                } else {
                    'undef'
                }
            }
        }

        apps.green = apps.green?:[]
        apps.blue = apps.blue?:[]
        def num = apps.green.size() + apps.blue.size()
        if (num!=1) {
            throw new GradleException("There must be exactly one CF green or blue app running, found $num.")
        }
        if (apps.undef) {
            throw new GradleException("Unexpected applications were found. Can't proceed: ${apps.undef*.name}")
        }

        current = apps.green?'green':'blue'
        next = apps.green?'blue':'green'

    }

}
