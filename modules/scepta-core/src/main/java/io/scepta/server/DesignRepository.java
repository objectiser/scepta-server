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
import io.scepta.model.Tag;

// QUESTIONS:

// How to deal with user authoriztion? E.g. who can add a policy to a particular group?
// Who "owns" the org, group, etc? Is it managed outside the dev server, or provided by jaas
// and special use of the roles associated with a user - but not sure how dynamic that would be?
// If using keycloak, it might require tooling to update the kc db of what a user's role is.
// could add write privileged user list to org, so only those users can change the org and add
// other users (for example) and add new groups. Then the group could also have a list of users
// that are permitted to add/remove policies. then same possibly at the policy level - although
// possibly owners of the org and group automatically have write permission on a policy?
// May be best to add user details in the model, as otherwise auth config could be complex.
// How can we reliably know who the current user is? Or does there have to be a level of trust?
// (i.e. passing the user details in operations, or verifying authorized user outside of the
// DevServer API? But then how to make consistent across uses of this API?

public interface DesignRepository {

    String MASTER_TAG = "master";

    java.util.Set<Organization> getOrganizations();

    void addOrganization(Organization org);

    void updateOrganization(Organization org);

    Organization getOrganization(String name);

    void removeOrganization(String name);

    /**
     * This method returns the list of 'master' policy groups.
     *
     * @param org The organization
     * @return The list of 'master' policy groups within the specified organization
     */
    java.util.Set<PolicyGroup> getPolicyGroups(String org);

    void addPolicyGroup(String org, PolicyGroup group);

    void updatePolicyGroup(String org, PolicyGroup group);

    /**
     * This method returns the policy group associated with the
     * supplied name, within the specified organization and
     * associated with the optional tag. If the tag is specified
     * then it will return the policy group with that tag value.
     * However if not specified, then the current instance of the
     * policy group will be returned.
     *
     * @param org The organization
     * @param group The name of the policy group
     * @param tag The optional tag
     * @return The policy group, or null if no found
     */
    PolicyGroup getPolicyGroup(String org, String group, String tag);

    void importPolicyGroup(String org, PolicyGroupInterchange defn);

    PolicyGroupInterchange exportPolicyGroup(String org, String group, String tag);

    /**
     * This method will remove a policy group, and associated policies,
     * endpoints, etc. This task will only remove the current version
     * of a policy group (if exists), but not any tagged versions.
     *
     * @param org The organization
     * @param group The policy group name
     */
    void removePolicyGroup(String org, String group);

    java.util.Set<Policy> getPolicies(String org, String group, String tag);

    void addPolicy(String org, String group, Policy policy);

    void updatePolicy(String org, String group, Policy policy);

    Policy getPolicy(String org, String group, String tag, String policy);

    String getPolicyDefinition(String org, String group, String tag, String policy);

    void setPolicyDefinition(String org, String group, String policy, String definition);

    String getResourceDefinition(String org, String group, String tag, String policy, String resource);

    void setResourceDefinition(String org, String group, String policy, String resource, String definition);

    void removePolicy(String org, String group, String policy);

    /**
     * This method tags the policy group, within the named organization,
     * using the optionally specified tag. If the tag is not supplied,
     * then one will be created.
     *
     * @param org The organization
     * @param group The policy group
     * @param tag The optional tag
     * @param description The description to associate with the tag
     * @return The tag
     */
    Tag createTag(String org, String group, String tag, String description);

    java.util.List<Tag> getTags(String org, String group);

    Tag getTag(String org, String group, String tag);

    void updateTag(String org, String group, Tag tag);

    void removeTag(String org, String group, String tag);

}
