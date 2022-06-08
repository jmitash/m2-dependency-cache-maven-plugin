package org.mitash.dependencycache;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.mitash.dependencycache.artifact.service.ArtifactAggregator;

import javax.inject.Inject;
import java.util.Set;

/**
 * Mojo for fetching pre-existing dependency caches for the project, if available, or requesting a cache creation if
 * not.
 *
 * @author Jacob Mitash
 */
@Mojo(
        name = "dependency-cache"
)
@SuppressWarnings("unused")
public class M2DependencyCacheMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Inject
    private ArtifactAggregator artifactAggregator;

    @Override
    public void execute() {
        Set<Artifact> artifacts = artifactAggregator.getAllArtifacts(project);
        Set<RemoteRepository> projectRepositories = artifactAggregator.getAllProjectRepositories(project);
        Set<RemoteRepository> pluginRepositories = artifactAggregator.getAllPluginRepositories(project);

        getLog().info(String.format("Found %d artifacts", artifacts.size()));
        getLog().info(String.format("Found %d project repositories", projectRepositories.size()));
        getLog().info(String.format("Found %d plugin repositories", pluginRepositories.size()));
    }
}
