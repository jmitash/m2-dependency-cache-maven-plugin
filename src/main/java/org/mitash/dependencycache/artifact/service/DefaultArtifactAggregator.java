package org.mitash.dependencycache.artifact.service;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Jacob Mitash
 */
@Named
@Singleton
class DefaultArtifactAggregator implements ArtifactAggregator {

    @Override
    @SuppressWarnings("CollectionAddAllCanBeReplacedWithConstructor")
    public Set<Artifact> getAllArtifacts(MavenProject project) {
        Set<Artifact> artifacts = new HashSet<>();

        artifacts.addAll(getImmediateArtifacts(project));

        forEveryParent(project,
                parentProject -> artifacts.addAll(getImmediateArtifacts(parentProject)));

        return artifacts;
    }

    @SuppressWarnings("unchecked")
    private Set<Artifact> getImmediateArtifacts(MavenProject project) {
        Set<Artifact> artifacts = new HashSet<>();

        artifacts.addAll(getOrEmpty(project::getDependencyArtifacts));
        artifacts.addAll(getOrEmpty(project::getPluginArtifacts));

        return artifacts;
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
