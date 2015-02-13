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

public class Endpoint {

    private String _name;
    private String _uri;
    private String _description;
    private java.util.List<Characteristic> _characteristics=new java.util.ArrayList<Characteristic>();
    private java.util.Set<Dependency> _dependencies=new java.util.HashSet<Dependency>();
    private java.util.Map<String, String> _consumerOptions=new java.util.HashMap<String, String>();
    private java.util.Map<String, String> _producerOptions=new java.util.HashMap<String, String>();

    public String getName() {
        return (_name);
    }

    public Endpoint setName(String name) {
        _name = name;
        return (this);
    }

    public String getURI() {
        return (_uri);
    }

    public Endpoint setURI(String uri) {
        _uri = uri;
        return (this);
    }

    public String getDescription() {
        return (_description);
    }

    public Endpoint setDescription(String description) {
        _description = description;
        return (this);
    }

    public java.util.List<Characteristic> getCharacteristics() {
        return (_characteristics);
    }

    public Endpoint setCharacteristics(java.util.List<Characteristic> characteristics) {
        _characteristics = characteristics;
        return (this);
    }

    public boolean hasCharacteristic(String type) {
        for (Characteristic c : _characteristics) {
            if (c.getType().equals(type)) {
                return (true);
            }
        }
        return (false);
    }

    public java.util.Set<Dependency> getDependencies() {
        return (_dependencies);
    }

    public Endpoint setDependencies(java.util.Set<Dependency> dependencies) {
        _dependencies = dependencies;
        return (this);
    }

    public java.util.Map<String, String> getConsumerOptions() {
        return (_consumerOptions);
    }

    public Endpoint setConsumerOptions(java.util.Map<String, String> consumerOptions) {
        _consumerOptions = consumerOptions;
        return (this);
    }

    public java.util.Map<String, String> getProducerOptions() {
        return (_producerOptions);
    }

    public Endpoint setProducerOptions(java.util.Map<String, String> producerOptions) {
        _producerOptions = producerOptions;
        return (this);
    }
}
