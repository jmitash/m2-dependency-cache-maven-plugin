package org.mitash.dependencycache.artifact.service;

import org.apache.maven.RepositoryUtils;
import org.eclipse.aether.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Default implementation of an Artifact Aggregator.
 *
 * @author Jacob Mitash
 */
@Named
@Singleton
class DefaultArtifactAggregator implements ArtifactAggregator {

    static final String SCOPE_SYSTEM = "system";
    static final String SCOPE_RUNTIME_PLUS_SYSTEM = "runtime+system";

    static final String KEY_ARTIFACT_TYPE = "org.mitash.dependencycache.ARTIFACT_TYPE";
    static final String VAL_ARTIFACT_TYPE_DEPENDENCY = "DEPENDENCY";
    static final String VAL_ARTIFACT_TYPE_PLUGIN = "PLUGIN";

    @Override
    @SuppressWarnings("CollectionAddAllCanBeReplacedWithConstructor")
    public Set<Artifact> getAllArtifacts(MavenProject project) {
        Set<Artifact> artifacts = new HashSet<>();

        artifacts.addAll(getImmediateArtifacts(project));

        forEveryParent(project,
                parentProject -> artifacts.addAll(getImmediateArtifacts(parentProject)));

        return artifacts;
    }

    @Override
    public Set<RemoteRepository> getAllProjectRepositories(MavenProject project) {
        Set<RemoteRepository> remoteRepositories = new HashSet<>(
                getOrEmpty(project::getRemoteProjectRepositories));

        forEveryParent(project,
                parentProject -> remoteRepositories.addAll(getOrEmpty(parentProject::getRemoteProjectRepositories)));

        return remoteRepositories;
    }

    @Override
    public Set<RemoteRepository> getAllPluginRepositories(MavenProject project) {
        Set<RemoteRepository> remoteRepositories = new HashSet<>(
                getOrEmpty(project::getRemotePluginRepositories));

        forEveryParent(project,
                parentProject -> remoteRepositories.addAll(getOrEmpty(parentProject::getRemotePluginRepositories)));

        return remoteRepositories;
    }

    private Set<Artifact> getImmediateArtifacts(MavenProject project) {
        Set<Artifact> artifacts = new HashSet<>();

        artifacts.addAll(getImmediateDependencies(project));
        artifacts.addAll(getImmediatePlugins(project));

        return artifacts;
    }

    private Set<Artifact> getImmediatePlugins(MavenProject project) {
        return getOrEmpty(project::getPluginArtifacts).stream()
                .filter(artifact -> artifact.getFile() == null)
                .map(RepositoryUtils::toArtifact)
                .map(artifact -> addArtifactTypeProperty(artifact, VAL_ARTIFACT_TYPE_PLUGIN))
                .collect(Collectors.toSet());
    }

    private Set<Artifact> getImmediateDependencies(MavenProject project) {
        return project.getDependencies().stream()
                .filter(modelDependency -> modelDependency.getSystemPath() == null)
                .filter(modelDependency -> !SCOPE_SYSTEM.equals(modelDependency.getScope()))
                .filter(modelDependency -> !SCOPE_RUNTIME_PLUS_SYSTEM.equals(modelDependency.getScope()))
                .map(modelDependency -> RepositoryUtils.toDependency(
                        modelDependency, typeId -> null))
                .map(Dependency::getArtifact)
                .map(artifact -> addArtifactTypeProperty(artifact, VAL_ARTIFACT_TYPE_DEPENDENCY))
                .collect(Collectors.toSet());
    }

    private Artifact addArtifactTypeProperty(Artifact artifact, String value) {
        Map<String, String> newProperties = new HashMap<>(artifact.getProperties());
        newProperties.put(KEY_ARTIFACT_TYPE, value);
        return artifact.setProperties(newProperties);
    }

    private <T> Collection<T> getOrEmpty(Supplier<Collection<T>> supplier) {
        Collection<T> value = supplier.get();
        return value == null ? Collections.emptySet() : value;
    }

    private void forEveryParent(MavenProject project, Consumer<MavenProject> consumer) {
        while (project.getParent() != null) {
            project = project.getParent();

            consumer.accept(project);
        }
    }
}
