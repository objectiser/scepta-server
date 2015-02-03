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
package io.scepta.design.server.rest;

import io.scepta.design.model.Organization;
import io.scepta.design.model.Policy;
import io.scepta.design.model.PolicyGroup;
import io.scepta.design.server.DesignServer;
import io.scepta.design.server.cassandra.CassandraDesignServer;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * This class represents the RESTful interface to the scepta design server.
 *
 */
@Path("/design")
public class RESTDesignServer {

    private DesignServer _devServer=new CassandraDesignServer();

    /**
     * This is the default constructor.
     */
    public RESTDesignServer() {
    }

    /**
     * This method returns the list of available organizations.
     *
     * @return The list of organizations
     */
    @GET
    @Path("/")
    @Produces("application/json")
    public Response getOrganizations() {
        return (success(_devServer.getOrganizations()));
    }

    /**
     * This method returns the organization associated with the
     * supplied name.
     *
     * @param orgName The organization name
     * @return The organization or null if not found
     */
    @GET
    @Path("/{orgName}")
    @Produces("application/json")
    public Response getOrganization(@PathParam("orgName") String orgName) {
        return (success(_devServer.getOrganization(orgName)));
    }

    /**
     * This method updates the organization associated with the
     * supplied name.
     *
     * @param orgName The organization name
     * @param organization The organization
     * @return Whether the operation was successful
     */
    @POST
    @Path("/{orgName}")
    @Consumes("application/json")
    public Response setOrganization(@PathParam("orgName") String orgName, Organization organization) {
        _devServer.updateOrganization(organization);

        return (success());
    }

    /**
     * This method determines the CORS header values for accessing an organization.
     *
     * @param servletResponse The servlet response
     * @return No relevant
     */
    @OPTIONS
    @Path("/{orgName}")
    public Response organizationOptions(@Context HttpServletResponse servletResponse) {
        servletResponse.addHeader("Allow-Control-Allow-Methods","POST,GET,OPTIONS");
        servletResponse.addHeader("Access-Control-Allow-Credentials","true");
        servletResponse.addHeader("Access-Control-Allow-Origin","*");
        servletResponse.addHeader("Access-Control-Allow-Headers","Content-Type,X-Requested-With");
        servletResponse.addHeader("Access-Control-Max-Age","60");
        return (null);
    }

    /**
     * This method returns the policy groups associated with the
     * organiation name.
     *
     * @param orgName The organization name
     * @return The policy groups
     */
    @GET
    @Path("/{orgName}/group")
    @Produces("application/json")
    public Response getPolicyGroups(@PathParam("orgName") String orgName) {
        return (success(_devServer.getPolicyGroups(orgName)));
    }

    /**
     * This method returns the policy group associated with the
     * organization name and group name.
     *
     * @param orgName The organization name
     * @param groupName The policy group name
     * @return The policy groups
     */
    @GET
    @Path("/{orgName}/group/{groupName}")
    @Produces("application/json")
    public Response getPolicyGroup(@PathParam("orgName") String orgName,
                                    @PathParam("groupName") String groupName,
                                    @QueryParam("tag") String tag) {
        return (success(_devServer.getPolicyGroup(orgName, groupName, tag)));
    }

    /**
     * This method updates a policy group.
     *
     * @param orgName The organization name
     * @param groupName The policy group name
     * @param group The policy group
     * @return Whether the operation was successful
     */
    @POST
    @Path("/{orgName}/group/{groupName}")
    @Consumes("application/json")
    public Response setPolicyGroup(@PathParam("orgName") String orgName,
            @PathParam("groupName") String groupName, PolicyGroup group) {

        // TODO: Check if group name matches

        _devServer.updatePolicyGroup(orgName, group);

        return (success());
    }

    /**
     * This method determines the CORS header values for accessing a policy group.
     *
     * @param servletResponse The servlet response
     * @return No relevant
     */
    @OPTIONS
    @Path("/{orgName}/group/{groupName}")
    public Response policyGroupOptions(@Context HttpServletResponse servletResponse) {
        servletResponse.addHeader("Allow-Control-Allow-Methods","POST,GET,OPTIONS");
        servletResponse.addHeader("Access-Control-Allow-Credentials","true");
        servletResponse.addHeader("Access-Control-Allow-Origin","*");
        servletResponse.addHeader("Access-Control-Allow-Headers","Content-Type,X-Requested-With");
        servletResponse.addHeader("Access-Control-Max-Age","60");
        return (null);
    }

    /**
     * This method returns the list of policies associated with the
     * organization and group name.
     *
     * @param orgName The organization name
     * @param groupName The policy group name
     * @return The list of policies
     */
    @GET
    @Path("/{orgName}/group/{groupName}/policy")
    @Produces("application/json")
    public Response getPolicies(@PathParam("orgName") String orgName,
                                    @PathParam("groupName") String groupName,
                                    @QueryParam("tag") String tag) {
        return (success(_devServer.getPolicies(orgName, groupName, tag)));
    }

    /**
     * This method returns the policy associated with the
     * organization, group and policy name.
     *
     * @param orgName The organization name
     * @param groupName The policy group name
     * @param policyName The policy name
     * @param tag The optional tag
     * @return The policy or null if not found
     */
    @GET
    @Path("/{orgName}/group/{groupName}/policy/{policyName}")
    @Produces("application/json")
    public Response getPolicy(@PathParam("orgName") String orgName,
                                    @PathParam("groupName") String groupName,
                                    @PathParam("policyName") String policyName,
                                    @QueryParam("tag") String tag) {
        return (success(_devServer.getPolicy(orgName, groupName, tag, policyName)));
    }

    /**
     * This method updates a policy.
     *
     * @param orgName The organization name
     * @param groupName The policy group name
     * @param policyName The policy name
     * @param policy The policy
     * @return Whether the operation was successful
     */
    @POST
    @Path("/{orgName}/group/{groupName}/policy/{policyName}")
    @Consumes("application/json")
    public Response setPolicy(@PathParam("orgName") String orgName,
                    @PathParam("groupName") String groupName,
                    @PathParam("policyName") String policyName,
                    Policy policy) {

        // TODO: Check if policy name matches

        _devServer.updatePolicy(orgName, groupName, policy);

        return (success());
    }

    /**
     * This method determines the CORS header values for accessing a policy.
     *
     * @param servletResponse The servlet response
     * @return No relevant
     */
    @OPTIONS
    @Path("/{orgName}/group/{groupName}/policy/{policyName}")
    public Response policyOptions(@Context HttpServletResponse servletResponse) {
        servletResponse.addHeader("Allow-Control-Allow-Methods","POST,GET,OPTIONS");
        servletResponse.addHeader("Access-Control-Allow-Credentials","true");
        servletResponse.addHeader("Access-Control-Allow-Origin","*");
        servletResponse.addHeader("Access-Control-Allow-Headers","Content-Type,X-Requested-With");
        servletResponse.addHeader("Access-Control-Max-Age","60");
        return (null);
    }

    /**
     * This method returns the policy definition associated with the
     * organization, group and policy name.
     *
     * @param orgName The organization name
     * @param groupName The policy group name
     * @param policyName The policy name
     * @param tag The optional tag
     * @return The policy definition or null if not found
     */
    @GET
    @Path("/{orgName}/group/{groupName}/policy/{policyName}/definition")
    @Produces("text/plain")
    public Response getPolicyDefinition(@PathParam("orgName") String orgName,
                                    @PathParam("groupName") String groupName,
                                    @PathParam("policyName") String policyName,
                                    @QueryParam("tag") String tag) {
        return (success(_devServer.getPolicyDefinition(orgName, groupName, tag, policyName)));
    }

    /**
     * This method returns the policy definition associated with the
     * organization, group and policy name.
     *
     * @param orgName The organization name
     * @param groupName The policy group name
     * @param policyName The policy name
     * @param resourceName The resource name
     * @param tag The optional tag
     * @return The resource definition or null if not found
     */
    @GET
    @Path("/{orgName}/group/{groupName}/policy/{policyName}/resource/{resourceName}")
    @Produces("text/plain")
    public Response getResourceDefinition(@PathParam("orgName") String orgName,
                                    @PathParam("groupName") String groupName,
                                    @PathParam("policyName") String policyName,
                                    @PathParam("resourceName") String resourceName,
                                    @QueryParam("tag") String tag) {
        return (success(_devServer.getResourceDefinition(orgName, groupName, tag, policyName, resourceName)));
    }

    /**
     * This method returns a successful response.
     *
     * @param result The result to be returned
     * @return The successful response
     */
    protected Response success(Object result) {
        return (Response
                .status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .header("Access-Control-Max-Age", "1209600")
                .entity(result)
                .build());
    }

    /**
     * This method returns a successful response.
     *
     * @return The successful response
     */
    protected Response success() {
        return (Response
                .status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .header("Access-Control-Max-Age", "1209600")
                .build());
    }
}
