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

public class PolicyGroup {

    private String _name;
    private String _description;
    private java.util.Set<Endpoint> _endpoints=new java.util.HashSet<Endpoint>();

    public String getName() {
        return (_name);
    }

    public PolicyGroup setName(String name) {
        _name = name;
        return (this);
    }

    public String getDescription() {
        return (_description);
    }

    public PolicyGroup setDescription(String description) {
        _description = description;
        return (this);
    }

    public java.util.Set<Endpoint> getEndpoints() {
        return (_endpoints);
    }

    public PolicyGroup setEndpoints(java.util.Set<Endpoint> endpoints) {
        _endpoints = endpoints;
        return (this);
    }

    /**
     * This method returns the endpoint associated with the
     * supplied name.
     *
     * @param name The endpoint name
     * @return The endpoint, or null if not found
     */
    public Endpoint getEndpoint(String name) {
        for (Endpoint endpoint : _endpoints) {
            if (endpoint.getName().equals(name)) {
                return (endpoint);
            }
        }
        return (null);
    }
}
