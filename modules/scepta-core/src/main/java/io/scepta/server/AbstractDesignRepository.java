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

import java.text.SimpleDateFormat;

import io.scepta.model.Organization;
import io.scepta.model.Policy;
import io.scepta.model.PolicyGroup;
import io.scepta.model.Resource;
import io.scepta.model.Tag;
import io.scepta.model.Tag.BuildStatus;

public abstract class AbstractDesignRepository implements DesignRepository {

    private static final SimpleDateFormat DATE_FORMATTER=new SimpleDateFormat("yyyyMMddHHmmss");

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
    public void removeOrganization(String name) {

        // Check current user has access to the organization

        doRemoveOrganization(name);
    }

    protected abstract void doRemoveOrganization(String name);

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
            doAddPolicyGroup(org, group, MASTER_TAG);
        } else {
            // TODO: Throw exception?
            throw new RuntimeException("Policy group already exists");
        }
    }

    protected abstract void doAddPolicyGroup(String org, PolicyGroup group, String tag);

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

    public void importPolicyGroup(String org, PolicyGroupInterchange defn) {

        // TODO: Check permission

        // Check if policy group already exists with the specific organization

        if (getPolicyGroup(org, defn.getGroupDetails().getName(), MASTER_TAG) != null) {

            // TODO: REPORT EXCEPTION
            throw new RuntimeException("Policy '"+defn.getGroupDetails().getName()
                    +"' already exists in organization '"+org+"'");
        }

        addPolicyGroup(org, defn.getGroupDetails());

        for (Policy p : defn.getPolicyDetails()) {
            addPolicy(org, defn.getGroupDetails().getName(), p);

            String policyDefn=defn.getPolicyDefinitions().get(p.getName());

            if (policyDefn != null) {
                setPolicyDefinition(org, defn.getGroupDetails().getName(), p.getName(), policyDefn);
            }

            for (Resource r : p.getResources()) {
                String resDefn=defn.getResourceDefinitions().get(r.getName());

                if (resDefn != null) {
                    setResourceDefinition(org, defn.getGroupDetails().getName(),
                            p.getName(), r.getName(), resDefn);
                }
            }
        }
    }

    public PolicyGroupInterchange exportPolicyGroup(String org, String group, String tag) {
        PolicyGroupInterchange defn=new PolicyGroupInterchange()
            .setGroupDetails(getPolicyGroup(org, group, tag))
            .setPolicyDetails(getPolicies(org, group, tag));

        // For each policy, we need to export its definition and its resource definitions
        java.util.Map<String,String> policyDefns=new java.util.HashMap<String,String>();
        java.util.Map<String,String> resourceDefns=new java.util.HashMap<String,String>();

        for (Policy p : defn.getPolicyDetails()) {
            policyDefns.put(p.getName(), getPolicyDefinition(org, group, tag, p.getName()));

            for (Resource r : p.getResources()) {
                String existingResource=resourceDefns.get(r.getName());
                String currentResource=getResourceDefinition(org, group, tag,
                                    p.getName(), r.getName());
                if (existingResource != null) {
                    // Check if resource content is the same
                    if (!existingResource.equals(currentResource)) {
                        // TODO: Deal with exception
                        throw new RuntimeException("Resource '"+r.getName()
                                +"' used in multiple policies but with different content");
                    }
                } else {
                    resourceDefns.put(r.getName(), currentResource);
                }
            }
        }

        defn.setPolicyDefinitions(policyDefns).setResourceDefinitions(resourceDefns);

        return (defn);
    }

    /**
     * {@inheritDoc}
     */
    public void removePolicyGroup(String org, String group) {

        // TODO: Check permission

        doRemovePolicyGroup(org, group);
    }

    protected abstract void doRemovePolicyGroup(String org, String group);

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
        if (getPolicy(org, group, MASTER_TAG, policy.getName()) == null) {
            doAddPolicy(org, group, MASTER_TAG, policy);
        } else {
            // TODO: Throw exception?
            throw new RuntimeException("Policy already exists");
        }
    }

    protected abstract void doAddPolicy(String org, String group, String tag, Policy policy);

    /**
     * {@inheritDoc}
     */
    public void updatePolicy(String org, String group, Policy policy) {

        // TODO: Check permission

        // Check if policy already exists
        Policy p=getPolicy(org, group, MASTER_TAG, policy.getName());

        if (p != null) {
            doUpdatePolicy(org, group, policy);

            // Check if any resources removed
            for (Resource res : p.getResources()) {
                if (policy.getResource(res.getName()) == null) {
                    doRemoveResourceDefinition(org, group, policy.getName(), res.getName());
                }
            }
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
    public void removePolicy(String org, String group, String policy) {

        // TODO: Check permitted to remove the policy

        doRemovePolicy(org, group, policy);
    }

    protected abstract void doRemovePolicy(String org, String group, String policy);

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

        if (getPolicy(org, group, MASTER_TAG, policy) == null) {
            // TODO: POLICY NOT FOUND
            throw new RuntimeException("Policy not found");
        }

        doSetPolicyDefinition(org, group, MASTER_TAG, policy, definition);
    }

    protected abstract void doSetPolicyDefinition(String org, String group, String tag,
                            String policy, String definition);

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

        doSetResourceDefinition(org, group, MASTER_TAG, policy, resource, definition);
    }

    protected abstract void doSetResourceDefinition(String org, String group, String tag, String policy,
                String resource, String definition);

    protected abstract void doRemoveResourceDefinition(String org, String group, String policy, String resource);

    /**
     * {@inheritDoc}
     */
    public Tag createTag(String org, String group, String tagName, String description) {
        // If tag not specified then create one based on the current date and time
        if (tagName == null || tagName.trim().length() == 0) {
            tagName = DATE_FORMATTER.format(new java.util.Date());
        } else if (tagName == MASTER_TAG) {

            // TODO: EXCEPTION
            throw new RuntimeException("Cannot tag using 'master'");
        }

        // Copy 'master' tagged information to specified tag
        PolicyGroup pg=getPolicyGroup(org, group, MASTER_TAG);

        if (pg != null) {
            doAddPolicyGroup(org, pg, tagName);

            for (Policy p : getPolicies(org, group, MASTER_TAG)) {
                doAddPolicy(org, group, tagName, p);

                String pdefn=getPolicyDefinition(org, group, MASTER_TAG, p.getName());

                if (pdefn != null) {
                    doSetPolicyDefinition(org, group, tagName, p.getName(), pdefn);
                }

                for (Resource r : p.getResources()) {
                    String rdefn=getResourceDefinition(org, group, MASTER_TAG, p.getName(), r.getName());

                    if (rdefn != null) {
                        doSetResourceDefinition(org, group, tagName, p.getName(), r.getName(), rdefn);
                    }
                }
            }

            // Create tag representation
            Tag tag=new Tag()
                    .setName(tagName)
                    .setDescription(description)
                    .setCreatedTimestamp(System.currentTimeMillis())
                    .setCreatedBy("UNKNOWN")
                    .setBuildStatus(BuildStatus.Created)
                    .setBuildTimestamp(System.currentTimeMillis());

            doCreateTag(org, group, tag);

            return (tag);
        } else {
            throw new RuntimeException("Unable to find policy group");
        }
    }

    protected abstract void doCreateTag(String org, String group, Tag tag);

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
    public Tag getTag(String org, String group, String tag) {

        // TODO: Check permission

        return (doGetTag(org, group, tag));
    }

    protected abstract Tag doGetTag(String org, String group, String tag);

    /**
     * {@inheritDoc}
     */
    public void updateTag(String org, String group, Tag tag) {

        // TODO: Check permission

        doUpdateTag(org, group, tag);
    }

    protected abstract void doUpdateTag(String org, String group, Tag tag);

    /**
     * {@inheritDoc}
     */
    public void removeTag(String org, String group, String tag) {

        // TODO: Check permission

        doRemoveTag(org, group, tag);
    }

    protected abstract void doRemoveTag(String org, String group, String tag);

}
