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
import java.util.List;
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

    private final RemoteRepository baseProjectRepository = remoteRepository("base", "1");
    private final RemoteRepository parentProjectRepository = remoteRepository("parent", "2");
    private final RemoteRepository parentParentProjectRepository = remoteRepository("parentParent", "3");

    private final RemoteRepository basePluginRepository = remoteRepository("base", "4");
    private final RemoteRepository parentPluginRepository = remoteRepository("parent", "5");
    private final RemoteRepository parentParentPluginRepository = remoteRepository("parentParent", "6");

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

    @Test
    void findBaseProjectRepository() {
        assertThat(aggregator.getAllProjectRepositories(stubProject()))
                .contains(baseProjectRepository);
    }

    @Test
    void findParentProjectRepository() {
        assertThat(aggregator.getAllProjectRepositories(stubProject()))
                .contains(parentProjectRepository);
    }

    @Test
    void findGrandProjectRepository() {
        assertThat(aggregator.getAllProjectRepositories(stubProject()))
                .contains(parentParentProjectRepository);
    }

    @Test
    void findBasePluginRepository() {
        assertThat(aggregator.getAllPluginRepositories(stubProject()))
                .contains(basePluginRepository);
    }

    @Test
    void findParentPluginRepository() {
        assertThat(aggregator.getAllPluginRepositories(stubProject()))
                .contains(parentPluginRepository);
    }

    @Test
    void findGrandPluginRepository() {
        assertThat(aggregator.getAllPluginRepositories(stubProject()))
                .contains(parentParentPluginRepository);
    }

    @Test
    void artifactTypeDependencyProperty() {
        Artifact baseDependencyArtifact = aggregator.getAllArtifacts(stubProject()).stream()
                .filter(matchesDependency(baseDependency))
                .findAny()
                .orElseThrow(() -> new RuntimeException("No base dependency found"));

        assertThat(baseDependencyArtifact.getProperties().get(DefaultArtifactAggregator.KEY_ARTIFACT_TYPE))
                .isEqualTo(DefaultArtifactAggregator.VAL_ARTIFACT_TYPE_DEPENDENCY);
    }

    @Test
    void artifactTypePluginProperty() {
        Artifact baseDependencyArtifact = aggregator.getAllArtifacts(stubProject()).stream()
                .filter(matchesArtifact(basePlugin))
                .findAny()
                .orElseThrow(() -> new RuntimeException("No base plugin found"));

        assertThat(baseDependencyArtifact.getProperties().get(DefaultArtifactAggregator.KEY_ARTIFACT_TYPE))
                .isEqualTo(DefaultArtifactAggregator.VAL_ARTIFACT_TYPE_PLUGIN);
    }

    private MavenProject stubProject() {
        Model model = new Model();
        Model parentModel = new Model();
        Model parentParentModel = new Model();

        MavenProject project = new StubMavenProject(
                model, baseProjectRepository, basePluginRepository);
        MavenProject parentProject = new StubMavenProject(
                parentModel, parentProjectRepository, parentPluginRepository);
        MavenProject parentParentProject = new StubMavenProject(
                parentParentModel, parentParentProjectRepository, parentParentPluginRepository);

        project.setParent(parentProject);
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

    private static class StubMavenProject extends MavenProject {

        private final RemoteRepository projectRepository;
        private final RemoteRepository pluginRepository;

        public StubMavenProject(Model model, RemoteRepository projectRepository, RemoteRepository pluginRepository) {
            super(model);
            this.projectRepository = projectRepository;
            this.pluginRepository = pluginRepository;
        }

        @Override
        public List<RemoteRepository> getRemoteProjectRepositories() {
            return Collections.singletonList(projectRepository);
        }

        @Override
        public List<RemoteRepository> getRemotePluginRepositories() {
            return Collections.singletonList(pluginRepository);
        }
    }
}