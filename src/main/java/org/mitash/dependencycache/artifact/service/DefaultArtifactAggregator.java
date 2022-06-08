package org.mitash.dependencycache.artifact.service;

import org.apache.maven.RepositoryUtils;
import org.eclipse.aether.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.util.artifact.DefaultArtifactTypeRegistry;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Jacob Mitash
 */
@Named
@Singleton
class DefaultArtifactAggregator implements ArtifactAggregator {

    private final static String SCOPE_SYSTEM = "system";
    private final static String SCOPE_RUNTIME_PLUS_SYSTEM = "runtime+system";
    @Override
    @SuppressWarnings("CollectionAddAllCanBeReplacedWithConstructor")
    public Set<Artifact> getAllArtifacts(MavenProject project) {
        Set<Artifact> artifacts = new HashSet<>();

        artifacts.addAll(getImmediateArtifacts(project));

        forEveryParent(project,
                parentProject -> artifacts.addAll(getImmediateArtifacts(parentProject)));

        return artifacts;
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
                .collect(Collectors.toSet());
    }

    private Set<Artifact> getImmediateDependencies(MavenProject project) {
        return project.getDependencies().stream()
                .filter(modelDependency -> modelDependency.getSystemPath() == null)
                .filter(modelDependency -> !SCOPE_SYSTEM.equals(modelDependency.getScope()))
                .filter(modelDependency -> !SCOPE_RUNTIME_PLUS_SYSTEM.equals(modelDependency.getScope()))
                .map(modelDependency -> RepositoryUtils.toDependency(
                        modelDependency, new DefaultArtifactTypeRegistry()))
                .map(Dependency::getArtifact)
                .collect(Collectors.toSet());
    }

    private <T> Set<T> getOrEmpty(Supplier<Set<T>> supplier) {
        Set<T> value = supplier.get();
        return value == null ? Collections.emptySet() : value;
    }

    private void forEveryParent(MavenProject project, Consumer<MavenProject> consumer) {
        while (project.getParent() != null) {
            project = project.getParent();

            consumer.accept(project);
        }
    }
}
