package org.mitash.dependencycache.artifact.service;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

import java.util.Set;

/**
 * Finds artifacts for the Maven project.
 *
 * @author Jacob Mitash
 */
public interface ArtifactAggregator {

    /**
     * Gets all artifacts for the given Maven project.
     * <p>
     * Includes dependencies, plugins, and the same for all parents. Does not include modules of the given project.
     *
     * @param project the project to get artifacts of
     * @return a set of artifacts associated with the project and its hierarchy
     */
    Set<Artifact> getAllArtifacts(MavenProject project);
}
