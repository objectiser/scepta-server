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

import io.scepta.model.Policy;
import io.scepta.model.PolicyGroup;

public class PolicyGroupInterchange {

    private PolicyGroup _groupDetails;
    private java.util.Set<Policy> _policyDetails;
    private java.util.Map<String,String> _policyDefinitions;
    private java.util.Map<String,String> _resourceDefinitions;

    public PolicyGroup getGroupDetails() {
        return (_groupDetails);
    }

    public PolicyGroupInterchange setGroupDetails(PolicyGroup groupDetails) {
        _groupDetails = groupDetails;

        return (this);
    }

    public java.util.Set<Policy> getPolicyDetails() {
        return (_policyDetails);
    }

    public PolicyGroupInterchange setPolicyDetails(java.util.Set<Policy> policyDetails) {
        _policyDetails = policyDetails;

        return (this);
    }

    public java.util.Map<String,String> getPolicyDefinitions() {
        return (_policyDefinitions);
    }

    public PolicyGroupInterchange setPolicyDefinitions(java.util.Map<String,String> policyDefinitions) {
        _policyDefinitions = policyDefinitions;

        return (this);
    }

    public java.util.Map<String,String> getResourceDefinitions() {
        return (_resourceDefinitions);
    }

    public PolicyGroupInterchange setResourceDefinitions(java.util.Map<String,String> resourceDefinitions) {
        _resourceDefinitions = resourceDefinitions;

        return (this);
    }
}
