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

public class Resource {

    private String _name;
    private String _description;
    private java.util.Set<Dependency> _dependencies=new java.util.HashSet<Dependency>();

    public String getName() {
        return (_name);
    }

    public Resource setName(String name) {
        _name = name;
        return (this);
    }

    public String getDescription() {
        return (_description);
    }

    public Resource setDescription(String description) {
        _description = description;
        return (this);
    }

    public java.util.Set<Dependency> getDependencies() {
        return (_dependencies);
    }

    public Resource setDependencies(java.util.Set<Dependency> dependencies) {
        _dependencies = dependencies;
        return (this);
    }

    public Resource addDependency(Dependency dependency) {
        _dependencies.add(dependency);
        return (this);
    }

    public Resource removeDependency(Dependency dependency) {
        _dependencies.remove(dependency);
        return (this);
    }
}
