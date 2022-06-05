package org.mitash.dependencycache;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

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

    @Override
    public void execute() {
        getLog().info("I would be doing something right now if I were implemented!");
    }
}
