package com.jakemarsden.gradleplugins.dependencymanagement

import groovy.transform.PackageScope
import org.gradle.api.artifacts.ModuleVersionSelector
import org.slf4j.LoggerFactory

import java.util.function.Predicate

/**
 * This {@link VersionResolver} implementation's primary behaviour is to resolve dependency versions by looking at the
 * specified {@link Properties} object. However, dependencies can also be resolved in a few other ways.
 * <p>
 * In order of <b>descending precedence</b>:
 * <ol>
 * <li>
 * The version declared explicitly in the dependencies block:
 * <ul>
 * <li><code>dependencies { compile 'myGroup:myName:myVersion' }</code></li>
 * </ul>
 * </li>
 * <li>
 * The version declared in the "{@code myGroup\:myName}" property
 * <ul>
 * <li><code>com.fasterxml.jackson.core\:jackson-databind=2.9.4</code></li>
 * </ul>
 * </li>
 * <li>
 * The version declared in the "{@code myGroup}" property
 * <ul>
 * <li><code>com.fasterxml.jackson.core=2.9.4</code></li>
 * </ul>
 * </li>
 * <li>
 * The transitive dependency version (Gradle's default behaviour)
 * <ul>
 * <li>Given that
 * <ul>
 * <li>None of the above methods of defining a dependency version were used</li>
 * <li><i>some-group:some-dependency</i> is a dependency of the current project</li>
 * <li><i>some-group:some-dependency</i> depends on <i>some-group:some-transitive-dependency:1.0-RELEASE</i></li>
 * </li>
 * <li>
 * Then
 * <ul>
 * <li>The version resolved for <i>some-group:some-transitive-dependency</i> will be <i>1.0-RELEASE</i></li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 *
 * @author jake.marsden
 */
@PackageScope
class PropertiesBasedVersionResolver implements VersionResolver {
    private static final logger = LoggerFactory.getLogger PropertiesBasedVersionResolver
    private final Properties versions
    private Predicate<ModuleVersionSelector> transitiveDependencyPredicate

    /**
     * The {@code transitiveDependencyPredicate} predicate need to be passed in because once dependency resolution has
     * started, it's not possible to tell which dependency versions were configured "explicitly" in the
     * {@code dependencies} block (no.1 in class-level JavaDoc), and which dependency versions came from transitive
     * dependencies (no.4 in class-level JavaDoc).
     * <p>
     * Simply having the caller decide by passing in a {@link Predicate} as a ctor argument seems like a neat enough
     * solution.
     */
    PropertiesBasedVersionResolver(Properties versions, Predicate<ModuleVersionSelector> isTransitiveDependency) {
        this.versions = Objects.requireNonNull versions
        this.transitiveDependencyPredicate = Objects.requireNonNull isTransitiveDependency
    }

    String resolveVersionFor(ModuleVersionSelector module) {
        boolean isTransitive = transitiveDependencyPredicate.test module
        def explicitVersion = (isTransitive) ? null : (module.version ?: null)
        def exactPropertyVersion = versions["$module.group:$module.name"]
        def groupPropertyVersion = versions[module.group]
        def transitiveVersion = (isTransitive) ? module.version : null

        String resolvedVersion = explicitVersion
        resolvedVersion = resolvedVersion ?: exactPropertyVersion
        resolvedVersion = resolvedVersion ?: groupPropertyVersion
        resolvedVersion = resolvedVersion ?: transitiveVersion

        // Output is probably too verbose for --info
        logger.debug "Resolved dependency version: $module.group:$module.name:$resolvedVersion"
        logger.debug "    Dependencies block:    $explicitVersion"
        logger.debug "    Module-level property: $exactPropertyVersion"
        logger.debug "    Group-level property:  $groupPropertyVersion"
        logger.debug "    Transitive version:    $transitiveVersion"

        resolvedVersion
    }
}
