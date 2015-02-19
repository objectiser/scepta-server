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
package io.scepta.model;

public class Policy {

    private String _name;
    private String _description;
    private java.util.Set<Dependency> _dependencies=new java.util.HashSet<Dependency>();
    private java.util.Set<Resource> _resources=new java.util.HashSet<Resource>();

    public String getName() {
        return (_name);
    }

    public Policy setName(String name) {
        _name = name;
        return (this);
    }

    public String getDescription() {
        return (_description);
    }

    public Policy setDescription(String description) {
        _description = description;
        return (this);
    }

    public java.util.Set<Dependency> getDependencies() {
        return (_dependencies);
    }

    public Policy setDependencies(java.util.Set<Dependency> dependencies) {
        _dependencies = dependencies;
        return (this);
    }

    public Policy addDependency(Dependency dependency) {
        _dependencies.add(dependency);
        return (this);
    }

    public Policy removeDependency(Dependency dependency) {
        _dependencies.remove(dependency);
        return (this);
    }

    public java.util.Set<Resource> getResources() {
        return (_resources);
    }

    public Policy addResource(Resource resource) {
        _resources.add(resource);
        return (this);
    }

    public Policy removeResource(Resource resource) {
        _resources.remove(resource);
        return (this);
    }

    public Policy setReources(java.util.Set<Resource> resources) {
        _resources = resources;
        return (this);
    }

    public Resource getResource(String name) {
        for (Resource res : _resources) {
            if (res.getName().equals(name)) {
                return (res);
            }
        }

        return (null);
    }
}
