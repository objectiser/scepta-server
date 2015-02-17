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

import java.util.Collections;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.scepta.generator.DependencyResolver;
import io.scepta.model.Dependency;

/**
 * This implementation of the URI dependency resolver will check for the
 * component in a local configuration file.
 *
 */
public class ConfiguredDependencyResolver implements DependencyResolver {

    private static final ObjectMapper MAPPER=new ObjectMapper();

    private static final Configuration CONFIG;

    static {
        java.io.InputStream is=ConfiguredDependencyResolver.class.getResourceAsStream("/ComponentDependencies.json");

        Configuration config=null;

        try {
            config = MAPPER.readValue(is, Configuration.class);
        } catch (Exception e) {
            // TODO: REPORT ERROR
            e.printStackTrace();

        } finally {
            try {
                is.close();
            } catch (Exception e) {
                // TODO: REPORT ERROR
                e.printStackTrace();
            }
        }

        CONFIG = config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTypeSupported(String component) {
        return (CONFIG.getDependencies(component).size() > 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Dependency> getDependencies(String component) {
        return (CONFIG.getDependencies(component));
    }

    public static class Configuration {

        private java.util.Map<String,java.util.Set<Dependency>> _dependencies=
                new java.util.HashMap<String,java.util.Set<Dependency>>();

        public void setDependencies(java.util.Map<String,java.util.Set<Dependency>> dependencies) {
            _dependencies = dependencies;
        }

        public java.util.Map<String,java.util.Set<Dependency>> getDependencies() {
            return (_dependencies);
        }

        public java.util.Set<Dependency> getDependencies(String component) {
            if (_dependencies.containsKey(component)) {
                return (_dependencies.get(component));
            }

            return (Collections.<Dependency>emptySet());
        }
    }
}
