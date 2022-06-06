package org.mitash.dependencycache;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
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

        getLog().info(String.format("Found %d artifacts", artifacts.size()));
    }
}
