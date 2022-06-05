package org.mitash.dependencycache;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Mojo for fetching pre-existing dependency caches for the project, if available.
 * <p>
 * This Mojo is by default bound to the {@link LifecyclePhase#PROCESS_RESOURCES process resources} phase since it
 * immediately precedes the {@link LifecyclePhase#COMPILE compile} phase in which dependencies are typically downloaded.
 *
 * @author Jacob Mitash
 */
@Mojo(
        name = "dependency-cache-get",
        defaultPhase = LifecyclePhase.PROCESS_RESOURCES
)
@SuppressWarnings("unused")
public class M2DependencyCacheGetMojo extends AbstractMojo {

    @Override
    public void execute() {
        getLog().info("I would be getting something right now if I were implemented!");
    }
}
