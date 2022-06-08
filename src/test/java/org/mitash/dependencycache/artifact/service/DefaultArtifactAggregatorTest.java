package org.mitash.dependencycache.artifact.service;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jacob Mitash
 */
class DefaultArtifactAggregatorTest {

    private DefaultArtifactAggregator aggregator;

    private final Artifact basePlugin = artifact("base", "plugin", "1");
    private final Artifact parentPlugin = artifact("parent", "plugin", "2");
    private final Artifact parentParentPlugin = artifact("parentParent", "plugin", "3");

    private final Artifact baseDependency = artifact("base", "dependency", "1");
    private final Artifact parentDependency = artifact("parent", "dependency", "2");
    private final Artifact parentParentDependency = artifact("parentParent", "dependency", "3");

    // TODO: dependency in both base and parent w/ different versions?

    @BeforeEach
    void beforeEach() {
        this.aggregator = new DefaultArtifactAggregator();
    }

    @Test
    void findsBasePluginArtifact() {
        assertThat(aggregator.getAllArtifacts(stubProject())).contains(basePlugin);
    }

    @Test
    void findsParentPluginArtifact() {
        assertThat(aggregator.getAllArtifacts(stubProject())).contains(parentPlugin);
    }

    @Test
    void findsGrandParentPluginArtifact() {
        assertThat(aggregator.getAllArtifacts(stubProject())).contains(parentParentPlugin);
    }

    @Test
    void findsBaseDependencyArtifact() {
        assertThat(aggregator.getAllArtifacts(stubProject())).contains(baseDependency);
    }

    @Test
    void findsParentDependencyArtifact() {
        assertThat(aggregator.getAllArtifacts(stubProject())).contains(parentDependency);
    }

    @Test
    void findsGrandParentDependencyArtifact() {
        assertThat(aggregator.getAllArtifacts(stubProject())).contains(parentParentDependency);
    }

    private MavenProject stubProject() {
        MavenProject project = new MavenProject();
        MavenProject parentProject = new MavenProject();
        project.setParent(parentProject);
        MavenProject parentParentProject = new MavenProject();
        parentProject.setParent(parentParentProject);

        project.setPluginArtifacts(Collections.singleton(basePlugin));
        parentProject.setPluginArtifacts(Collections.singleton(parentPlugin));
        parentParentProject.setPluginArtifacts(Collections.singleton(parentParentPlugin));

        project.setDependencyArtifacts(Collections.singleton(baseDependency));
        parentProject.setDependencyArtifacts(Collections.singleton(parentDependency));
        parentParentProject.setDependencyArtifacts(Collections.singleton(parentParentDependency));

        return project;
    }

    private Artifact artifact(String groupId, String artifactId, String version) {
        return new DefaultArtifact(
                groupId, artifactId, version, null, "notnull", "notnull", null);
    }
}