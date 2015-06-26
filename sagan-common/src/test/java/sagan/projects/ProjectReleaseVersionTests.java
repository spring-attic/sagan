/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sagan.projects;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author Dave Syer
 *
 */
public class ProjectReleaseVersionTests {

    @Test
    public void snapshotDetected() {
        ProjectRelease version = new ProjectReleaseBuilder().versionName("1.0.0.BUILD-SNAPSHOT").releaseStatus(null).build();
        assertThat(version.isSnapshot(), equalTo(true));
    }

    @Test
    public void snapshotDetectedCiStyle() {
        ProjectRelease version = new ProjectReleaseBuilder().versionName("1.0.0.CI-SNAPSHOT").releaseStatus(null).build();
        assertThat(version.isSnapshot(), equalTo(true));
    }

    @Test
    public void snapshotDetectedMavenStyle() {
        ProjectRelease version = new ProjectReleaseBuilder().versionName("1.0.0-SNAPSHOT").releaseStatus(null).build();
        assertThat(version.isSnapshot(), equalTo(true));
    }

    @Test
    public void releaseTrainSnapshotDetected() {
        ProjectRelease version = new ProjectReleaseBuilder().versionName("Angel.BUILD-SNAPSHOT").releaseStatus(null).build();
        assertThat(version.isSnapshot(), equalTo(true));
    }

    @Test
    public void prereleaseDetected() {
        ProjectRelease version = new ProjectReleaseBuilder().versionName("1.0.0.RC1").releaseStatus(null).build();
        assertThat(version.isPreRelease(), equalTo(true));
    }

    @Test
    public void releaseTrainPrereleaseDetected() {
        ProjectRelease version = new ProjectReleaseBuilder().versionName("Angel.RC1").releaseStatus(null).build();
        assertThat(version.isPreRelease(), equalTo(true));
    }

    @Test
    public void gaDetected() {
        ProjectRelease version = new ProjectReleaseBuilder().versionName("1.0.0.RELEASE").releaseStatus(null).build();
        assertThat(version.isGeneralAvailability(), equalTo(true));
    }

    @Test
    public void releaseTrainGaDetected() {
        ProjectRelease version = new ProjectReleaseBuilder().versionName("Angel.RELEASE").releaseStatus(null).build();
        assertThat(version.isGeneralAvailability(), equalTo(true));
    }

    @Test
    public void releaseServiceReleaseTrainGaDetected() {
        ProjectRelease version = new ProjectReleaseBuilder().versionName("Angel.SR1").releaseStatus(null).build();
        assertThat(version.isGeneralAvailability(), equalTo(true));
    }

}
