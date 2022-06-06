package org.mitash.dependencycache.artifact.service;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

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

    private Set<Artifact> getImmediateArtifacts(MavenProject project) {
        Set<Artifact> artifacts = new HashSet<>();

        artifacts.addAll(getDependencyArtifacts(project));
        artifacts.addAll(getPluginArtifacts(project));

        return artifacts;
    }

    private Set<Artifact> getDependencyArtifacts(MavenProject project) {
        //noinspection unchecked
        return project.getDependencyArtifacts() == null ?
                Collections.emptySet() : project.getDependencyArtifacts();
    }

    private Set<Artifact> getPluginArtifacts(MavenProject project) {
        //noinspection unchecked
        return project.getPluginArtifacts() == null ?
                Collections.emptySet() : project.getPluginArtifacts();
    }

    private void forEveryParent(MavenProject project, Consumer<MavenProject> consumer) {
        while (project.getParent() != null) {
            project = project.getParent();

            consumer.accept(project);
        }
    }
}
