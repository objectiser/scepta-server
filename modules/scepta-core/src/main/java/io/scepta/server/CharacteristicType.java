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
package io.scepta.server;

public class CharacteristicType {

    private String _name;
    private java.util.Map<String, CharacteristicType.PropertyDescriptor> _propertyDescriptors=
            new java.util.HashMap<String, CharacteristicType.PropertyDescriptor>();

    public String getName() {
        return (_name);
    }

    public CharacteristicType setName(String name) {
        _name = name;
        return (this);
    }

    public java.util.Map<String, PropertyDescriptor> getPropertyDescriptors() {
        return (_propertyDescriptors);
    }

    public CharacteristicType setPropertyDescriptors(java.util.Map<String, PropertyDescriptor> pd) {
        _propertyDescriptors = pd;
        return (this);
    }

    public static class PropertyDescriptor {

        private boolean _mandatory=false;
        private String _defaultValue=null;

        public boolean getMandatory() {
            return (_mandatory);
        }

        public PropertyDescriptor setMandatory(boolean b) {
            _mandatory = b;
            return (this);
        }

        public String getDefaultValue() {
            return (_defaultValue);
        }

        public PropertyDescriptor setDefaultValue(String def) {
            _defaultValue = def;
            return (this);
        }
    }
}
