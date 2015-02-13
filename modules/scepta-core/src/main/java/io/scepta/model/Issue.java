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

public class Issue {

    private String _organization;
    private String _group;
    private String _policy;
    private String _resource;

    private int _startLine;
    private int _startColumn;
    private int _endLine;
    private int _endColumn;

    private String _severity;
    private String _description;

    public String getOrganization() {
        return (_organization);
    }

    public Issue setOrganization(String org) {
        _organization = org;

        return (this);
    }

    public String getGroup() {
        return (_group);
    }

    public Issue setGroup(String group) {
        _group = group;

        return (this);
    }

    public String getPolicy() {
        return (_policy);
    }

    public Issue setPolicy(String policy) {
        _policy = policy;

        return (this);
    }

    public String getResource() {
        return (_resource);
    }

    public Issue setResource(String resource) {
        _resource = resource;

        return (this);
    }

    public int getStartLine() {
        return (_startLine);
    }

    public Issue setStartLine(int line) {
        _startLine = line;

        return (this);
    }

    public int getStartColumn() {
        return (_startColumn);
    }

    public Issue setStartColumn(int col) {
        _startColumn = col;

        return (this);
    }

    public int getEndLine() {
        return (_endLine);
    }

    public Issue setEndLine(int line) {
        _endLine = line;

        return (this);
    }

    public int getEndColumn() {
        return (_endColumn);
    }

    public Issue setEndColumn(int col) {
        _endColumn = col;

        return (this);
    }

    public String getSeverity() {
        return (_severity);
    }

    public Issue setSeverity(String severity) {
        _severity = severity;

        return (this);
    }

    public String getDescription() {
        return (_description);
    }

    public Issue setDescription(String desc) {
        _description = desc;

        return (this);
    }

}
