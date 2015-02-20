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
package io.scepta.design.server.cassandra;

import io.scepta.model.Organization;
import io.scepta.model.Policy;
import io.scepta.model.PolicyGroup;
import io.scepta.model.Tag;
import io.scepta.server.AbstractDesignRepository;
import io.scepta.server.DesignRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CassandraDesignRepository extends AbstractDesignRepository {

    private static final String KEYSPACE = "scepta";

    private Cluster _cluster;
    private Session _session;

    private static final ObjectMapper MAPPER=new ObjectMapper();

    private PreparedStatement _insertOrganization;
    private PreparedStatement _insertPolicyGroup;
    private PreparedStatement _insertPolicy;
    private PreparedStatement _insertPolicyDefinition;
    private PreparedStatement _insertResourceDefinition;
    private PreparedStatement _insertTag;
    private PreparedStatement _updateOrganization;
    private PreparedStatement _updatePolicyGroup;
    private PreparedStatement _updatePolicy;
    private PreparedStatement _updatePolicyDefinition;
    private PreparedStatement _updateResourceDefinition;
    private PreparedStatement _updateTag;
    private PreparedStatement _removeOrganization;
    private PreparedStatement _removePolicyGroup;
    private PreparedStatement _removePolicy;
    private PreparedStatement _removePoliciesForTag;
    private PreparedStatement _removePolicyDefinition;
    private PreparedStatement _removePolicyDefinitionsForTag;
    private PreparedStatement _removeResourceDefinition;
    private PreparedStatement _removeResourceDefinitionsForPolicy;
    private PreparedStatement _removeResourceDefinitionsForTag;
    private PreparedStatement _removeTag;

    /**
     * The default constructor.
     */
    public CassandraDesignRepository() {
        connect();
        initStatements();
    }

    protected void connect() {
        // Initialize cassandra
        _cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
        _session = _cluster.connect(KEYSPACE);
    }

    protected void initStatements() {
        // Create prepared statements
        _insertOrganization = _session.prepare(
                "INSERT INTO scepta.organizations " +
                "(organization, data) " +
                "VALUES (?,?);");

        _insertPolicyGroup = _session.prepare(
                "INSERT INTO scepta.policygroups " +
                "(organization, group, tag, data)" +
                "VALUES (?,?,?,?);");

        _insertPolicy = _session.prepare(
                "INSERT INTO scepta.policies " +
                "(organization, group, tag, policy, data)" +
                "VALUES (?,?,?,?,?);");

        _insertPolicyDefinition = _session.prepare(
                "INSERT INTO scepta.policydefns " +
                "(organization, group, tag, policy, data)" +
                "VALUES (?,?,?,?,?);");

        _insertResourceDefinition = _session.prepare(
                "INSERT INTO scepta.resourcedefns " +
                "(organization, group, tag, policy, resource, data)" +
                "VALUES (?,?,?,?,?,?);");

        _insertTag = _session.prepare(
                "INSERT INTO scepta.tags " +
                "(organization, group, tag, data)" +
                "VALUES (?,?,?,?);");

        _updateOrganization = _session.prepare(
                "UPDATE scepta.organizations " +
                "SET data = ? " +
                "WHERE organization = ?;");

        _updatePolicyGroup = _session.prepare(
                "UPDATE scepta.policygroups " +
                "SET data = ? " +
                "WHERE organization = ? AND " +
                "group = ? AND " +
                "tag = '"+DesignRepository.MASTER_TAG+"';");

        _updatePolicy = _session.prepare(
                "UPDATE scepta.policies " +
                "SET data = ? " +
                "WHERE organization = ? AND " +
                "group = ? AND " +
                "tag = '"+DesignRepository.MASTER_TAG+"' AND " +
                "policy = ?;");

        _updatePolicyDefinition = _session.prepare(
                "UPDATE scepta.policydefns " +
                "SET data = ? " +
                "WHERE organization = ? AND " +
                "group = ? AND " +
                "tag = '"+DesignRepository.MASTER_TAG+"' AND " +
                "policy = ?;");

        _updateResourceDefinition = _session.prepare(
                "UPDATE scepta.resourcedefns " +
                "SET data = ? " +
                "WHERE organization = ? AND " +
                "group = ? AND " +
                "tag = '"+DesignRepository.MASTER_TAG+"' AND " +
                "policy = ? AND " +
                "resource = ?;");

        _updateTag = _session.prepare(
                "UPDATE scepta.tags " +
                "SET data = ? " +
                "WHERE organization = ? AND " +
                "group = ? AND " +
                "tag = ?;");

        _removeOrganization = _session.prepare(
                "DELETE FROM scepta.organizations " +
                "WHERE organization = ?;");

        _removePolicyGroup = _session.prepare(
                "DELETE FROM scepta.policygroups " +
                "WHERE organization = ? AND " +
                "group = ? AND " +
                "tag = ?;");

        _removePolicy = _session.prepare(
                "DELETE FROM scepta.policies " +
                "WHERE organization = ? AND " +
                "group = ? AND " +
                "tag = '"+DesignRepository.MASTER_TAG+"' AND " +
                "policy = ?;");
        _removePoliciesForTag = _session.prepare(
                "DELETE FROM scepta.policies " +
                "WHERE organization = ? AND " +
                "group = ? AND " +
                "tag = ?;");

        _removePolicyDefinition = _session.prepare(
                "DELETE FROM scepta.policydefns " +
                "WHERE organization = ? AND " +
                "group = ? AND " +
                "tag = '"+DesignRepository.MASTER_TAG+"' AND " +
                "policy = ?;");
        _removePolicyDefinitionsForTag = _session.prepare(
                "DELETE FROM scepta.policydefns " +
                "WHERE organization = ? AND " +
                "group = ? AND " +
                "tag = ?;");

        _removeResourceDefinition = _session.prepare(
                "DELETE FROM scepta.resourcedefns " +
                "WHERE organization = ? AND " +
                "group = ? AND " +
                "tag = '"+DesignRepository.MASTER_TAG+"' AND " +
                "policy = ? AND " +
                "resource = ?;");
        _removeResourceDefinitionsForPolicy = _session.prepare(
                "DELETE FROM scepta.resourcedefns " +
                "WHERE organization = ? AND " +
                "group = ? AND " +
                "tag = '"+DesignRepository.MASTER_TAG+"' AND " +
                "policy = ?;");
        _removeResourceDefinitionsForTag = _session.prepare(
                "DELETE FROM scepta.resourcedefns " +
                "WHERE organization = ? AND " +
                "group = ? AND " +
                "tag = ?;");

        _removeTag = _session.prepare(
                "DELETE FROM scepta.tags " +
                "WHERE organization = ? AND " +
                "group = ? AND " +
                "tag = ?;");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected java.util.Set<Organization> doGetOrganizations() {
        java.util.Set<Organization> ret=new java.util.HashSet<Organization>();

        ResultSet results = _session.execute("SELECT data FROM organizations");
        for (Row row : results) {
            try {
                String data=row.getString("data");

                ret.add(MAPPER.readValue(data.getBytes(), Organization.class));
            } catch (Exception e) {
                // TODO: HANDLE EXCEPTION
                e.printStackTrace();
            }
        }

        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAddOrganization(Organization org) {
        BoundStatement boundStatement = new BoundStatement(_insertOrganization);

        try {
            String data=MAPPER.writeValueAsString(org);

            _session.execute(boundStatement.bind(org.getName(), data));
        } catch (Exception e) {
            // TODO: Handle exception
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUpdateOrganization(Organization org) {
        BoundStatement boundStatement = new BoundStatement(_updateOrganization);

        try {
            String data=MAPPER.writeValueAsString(org);

            _session.execute(boundStatement.bind(data, org.getName()));
        } catch (Exception e) {
            // TODO: Handle exception
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Organization doGetOrganization(String name) {
        Row row=_session.execute("SELECT data FROM organizations WHERE organization = '"+name+"'").one();

        if (row != null) {
            try {
                String data=row.getString("data");

                return (MAPPER.readValue(data.getBytes(), Organization.class));
            } catch (Exception e) {
                // TODO: HANDLE EXCEPTION
                e.printStackTrace();
            }
        }

        return (null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRemoveOrganization(String name) {
        try {
            _session.execute(new BoundStatement(_removeOrganization).bind(name));
        } catch (Exception e) {
            // TODO: Handle exception
            e.printStackTrace();
        }
    }

    /**
     * This method returns the policy group names for an organization.
     *
     * @param org The organization
     * @return The list of policy group names
     */
    protected Set<String> doGetPolicyGroupNames(String org) {
        java.util.Set<String> ret=new java.util.HashSet<String>();

        ResultSet results = _session.execute("SELECT group FROM policygroups WHERE organization='"
                                +org+"'");
        for (Row row : results) {
            ret.add(row.getString("group"));
        }

        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<PolicyGroup> doGetPolicyGroups(String org) {
        java.util.Set<PolicyGroup> ret=new java.util.HashSet<PolicyGroup>();

        for (String groupName : doGetPolicyGroupNames(org)) {
            ResultSet results = _session.execute("SELECT data FROM policygroups WHERE organization='"
                                    +org+"' AND group = '"+groupName
                                    +"' AND tag='"+DesignRepository.MASTER_TAG+"'");
            for (Row row : results) {
                try {
                    String data=row.getString("data");

                    ret.add(MAPPER.readValue(data.getBytes(), PolicyGroup.class));
                } catch (Exception e) {
                    // TODO: HANDLE EXCEPTION
                    e.printStackTrace();
                }
            }
        }

        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAddPolicyGroup(String org, PolicyGroup group, String tag) {
        BoundStatement boundStatement = new BoundStatement(_insertPolicyGroup);

        try {
            String data=MAPPER.writeValueAsString(group);

            _session.execute(boundStatement.bind(org, group.getName(), tag, data));
        } catch (Exception e) {
            // TODO: Handle exception
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUpdatePolicyGroup(String org, PolicyGroup group) {
        BoundStatement boundStatement = new BoundStatement(_updatePolicyGroup);

        try {
            String data=MAPPER.writeValueAsString(group);

            _session.execute(boundStatement.bind(data, org, group.getName()));
        } catch (Exception e) {
            // TODO: Handle exception
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PolicyGroup doGetPolicyGroup(String org, String group, String tag) {
        Row row=_session.execute("SELECT data FROM policygroups WHERE organization = '"+org
                +"' AND group = '"+group+"' AND tag = '"+tag+"'").one();

        if (row != null) {
            try {
                String data=row.getString("data");

                return (MAPPER.readValue(data.getBytes(), PolicyGroup.class));
            } catch (Exception e) {
                // TODO: HANDLE EXCEPTION
                e.printStackTrace();
            }
        }

        return (null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRemovePolicyGroup(String org, String group) {
        try {
            for (Policy p : doGetPolicies(org, group, MASTER_TAG)) {
                doRemovePolicy(org, group, p.getName());
            }
            _session.execute(new BoundStatement(_removePolicyGroup).bind(org, group));
        } catch (Exception e) {
            // TODO: Handle exception
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<Policy> doGetPolicies(String org, String group, String tag) {
        java.util.Set<Policy> ret=new java.util.HashSet<Policy>();

        ResultSet results = _session.execute("SELECT data FROM policies WHERE organization='"+org
                            +"' AND group = '"+group+"' AND tag = '"+tag+"'");
        for (Row row : results) {
            try {
                String data=row.getString("data");

                ret.add(MAPPER.readValue(data.getBytes(), Policy.class));
            } catch (Exception e) {
                // TODO: HANDLE EXCEPTION
                e.printStackTrace();
            }
        }

        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAddPolicy(String org, String group, String tag, Policy policy) {
        BoundStatement boundStatement = new BoundStatement(_insertPolicy);

        try {
            String data=MAPPER.writeValueAsString(policy);

            _session.execute(boundStatement.bind(org, group, tag, policy.getName(), data));
        } catch (Exception e) {
            // TODO: Handle exception
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUpdatePolicy(String org, String group, Policy policy) {
        BoundStatement boundStatement = new BoundStatement(_updatePolicy);

        try {
            String data=MAPPER.writeValueAsString(policy);

            _session.execute(boundStatement.bind(data, org, group, policy.getName()));
        } catch (Exception e) {
            // TODO: Handle exception
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Policy doGetPolicy(String org, String group, String tag, String policy) {
        Row row=_session.execute("SELECT data FROM policies WHERE organization = '"+org
                +"' AND group = '"+group+"' AND tag = '"+tag+"' AND policy = '"
                +policy+"'").one();

        if (row != null) {
            try {
                String data=row.getString("data");

                return (MAPPER.readValue(data.getBytes(), Policy.class));
            } catch (Exception e) {
                // TODO: HANDLE EXCEPTION
                e.printStackTrace();
            }
        }

        return (null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRemovePolicy(String org, String group, String policy) {
        try {
            _session.execute(new BoundStatement(_removePolicy).bind(org, group, policy));
            _session.execute(new BoundStatement(_removePolicyDefinition).bind(org, group, policy));
            _session.execute(new BoundStatement(_removeResourceDefinitionsForPolicy).bind(org, group, policy));
        } catch (Exception e) {
            // TODO: Handle exception
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetPolicyDefinition(String org, String group, String tag, String policy) {
        Row row=_session.execute("SELECT data FROM policydefns WHERE organization = '"+org
                +"' AND group = '"+group+"' AND tag = '"+tag+"' AND policy = '"
                +policy+"'").one();

        if (row != null) {
            return (row.getString("data"));
        }

        return (null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetPolicyDefinition(String org, String group, String tag, String policy, String data) {
        if (doGetPolicyDefinition(org, group, tag, policy) == null) {
            BoundStatement boundStatement = new BoundStatement(_insertPolicyDefinition);

            try {
                _session.execute(boundStatement.bind(org, group, tag, policy, data));
            } catch (Exception e) {
                // TODO: Handle exception
                e.printStackTrace();
            }
        } else if (tag == MASTER_TAG) {
            BoundStatement boundStatement = new BoundStatement(_updatePolicyDefinition);

            try {
                _session.execute(boundStatement.bind(data, org, group, policy));
            } catch (Exception e) {
                // TODO: Handle exception
                e.printStackTrace();
            }
        } else {
            // TODO: REPORT EXCEPTION - should not be able to update tagged definitions
            throw new RuntimeException("Attempt to update tagged policy definition");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetResourceDefinition(String org, String group, String tag, String policy,
            String resource) {
        Row row=_session.execute("SELECT data FROM resourcedefns WHERE organization = '"+org
                +"' AND group = '"+group+"' AND tag = '"+tag+"' AND policy = '"
                +policy+"' AND resource = '"+resource+"'").one();

        if (row != null) {
            return (row.getString("data"));
        }

        return (null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetResourceDefinition(String org, String group, String tag, String policy, String resource,
            String data) {
        if (doGetResourceDefinition(org, group, tag, policy, resource) == null) {
            BoundStatement boundStatement = new BoundStatement(_insertResourceDefinition);

            try {
                _session.execute(boundStatement.bind(org, group, tag, policy, resource, data));
            } catch (Exception e) {
                // TODO: Handle exception
                e.printStackTrace();
            }
        } else if (tag == MASTER_TAG) {
            BoundStatement boundStatement = new BoundStatement(_updateResourceDefinition);

            try {
                _session.execute(boundStatement.bind(data, org, group, policy, resource));
            } catch (Exception e) {
                // TODO: Handle exception
                e.printStackTrace();
            }
        } else {
            // TODO: REPORT EXCEPTION - should not be able to update tagged definitions
            throw new RuntimeException("Attempt to update tagged resource definition");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRemoveResourceDefinition(String org, String group, String policy, String resource) {
        try {
            _session.execute(new BoundStatement(_removeResourceDefinition).bind(org, group, policy, resource));
        } catch (Exception e) {
            // TODO: Handle exception
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Tag> doGetTags(String org, String group) {
        java.util.List<Tag> ret=new java.util.ArrayList<Tag>();

        ResultSet results = _session.execute("SELECT data FROM tags WHERE organization='"+org
                            +"' AND group = '"+group+"'");
        for (Row row : results) {
            try {
                String data=row.getString("data");

                ret.add(MAPPER.readValue(data.getBytes(), Tag.class));
            } catch (Exception e) {
                // TODO: HANDLE EXCEPTION
                e.printStackTrace();
            }
        }

        // Sort tags, latest first
        if (ret.size() > 1) {
            Collections.sort(ret, new Comparator<Tag>() {
                public int compare(Tag t1, Tag t2) {
                    return ((int)(t2.getCreatedTimestamp()-t1.getCreatedTimestamp()));
                }
            });
        }

        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Tag doGetTag(String org, String group, String tag) {
        Row row=_session.execute("SELECT data FROM tags WHERE organization = '"+org
                +"' AND group = '"+group+"' AND tag = '"+tag+"'").one();

        if (row != null) {
            try {
                String data=row.getString("data");

                return (MAPPER.readValue(data.getBytes(), Tag.class));
            } catch (Exception e) {
                // TODO: HANDLE EXCEPTION
                e.printStackTrace();
            }
        }

        return (null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doCreateTag(String org, String group, Tag tag) {
        BoundStatement boundStatement = new BoundStatement(_insertTag);

        try {
            String data=MAPPER.writeValueAsString(tag);

            _session.execute(boundStatement.bind(org, group, tag.getName(), data));
        } catch (Exception e) {
            // TODO: Handle exception
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUpdateTag(String org, String group, Tag tag) {
        BoundStatement boundStatement = new BoundStatement(_updateTag);

        try {
            String data=MAPPER.writeValueAsString(tag);
System.out.println("UPDATE TAG: "+data);

            _session.execute(boundStatement.bind(data, org, group, tag.getName()));
        } catch (Exception e) {
            // TODO: Handle exception
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRemoveTag(String org, String group, String tag) {
        try {
            _session.execute(new BoundStatement(_removePolicyGroup).bind(org, group, tag));
            _session.execute(new BoundStatement(_removePoliciesForTag).bind(org, group, tag));
            _session.execute(new BoundStatement(_removePolicyDefinitionsForTag).bind(org, group, tag));
            _session.execute(new BoundStatement(_removeResourceDefinitionsForTag).bind(org, group, tag));
            _session.execute(new BoundStatement(_removeTag).bind(org, group, tag));
        } catch (Exception e) {
            // TODO: Handle exception
            e.printStackTrace();
        }
    }

}
