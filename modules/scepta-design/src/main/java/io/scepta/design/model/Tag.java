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
package io.scepta.design.model;

public class Tag {

    private String _name;
    private String _description;
    private long _createdTimestamp;
    private String _createdBy;

    public String getName() {
        return (_name);
    }

    public Tag setName(String name) {
        _name = name;
        return (this);
    }

    public String getDescription() {
        return (_description);
    }

    public Tag setDescription(String description) {
        _description = description;
        return (this);
    }

    public long getCreatedTimestamp() {
        return (_createdTimestamp);
    }

    public Tag setCreatedTimestamp(long timestamp) {
        _createdTimestamp = timestamp;
        return (this);
    }

    public String getCreatedBy() {
        return (_createdBy);
    }

    public Tag setCreatedBy(String creator) {
        _createdBy = creator;
        return (this);
    }
}

