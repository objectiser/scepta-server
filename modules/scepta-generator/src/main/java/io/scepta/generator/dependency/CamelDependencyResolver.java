/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.scepta.generator.dependency;

import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import io.scepta.generator.DependencyResolver;
import io.scepta.model.Dependency;

/**
 * This implementation of the component dependency resolver will check for an Apache
 * Camel component.
 *
 */
public class CamelDependencyResolver implements DependencyResolver {

    // TODO: Obtain camel version
    private static final String DEFAULT_CAMEL_VERSION = "2.15-SNAPSHOT";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTypeSupported(String component) {
        return (getCamelComponentDependencies(component).size() > 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Dependency> getDependencies(String component) {
        return (getCamelComponentDependencies(component));
    }

    /**
     * This method returns the camel version.
     *
     * @return The camel version
     */
    protected static String getCamelVersion() {
        String version=org.apache.camel.CamelContext.class.getPackage().getImplementationVersion();

        if (version != null) {
            return (version);
        }

        return (DEFAULT_CAMEL_VERSION);
    }

    /**
     * This method checks if the named component is a known camel
     * component.
     *
     * @param component The component
     * @return The set of dependencies
     */
    protected static java.util.Set<Dependency> getCamelComponentDependencies(String component) {
        java.util.Set<Dependency> ret=new java.util.HashSet<Dependency>();
        String camelVersion=getCamelVersion();

        try {
            Maven.resolver().resolve("org.apache.camel:camel-"+component+":"+camelVersion)
                                .withoutTransitivity().asFile();

            ret.add(new Dependency()
                    .setGroupId("org.apache.camel")
                    .setArtifactId("camel-"+component)
                    .setVersion(camelVersion));
        } catch (Throwable t) {
            // Ignore, as means component does not exist
        }

        return (ret);
    }

}
