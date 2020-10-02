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

package sagan.site.projects;

import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

public class ReleaseVersionTests {

	private Project project = new Project("spring-test", "Spring Test Project");

    @Test
    public void snapshotDetected() {
        Release release = new Release(Version.of("1.0.0.BUILD-SNAPSHOT"));
        assertThat(release.isSnapshot()).isEqualTo(true);
        assertThat(release.getRepository().getUrl().contains("snapshot"));
    }

    @Test
    public void snapshotDetectedMavenStyle() {
		Release release = new Release(Version.of("1.0.0-SNAPSHOT"));
        assertThat(release.isSnapshot()).isEqualTo(true);
        assertThat(release.getRepository().getUrl().contains("snapshot"));
    }

    @Test
    public void releaseTrainSnapshotDetected() {
		Release release = new Release(Version.of("Angel.BUILD-SNAPSHOT"));
        assertThat(release.isSnapshot()).isEqualTo(true);
        assertThat(release.getRepository().getUrl().contains("snapshot"));
    }

    @Test
    public void prereleaseDetected() {
		Release release = new Release(Version.of("1.0.0.RC1"));
        assertThat(release.isPreRelease()).isEqualTo(true);
        assertThat(release.getRepository().getUrl().contains("milestone"));
    }

    @Test
    public void releaseTrainPrereleaseDetected() {
		Release release = new Release(Version.of("Angel.RC1"));
        assertThat(release.isPreRelease()).isEqualTo(true);
        assertThat(release.getRepository().getUrl().contains("milestone"));
    }

    @Test
    public void gaDetected() {
		Release release = new Release(Version.of("1.0.0.RELEASE"));
        assertThat(release.isGeneralAvailability()).isEqualTo(true);
		assertThat(release.getRepository().getUrl().contains("release"));
    }

    @Test
    public void releaseTrainGaDetected() {
		Release release = new Release(Version.of("Angel.RELEASE"));
        assertThat(release.isGeneralAvailability()).isEqualTo(true);
		assertThat(release.getRepository().getUrl().contains("release"));
    }

    @Test
    public void releaseServiceReleaseTrainGaDetected() {
		Release release = new Release(Version.of("Angel.SR1"));
        assertThat(release.isGeneralAvailability()).isEqualTo(true);
		assertThat(release.getRepository().getUrl().contains("release"));
    }

    @Test
    public void releaseDashSeparatorDetected() {
		Release release = new Release(Version.of("Angel.M1"));
        assertThat(release.isPreRelease()).isEqualTo(true);
        assertThat(release.getRepository().getUrl().contains("milestone"));
    }

}
