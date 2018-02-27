package com.jakemarsden.gradleplugins.dependencymanagement

import org.gradle.api.InvalidUserDataException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.*

/**
 * Uses a {@link PropertiesBasedVersionResolver} to resolve property versions using the
 * "{@value #DEPENDENCY_VERSIONS_PATH}" properties file.
 *
 * @see PropertiesBasedVersionResolver
 * @author jake.marsden
 */
class DependencyManagementPlugin implements Plugin<Project> {
    private static final DEPENDENCY_VERSIONS_PATH = '../DependencyVersions/dependencyVersions.properties'

    void apply(Project project) {
        Properties versions = loadVersionProperties project

        project.configurations.all { Configuration config ->
            // The dependencies explicitly declared for the project, ie. not transitive dependencies
            Collection<Dependency> topLevelDependencies = []

            def transitivePredicate = { ModuleVersionSelector module ->
                // It's transitive if it DOESN'T appear in topLevelDependencies
                !topLevelDependencies.any { it.group == module.group && it.name == module.name }
            }
            VersionResolver versionResolver = new PropertiesBasedVersionResolver(versions, transitivePredicate)

            // Record which dependencies are top-level (non-transitive) because the versionResolver needs to know
            config.incoming.beforeResolve { ResolvableDependencies resolvable ->
                topLevelDependencies.addAll resolvable.dependencies
            }

            config.resolutionStrategy { ResolutionStrategy strategy ->
                strategy.eachDependency { DependencyResolveDetails details ->
                    def resolvedVersion = versionResolver.resolveVersionFor details.target
                    details.useVersion resolvedVersion
                }
            }
        }
    }

    private Properties loadVersionProperties(Project project) {
        def file = project.file DEPENDENCY_VERSIONS_PATH
        if (!file.isFile()) {
            def msg = "Unable to find the dependency versions property file using the path: $DEPENDENCY_VERSIONS_PATH"
            throw new InvalidUserDataException(msg)
        }
        def properties = new Properties()
        file.withInputStream properties.&load
        properties
    }
}
