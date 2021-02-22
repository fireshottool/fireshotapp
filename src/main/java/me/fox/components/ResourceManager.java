package me.fox.components;

import java.io.File;
import java.util.List;

/**
 * @author (Ausgefuchster)
 * @version (~ 16.11.2020)
 */

public interface ResourceManager {

    /**
     * Applies the resources (images).
     *
     * @param files {@link List} of resources
     */
    void applyResources(List<File> files);
}
