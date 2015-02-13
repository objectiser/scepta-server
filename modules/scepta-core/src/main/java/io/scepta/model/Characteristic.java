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

public class Characteristic {

    private String _type;
    private java.util.Map<String, String> _properties=new java.util.HashMap<String, String>();

    public String getType() {
        return (_type);
    }

    public Characteristic setType(String type) {
        _type = type;
        return (this);
    }

    public java.util.Map<String, String> getProperties() {
        return (_properties);
    }

    public Characteristic setProperties(java.util.Map<String, String> properties) {
        _properties = properties;
        return (this);
    }
}
