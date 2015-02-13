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

import io.scepta.model.Organization;
import io.scepta.model.Policy;
import io.scepta.model.PolicyGroup;
import io.scepta.model.Resource;
import io.scepta.model.Tag;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class InMemoryDesignRepository extends AbstractDesignRepository {

    private java.util.Set<Organization> _organizations=new java.util.HashSet<Organization>();
    private java.util.Map<Organization, java.util.Set<PolicyGroup>> _groups=
            new java.util.HashMap<Organization, java.util.Set<PolicyGroup>>();
    private java.util.Map<PolicyGroup, java.util.Set<Policy>> _policies=
            new java.util.HashMap<PolicyGroup, java.util.Set<Policy>>();
    private java.util.Map<Policy, String> _policyDefn=new java.util.HashMap<Policy, String>();
    private java.util.Map<Resource, String> _resourceDefn=new java.util.HashMap<Resource, String>();

    /**
     * The default constructor.
     */
    public InMemoryDesignRepository() {
        // Create default organization
        _organizations.add(new Organization().setName("default"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected java.util.Set<Organization> doGetOrganizations() {
        return (Collections.unmodifiableSet(_organizations));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAddOrganization(Organization org) {
        _organizations.add(org);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUpdateOrganization(Organization org) {
        doRemoveOrganization(org.getName());
        doAddOrganization(org);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Organization doGetOrganization(String name) {
        for (Organization org : _organizations) {
            if (org.getName().equals(name)) {
                return (org);
            }
        }
        return (null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRemoveOrganization(String name) {
        boolean removed=false;

        for (Organization org : _organizations) {
            if (org.getName().equals(name)) {
                removed = _organizations.remove(org);
            }
        }

        if (!removed) {
            // TODO: EXCEPTION?
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<PolicyGroup> doGetPolicyGroups(String org) {
        Organization o=doGetOrganization(org);

        if (o != null && _groups.containsKey(o)) {
            return (_groups.get(o));
        }

        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAddPolicyGroup(String org, PolicyGroup group, String tag) {
        Organization o=doGetOrganization(org);

        if (o != null) {
            java.util.Set<PolicyGroup> groups=_groups.get(o);

            if (groups == null) {
                groups = new java.util.HashSet<PolicyGroup>();
                _groups.put(o, groups);
            }

            groups.add(group);
        } else {
            // TODO: Throw exception?
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUpdatePolicyGroup(String org, PolicyGroup group) {
        doRemovePolicyGroup(org, group.getName());
        doAddPolicyGroup(org, group, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PolicyGroup doGetPolicyGroup(String org, String group, String tag) {
        Organization o=doGetOrganization(org);

        // TODO: Currently doesn't deal with tags

        if (o != null && _groups.containsKey(o)) {
            for (PolicyGroup pg : _groups.get(o)) {
                if (pg.getName().equals(group)) {
                    return (pg);
                }
            }
        }

        return (null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRemovePolicyGroup(String org, String group) {
        boolean removed=false;

        Organization o=doGetOrganization(org);

        // TODO: Currently doesn't deal with tags

        if (o != null && _groups.containsKey(o)) {
            for (PolicyGroup pg : _groups.get(o)) {
                if (pg.getName().equals(group)) {
                    removed = (_groups.remove(pg) != null);
                }
            }
        }

        if (!removed) {
            // TODO: EXCEPTION?
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<Policy> doGetPolicies(String org, String group, String tag) {
        PolicyGroup pg=doGetPolicyGroup(org, group, tag);

        if (pg != null && _policies.containsKey(pg)) {
            return (_policies.get(pg));
        }

        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAddPolicy(String org, String group, String tag, Policy policy) {
        PolicyGroup pg=doGetPolicyGroup(org, group, null);

        if (pg != null) {
            java.util.Set<Policy> policies=_policies.get(pg);
            if (policies == null) {
                policies = new java.util.HashSet<Policy>();
                _policies.put(pg, policies);
            }

            policies.add(policy);
        } else {
            // TODO: Throw exception?
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUpdatePolicy(String org, String group, Policy policy) {
        doRemovePolicy(org, group, policy.getName());
        doAddPolicy(org, group, null, policy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Policy doGetPolicy(String org, String group, String tag, String policy) {
        PolicyGroup pg=doGetPolicyGroup(org, group, tag);

        if (pg != null && _policies.containsKey(pg)) {
            for (Policy pol : _policies.get(pg)) {
                if (pol.getName().equals(policy)) {
                    return (pol);
                }
            }
        }

        return (null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRemovePolicy(String org, String group, String policy) {
        boolean removed=false;

        PolicyGroup pg=doGetPolicyGroup(org, group, null);

        if (pg != null && _policies.containsKey(pg)) {
            for (Policy pol : _policies.get(pg)) {
                if (pol.getName().equals(policy)) {
                    removed = (_policies.remove(pol) != null);
                }
            }
        }

        if (!removed) {
            // TODO: EXCEPTION?
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetPolicyDefinition(String org, String group, String tag, String policy) {
        Policy p=doGetPolicy(org, group, tag, policy);

        if (p != null && _policyDefn.containsKey(p)) {
            return (_policyDefn.get(p));
        }

        return (null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetPolicyDefinition(String org, String group, String tag, String policy, String definition) {
        Policy p=doGetPolicy(org, group, null, policy);

        if (p != null) {
            _policyDefn.put(p, definition);
            return;
        }

        // Exception ???
        throw new RuntimeException("Unable to find policy '"+policy+"'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetResourceDefinition(String org, String group, String tag, String policy,
            String resource) {
        Policy p=doGetPolicy(org, group, tag, policy);

        if (p != null) {
            // Retrieve resource
            for (Resource r : p.getResources()) {
                if (r.getName().equals(resource)) {
                    return (_resourceDefn.get(r));
                }
            }
        }

        return (null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetResourceDefinition(String org, String group, String tag, String policy, String resource,
            String definition) {
        Policy p=doGetPolicy(org, group, null, policy);

        if (p != null) {
            // Retrieve resource
            for (Resource r : p.getResources()) {
                if (r.getName().equals(resource)) {
                    _resourceDefn.put(r, definition);
                    return;
                }
            }
        }

        // Exception ???
        throw new RuntimeException("Unable to find resource '"+resource+"'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRemoveResourceDefinition(String org, String group, String policy, String resource) {
        boolean removed=false;

        Policy p=doGetPolicy(org, group, null, policy);

        if (p != null) {
            // Retrieve resource
            for (Resource r : p.getResources()) {
                if (r.getName().equals(resource)) {
                    _resourceDefn.remove(r);
                    return;
                }
            }
        }

        if (!removed) {
            // TODO: EXCEPTION?
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doCreateTag(String org, String group, Tag tag) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Tag> doGetTags(String org, String group) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Tag doGetTag(String org, String group, String tag) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUpdateTag(String org, String group, Tag tag) {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRemoveTag(String org, String group, String tag) {
        // TODO Auto-generated method stub
    }
}
