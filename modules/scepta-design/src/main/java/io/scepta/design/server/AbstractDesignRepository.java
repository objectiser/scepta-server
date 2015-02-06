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

import io.scepta.design.model.Organization;
import io.scepta.design.model.Policy;
import io.scepta.design.model.PolicyGroup;
import io.scepta.design.model.Tag;

public abstract class AbstractDesignRepository implements DesignRepository {

    /**
     * {@inheritDoc}
     */
    public final java.util.Set<Organization> getOrganizations() {
        return (doGetOrganizations());
    }

    protected abstract java.util.Set<Organization> doGetOrganizations();

    /**
     * {@inheritDoc}
     */
    public void addOrganization(Organization org) {

        // Check if user has permission to add a new organization

        // Check if organization already exists
        if (getOrganization(org.getName()) == null) {
            doAddOrganization(org);
        } else {
            // TODO: Throw exception?
            throw new RuntimeException("Organization already exists");
        }
    }

    protected abstract void doAddOrganization(Organization org);

    /**
     * {@inheritDoc}
     */
    public void updateOrganization(Organization org) {

        // Check if user has permission to update an organization

        // Check if organization does not exists
        if (getOrganization(org.getName()) != null) {
            doUpdateOrganization(org);
        } else {
            // TODO: Throw exception?
            throw new RuntimeException("Organization does not exists");
        }
    }

    protected abstract void doUpdateOrganization(Organization org);

    /**
     * {@inheritDoc}
     */
    public Organization getOrganization(String name) {

        // Check current user has access to the organization

        return (doGetOrganization(name));
    }

    protected abstract Organization doGetOrganization(String name);

    /**
     * {@inheritDoc}
     */
    public Organization removeOrganization(String name) {

        // Check current user has access to the organization

        return (doRemoveOrganization(name));
    }

    protected abstract Organization doRemoveOrganization(String name);

    /**
     * {@inheritDoc}
     */
    public java.util.Set<PolicyGroup> getPolicyGroups(String org) {

        // Check current user has access to the organization

        return (doGetPolicyGroups(org));
    }

    protected abstract java.util.Set<PolicyGroup> doGetPolicyGroups(String org);

    /**
     * {@inheritDoc}
     */
    public void addPolicyGroup(String org, PolicyGroup group) {

        // TODO: Check permission

        // Check if policy group does not exists
        if (getPolicyGroup(org, group.getName(), MASTER_TAG) == null) {
            doAddPolicyGroup(org, group);
        } else {
            // TODO: Throw exception?
            throw new RuntimeException("Policy group already exists");
        }
    }

    protected abstract void doAddPolicyGroup(String org, PolicyGroup group);

    /**
     * {@inheritDoc}
     */
    public void updatePolicyGroup(String org, PolicyGroup group) {

        // TODO: Check permission

        // Check if policy group already exists
        if (getPolicyGroup(org, group.getName(), MASTER_TAG) != null) {
            doUpdatePolicyGroup(org, group);
        } else {
            // TODO: Throw exception?
            throw new RuntimeException("Policy group does not exists");
        }
    }

    protected abstract void doUpdatePolicyGroup(String org, PolicyGroup group);

    /**
     * {@inheritDoc}
     */
    public PolicyGroup getPolicyGroup(String org, String group, String tag) {

        // TODO: Check permission

        return (doGetPolicyGroup(org, group, (tag == null ? MASTER_TAG : tag)));
    }

    protected abstract PolicyGroup doGetPolicyGroup(String org, String group, String tag);

    /**
     * {@inheritDoc}
     */
    public PolicyGroup removePolicyGroup(String org, String group) {

        // TODO: Check permission

        return (doRemovePolicyGroup(org, group));
    }

    protected abstract PolicyGroup doRemovePolicyGroup(String org, String group);

    /**
     * {@inheritDoc}
     */
    public java.util.List<Tag> getTags(String org, String group) {

        // TODO: Check permission

        return (doGetTags(org, group));
    }

    protected abstract java.util.List<Tag> doGetTags(String org, String group);

    /**
     * {@inheritDoc}
     */
    public java.util.Set<Policy> getPolicies(String org, String group, String tag) {

        // TODO: Check permission

        return (doGetPolicies(org, group, (tag == null ? MASTER_TAG : tag)));
    }

    protected abstract java.util.Set<Policy> doGetPolicies(String org, String group, String tag);

    /**
     * {@inheritDoc}
     */
    public void addPolicy(String org, String group, Policy policy) {

        // TODO: Check permission

        // Check if policy does not exists
        if (getPolicy(org, group, policy.getName(), MASTER_TAG) == null) {
            doAddPolicy(org, group, policy);
        } else {
            // TODO: Throw exception?
            throw new RuntimeException("Policy already exists");
        }
    }

    protected abstract void doAddPolicy(String org, String group, Policy policy);

    /**
     * {@inheritDoc}
     */
    public void updatePolicy(String org, String group, Policy policy) {

        // TODO: Check permission

        // Check if policy already exists
        if (getPolicy(org, group, MASTER_TAG, policy.getName()) != null) {
            doUpdatePolicy(org, group, policy);
        } else {
            // TODO: Throw exception?
            throw new RuntimeException("Policy does not exists");
        }
    }

    protected abstract void doUpdatePolicy(String org, String group, Policy policy);

    /**
     * {@inheritDoc}
     */
    public Policy getPolicy(String org, String group, String tag, String policy) {

        // TODO: Check permission

        return (doGetPolicy(org, group, (tag == null ? MASTER_TAG : tag), policy));
    }

    protected abstract Policy doGetPolicy(String org, String group, String tag, String policy);

    /**
     * {@inheritDoc}
     */
    public String getPolicyDefinition(String org, String group, String tag, String policy) {

        // TODO: Check permission

        return (doGetPolicyDefinition(org, group, (tag == null ? MASTER_TAG : tag), policy));
    }

    protected abstract String doGetPolicyDefinition(String org, String group, String tag, String policy);

    /**
     * {@inheritDoc}
     */
    public void setPolicyDefinition(String org, String group, String policy, String definition) {

        // TODO: Check permission

        doSetPolicyDefinition(org, group, policy, definition);
    }

    protected abstract void doSetPolicyDefinition(String org, String group, String policy, String definition);

    /**
     * {@inheritDoc}
     */
    public String getResourceDefinition(String org, String group, String tag, String policy, String resource) {

        // TODO: Check permission

        return (doGetResourceDefinition(org, group, (tag == null ? MASTER_TAG : tag), policy, resource));
    }

    protected abstract String doGetResourceDefinition(String org, String group, String tag, String policy,
                String resource);

    /**
     * {@inheritDoc}
     */
    public void setResourceDefinition(String org, String group, String policy, String resource, String definition) {

        // TODO: Check permission

        doSetResourceDefinition(org, group, policy, resource, definition);
    }

    protected abstract void doSetResourceDefinition(String org, String group, String policy,
                String resource, String definition);

    /**
     * {@inheritDoc}
     */
    public Policy removePolicy(String org, String group, String policy) {

        // TODO: Check permitted to remove the policy

        return (doRemovePolicy(org, group, policy));
    }

    protected abstract Policy doRemovePolicy(String org, String group, String policy);

}
