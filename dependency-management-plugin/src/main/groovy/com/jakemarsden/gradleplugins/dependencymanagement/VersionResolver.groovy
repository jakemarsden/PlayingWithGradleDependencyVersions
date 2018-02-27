package com.jakemarsden.gradleplugins.dependencymanagement

import groovy.transform.PackageScope
import org.gradle.api.artifacts.ModuleVersionSelector

/**
 * Defines how a dependency's version should be resolved.
 *
 * @author jake.marsden
 */
@PackageScope
interface VersionResolver {

    String resolveVersionFor(ModuleVersionSelector module)
}
