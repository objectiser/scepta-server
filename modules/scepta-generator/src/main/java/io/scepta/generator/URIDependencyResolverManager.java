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
package io.scepta.generator;

import io.scepta.model.Dependency;

import java.util.ServiceLoader;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;

/**
 * This class managers the set of URI dependency resolvers.
 *
 */
public class URIDependencyResolverManager {

    private static final String DEFAULT_CAMEL_VERSION = "2.14.1";
    private static final ServiceLoader<URIDependencyResolver> RESOLVERS=
                        ServiceLoader.load(URIDependencyResolver.class);

    /**
     * This method returns the set of dependencies associated with the
     * supplied URI.
     *
     * @param uri The URI
     * @return The set of dependencies
     */
    public static java.util.Set<Dependency> getDependencies(String uri) {

        for (URIDependencyResolver resolver : RESOLVERS) {
            if (resolver.isTypeSupported(uri)) {
                return (resolver.getDependencies(uri));
            }
        }

        // Check if a default resolver can help
        return (getCamelComponentDependencies(uri));
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
     * @param uri The URI
     * @return The set of dependencies
     */
    protected static java.util.Set<Dependency> getCamelComponentDependencies(String uri) {
        java.util.Set<Dependency> ret=new java.util.HashSet<Dependency>();
        int index=uri.indexOf(':');

        if (index != -1) {
            String component=uri.substring(0, index);
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
        }

        return (ret);
    }

}
