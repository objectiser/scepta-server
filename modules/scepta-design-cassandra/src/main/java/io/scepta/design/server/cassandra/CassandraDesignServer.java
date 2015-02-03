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

import io.scepta.design.model.Organization;
import io.scepta.design.model.Policy;
import io.scepta.design.model.PolicyGroup;
import io.scepta.design.model.Tag;
import io.scepta.design.server.AbstractDesignServer;
import io.scepta.design.server.DesignServer;

import java.util.List;
import java.util.Set;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CassandraDesignServer extends AbstractDesignServer {

    private static final String KEYSPACE = "scepta";

    private Cluster _cluster;
    private Session _session;

    private static final ObjectMapper MAPPER=new ObjectMapper();

    private PreparedStatement _updateOrganization;
    private PreparedStatement _updatePolicyGroup;
    private PreparedStatement _updatePolicy;

    /**
     * The default constructor.
     */
    public CassandraDesignServer() {
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
        _updateOrganization = _session.prepare(
                "UPDATE scepta.organizations " +
                "SET data = ? " +
                "WHERE organization = ?;");

        _updatePolicyGroup = _session.prepare(
                "UPDATE scepta.policygroups " +
                "SET data = ? " +
                "WHERE organization = ? AND " +
                "group = ? AND " +
                "tag = '"+DesignServer.MASTER_TAG+"';");

        _updatePolicy = _session.prepare(
                "UPDATE scepta.policies " +
                "SET data = ? " +
                "WHERE organization = ? AND " +
                "group = ? AND " +
                "tag = '"+DesignServer.MASTER_TAG+"' AND " +
                "policy = ?;");
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
    protected Organization doRemoveOrganization(String name) {
        return (null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<PolicyGroup> doGetPolicyGroups(String org) {
        java.util.Set<PolicyGroup> ret=new java.util.HashSet<PolicyGroup>();

        ResultSet results = _session.execute("SELECT data FROM policygroups WHERE organization='"+org+"'");
        for (Row row : results) {
            try {
                String data=row.getString("data");

                ret.add(MAPPER.readValue(data.getBytes(), PolicyGroup.class));
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
    protected void doAddPolicyGroup(String org, PolicyGroup group) {
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
    protected PolicyGroup doRemovePolicyGroup(String org, String group) {
        return (null);
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
    protected void doAddPolicy(String org, String group, Policy policy) {
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
    protected void doSetPolicyDefinition(String org, String group, String policy, String definition) {
        // Exception ???
        throw new RuntimeException("Unable to find policy '"+policy+"'");
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
    protected void doSetResourceDefinition(String org, String group, String policy, String resource,
            String definition) {
        // Exception ???
        throw new RuntimeException("Unable to find resource '"+resource+"'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Policy doRemovePolicy(String org, String group, String policy) {
        return (null);
    }
}
