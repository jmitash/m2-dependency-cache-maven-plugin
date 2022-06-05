package org.mitash.dependencycache;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Mojo for uploading dependency caches for the project, if changed.
 * <p>
 * This Mojo is by default bound to the {@link LifecyclePhase#PROCESS_CLASSES process classes} phase since it
 * immediately follows the {@link LifecyclePhase#COMPILE compile} phase in which dependencies are typically downloaded.
 *
 * @author Jacob Mitash
 */

@Mojo(
        name = "dependency-cache-put",
        defaultPhase = LifecyclePhase.PROCESS_CLASSES
)
@SuppressWarnings("unused")
public class M2DependencyCachePutMojo extends AbstractMojo {

    @Override
    public void execute() {
        getLog().info("I would be putting something right now if I were implemented!");
    }
}

