import org.cloudfoundry.gradle.tasks.AbstractCloudFoundryTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException

class GreenBlueMapping extends AbstractGreenBlueTask {

    Map spaceMappings = [:].withDefault { [:] }

    @TaskAction
    void deploy() {
        identifyGreenBlue()

        logger.lifecycle("Currently live is $current. Next one will be $next.")

        withGreenBlue(next) { app ->
            project.cloudfoundry.uris = ["${app}-${space}.cfapps.io"] + spaceMappings[space].map
            project.tasks.find { it.name=='cf-map' }.execute()
        }

        withGreenBlue(current) { app ->
            project.cloudfoundry.uris = ["${app}-${space}.cfapps.io"] + spaceMappings[space].unmap
            project.tasks.find { it.name=='cf-unmap' }.execute()
        }

    }

}
