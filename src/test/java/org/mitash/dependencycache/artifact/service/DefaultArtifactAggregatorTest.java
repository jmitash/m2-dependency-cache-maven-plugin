package org.mitash.dependencycache.artifact.service;


import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jacob Mitash
 */
@SuppressWarnings("SameParameterValue")
class DefaultArtifactAggregatorTest {

    private DefaultArtifactAggregator aggregator;

    private final org.apache.maven.artifact.Artifact basePlugin = artifact("base", "plugin", "1");
    private final org.apache.maven.artifact.Artifact parentPlugin = artifact("parent", "plugin", "2");
    private final org.apache.maven.artifact.Artifact parentParentPlugin = artifact("parentParent", "plugin", "3");

    private final Dependency baseDependency = dependency("base", "dependency", "1");
    private final Dependency parentDependency = dependency("parent", "dependency", "2");
    private final Dependency parentParentDependency = dependency("parentParent", "dependency", "3");

    private final RemoteRepository baseRepository = remoteRepository("base", "1");
    private final RemoteRepository parentRepository = remoteRepository("parent", "2");
    private final RemoteRepository parentParentRepository = remoteRepository("parentParent", "3");

    // TODO: dependency in both base and parent w/ different versions?

    @BeforeEach
    void beforeEach() {
        this.aggregator = new DefaultArtifactAggregator();
    }

    @Test
    void findsBasePluginArtifact() {
        assertThat(aggregator.getAllArtifacts(stubProject()))
                .anyMatch(matchesArtifact(basePlugin));
    }

    @Test
    void findsParentPluginArtifact() {
        assertThat(aggregator.getAllArtifacts(stubProject()))
                .anyMatch(matchesArtifact(parentPlugin));
    }

    @Test
    void findsGrandParentPluginArtifact() {
        assertThat(aggregator.getAllArtifacts(stubProject()))
                .anyMatch(matchesArtifact(parentParentPlugin));
    }

    @Test
    void findsBaseDependencyArtifact() {
        assertThat(aggregator.getAllArtifacts(stubProject()))
                .anyMatch(matchesDependency(baseDependency));
    }

    @Test
    void findsParentDependencyArtifact() {
        assertThat(aggregator.getAllArtifacts(stubProject()))
                .anyMatch(matchesDependency(parentDependency));
    }

    @Test
    void findsGrandParentDependencyArtifact() {
        assertThat(aggregator.getAllArtifacts(stubProject()))
                .anyMatch(matchesDependency(parentParentDependency));
    }

    private MavenProject stubProject() {
        Model model = new Model();
        Model parentModel = new Model();
        Model parentParentModel = new Model();

        MavenProject project = new MavenProject(model);
        MavenProject parentProject = new MavenProject(parentModel);
        project.setParent(parentProject);
        MavenProject parentParentProject = new MavenProject(parentParentModel);
        parentProject.setParent(parentParentProject);

        project.setPluginArtifacts(Collections.singleton(basePlugin));
        parentProject.setPluginArtifacts(Collections.singleton(parentPlugin));
        parentParentProject.setPluginArtifacts(Collections.singleton(parentParentPlugin));

        model.setDependencies(Collections.singletonList(baseDependency));
        parentModel.setDependencies(Collections.singletonList(parentDependency));
        parentParentModel.setDependencies(Collections.singletonList(parentParentDependency));

        return project;
    }

    private Dependency dependency(String groupId, String artifactId, String version) {
        Dependency dependency = new Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        dependency.setVersion(version);
        return dependency;
    }

    private org.apache.maven.artifact.Artifact artifact(String groupId, String artifactId, String version) {
        return new DefaultArtifact(
                groupId, artifactId, version,
                null, "notnull", "notnull", new DefaultArtifactHandler());
    }

    private RemoteRepository remoteRepository(String id, String url) {
        return new RemoteRepository.Builder(id, "type", url)
                .build();
    }

    private Predicate<Artifact> matchesDependency(Dependency dependency) {
        return artifact ->
                artifact != null
                        && dependency != null
                        && dependency.getGroupId().equals(artifact.getGroupId())
                        && dependency.getArtifactId().equals(artifact.getArtifactId())
                        && dependency.getVersion().equals(artifact.getVersion());
    }

    private Predicate<Artifact> matchesArtifact(org.apache.maven.artifact.Artifact original) {
        return artifact ->
                artifact != null
                        && original != null
                        && original.getGroupId().equals(artifact.getGroupId())
                        && original.getArtifactId().equals(artifact.getArtifactId())
                        && original.getVersion().equals(artifact.getVersion());
    }
}