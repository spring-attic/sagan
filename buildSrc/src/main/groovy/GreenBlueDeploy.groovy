import org.cloudfoundry.gradle.tasks.AbstractCloudFoundryTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException

/**
 * This task identifies which application of blue/green is currently running, then uploads the
 * application to CloudFoundry using the alternative.
 *
 * @author Cédric Champeau
 */
class GreenBlueDeploy extends AbstractGreenBlueTask {

    @TaskAction
    void deploy() {
        identifyGreenBlue()

        logger.lifecycle "Apparently $current is running. Next one to be deployed is $next"

        withGreenBlue(next) {
            // Deploy
            project.tasks.find { it.name=='cf-push' }.execute()
        }
    }

}
