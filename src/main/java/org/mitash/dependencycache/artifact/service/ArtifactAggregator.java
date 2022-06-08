package org.mitash.dependencycache.artifact.service;


import org.apache.maven.project.MavenProject;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.Set;

/**
 * Finds artifacts and repositories for the Maven project.
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

    /**
     * Gets all the remote project repositories for the given Maven project. These repositories will be used to download
     * dependencies.
     * <p>
     * Includes remote repositories for all parents. Does not include modules of the given project.
     *
     * @param project the project to get the remote repositories of
     * @return a set of remote repositories associated with the project and its hierarchy
     */
    Set<RemoteRepository> getAllProjectRepositories(MavenProject project);

    /**
     * Gets all the remote plugin repositories for the given Maven project. These repositories will be used to download
     * plugins.
     * <p>
     * Includes remote repositories for all parents. Does not include modules of the given project.
     *
     * @param project the project to get the remote repositories of
     * @return a set of remote repositories associated with the project and its hierarchy
     */
    Set<RemoteRepository> getAllPluginRepositories(MavenProject project);
}
