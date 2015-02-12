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
package io.scepta.design.server;

import io.scepta.design.model.Issue;

import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * This class represents a wrapper for the generated executable
 * representation of a policy group, and any issues that were
 * detected during the generation procedure.
 *
 */
public class GeneratedResult {

    private java.util.Map<String,WebArchive> _generated=new java.util.HashMap<String,WebArchive>();
    private java.util.List<Issue> _issues=new java.util.ArrayList<Issue>();
    private PolicyGroupInterchange _group;

    public GeneratedResult(PolicyGroupInterchange group) {
        _group = group;
    }

    public PolicyGroupInterchange getGroup() {
        return (_group);
    }

    public java.util.Map<String,WebArchive> getGenerated() {
        return (_generated);
    }

    public GeneratedResult setGenerated(java.util.Map<String,WebArchive> gen) {
        _generated = gen;

        return (this);
    }

    public java.util.List<Issue> getIssues() {
        return (_issues);
    }

    public GeneratedResult setIssues(java.util.List<Issue> issues) {
        _issues = issues;

        return (this);
    }

}
