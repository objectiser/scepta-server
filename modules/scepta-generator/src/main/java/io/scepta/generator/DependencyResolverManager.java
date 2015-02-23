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

/**
 * This class managers the set of URI dependency resolvers.
 *
 */
public class DependencyResolverManager {

    private static final ServiceLoader<DependencyResolver> RESOLVERS=
                        ServiceLoader.load(DependencyResolver.class);

    /**
     * This method returns the set of dependencies associated with the
     * supplied URI.
     *
     * @param uri The URI
     * @return The set of dependencies
     */
    public static java.util.Set<Dependency> getDependencies(String uri) {
        java.util.Set<Dependency> ret=new java.util.HashSet<Dependency>();
        int index=uri.indexOf(':');
        String component=uri;

        if (index != -1) {
            component = uri.substring(0, index);
        }

        for (DependencyResolver resolver : RESOLVERS) {
            if (resolver.isTypeSupported(component)) {
                ret.addAll(resolver.getDependencies(component));
            }
        }

        return (ret);
    }

}
