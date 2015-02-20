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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;

import io.scepta.model.Organization;
import io.scepta.model.Policy;
import io.scepta.model.PolicyGroup;
import io.scepta.model.Tag;
import io.scepta.server.AbstractDesignServer;
import io.scepta.server.PolicyGroupInterchange;
import io.scepta.util.JSONUtil;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 * This class represents the RESTful interface to the scepta design server.
 *
 */
@Path("/design")
public class RESTDesignServer extends AbstractDesignServer {

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
        return (success(getRepository().getOrganizations()));
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
        return (success(getRepository().getOrganization(orgName)));
    }

    /**
     * This method updates the organization associated with the
     * supplied name.
     *
     * @param orgName The organization name
     * @param organization The organization
     * @return Whether the operation was successful
     */
    @PUT
    @Path("/{orgName}")
    @Consumes("application/json")
    public Response setOrganization(@PathParam("orgName") String orgName, Organization organization) {
        getRepository().updateOrganization(organization);

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
        java.util.List<PolicyGroup> groups=
                new java.util.ArrayList<PolicyGroup>(getRepository().getPolicyGroups(orgName));

        Collections.sort(groups, new Comparator<PolicyGroup>() {
            public int compare(PolicyGroup o1, PolicyGroup o2) {
                return (o2.getName().compareTo(o1.getName()));
            }
        });

        return (success(groups));
    }

    /**
     * This method imports a complete policy group.
     *
     * @param orgName The organization name
     * @param group The policy group interchange information
     * @return The response
     */
    @POST
    @Path("/{orgName}/import")
    @Consumes("application/json")
    public Response importPolicyGroup(@PathParam("orgName") String orgName, PolicyGroupInterchange group) {
        getRepository().importPolicyGroup(orgName, group);
        return (success());
    }

    /**
     * This method determines the CORS header values for accessing an organization import.
     *
     * @param servletResponse The servlet response
     * @return No relevant
     */
    @OPTIONS
    @Path("/{orgName}/import")
    public Response organizationImportOptions(@Context HttpServletResponse servletResponse) {
        servletResponse.addHeader("Allow-Control-Allow-Methods","POST,GET,OPTIONS");
        servletResponse.addHeader("Access-Control-Allow-Credentials","true");
        servletResponse.addHeader("Access-Control-Allow-Origin","*");
        servletResponse.addHeader("Access-Control-Allow-Headers","Content-Type,X-Requested-With");
        servletResponse.addHeader("Access-Control-Max-Age","60");
        return (null);
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
        return (success(getRepository().getPolicyGroup(orgName, groupName, tag)));
    }

    /**
     * This method exports the policy group associated with the
     * organization, group and optional tag name.
     *
     * @param orgName The organization name
     * @param groupName The policy group name
     * @param tag The optional tag name
     * @return Export representation of the policy group
     */
    @GET
    @Path("/{orgName}/group/{groupName}/export")
    @Produces("application/json")
    public Response export(@PathParam("orgName") String orgName,
                                    @PathParam("groupName") String groupName,
                                    @QueryParam("tag") String tag) {
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException {
                PolicyGroupInterchange export=getRepository().exportPolicyGroup(orgName, groupName, tag);
System.out.println("EXPORT: "+export);
                if (export != null) {
                    JSONUtil.serialize(export, os);
                } else {
                    // TODO: REPORT ERROR
                    throw new IOException("Failed to export group '"+groupName+"'");
                }
            }
        };

System.out.println("RETURNING AS FILE: "+groupName+".json");
        ResponseBuilder response = Response.ok(stream);
        response.header("Content-Disposition", "attachment; filename=\""+groupName+".json\"");
        return (response.build());
    }

    /**
     * This method tags the policy group master associated with the
     * organization, group and optional tag name.
     *
     * @param orgName The organization name
     * @param groupName The policy group name
     * @param tagName The optional tag name
     * @param description The description
     * @return The tag name (if not specified then one will be generated)
     */
    @POST
    @Path("/{orgName}/group/{groupName}/tag")
    @Consumes("text/plain")
    @Produces("application/json")
    public Response createTag(@PathParam("orgName") String orgName,
                                    @PathParam("groupName") String groupName,
                                    @QueryParam("name") String tagName, String description) {
        Tag tag=getRepository().createTag(orgName, groupName, tagName, description);

        // Create build
        initBuild(orgName, groupName, tag.getName());

        return (success(tag));
    }

    /**
     * This method lists the tags associated with organization and
     * policy group..
     *
     * @param orgName The organization name
     * @param groupName The policy group name
     * @return The list of tags for the organization and group
     */
    @GET
    @Path("/{orgName}/group/{groupName}/tag")
    @Produces("application/json")
    public Response showTags(@PathParam("orgName") String orgName,
                                    @PathParam("groupName") String groupName) {
        return (success(getRepository().getTags(orgName, groupName)));
    }

    /**
     * This method returns the tag associated with organization,
     * policy group and tag name.
     *
     * @param orgName The organization name
     * @param groupName The policy group name
     * @param tagName The tag name
     * @return The list of tags for the organization and group
     */
    @GET
    @Path("/{orgName}/group/{groupName}/tag/{tagName}")
    @Produces("application/json")
    public Response showTag(@PathParam("orgName") String orgName,
                                    @PathParam("groupName") String groupName,
                                    @PathParam("tagName") String tagName) {
        return (success(getRepository().getTag(orgName, groupName, tagName)));
    }

    /**
     * This method removes the specified tag.
     *
     * @param orgName The organization name
     * @param groupName The policy group name
     * @param tagName The tag name
     * @return Whether successful
     */
    @DELETE
    @Path("/{orgName}/group/{groupName}/tag/{tagName}")
    public Response removeTag(@PathParam("orgName") String orgName,
                                    @PathParam("groupName") String groupName,
                                    @PathParam("tagName") String tagName) {
        getRepository().removeTag(orgName, groupName, tagName);
        return (success());
    }

    /**
     * This method updates a policy group.
     *
     * @param orgName The organization name
     * @param groupName The policy group name
     * @param group The policy group
     * @return Whether the operation was successful
     */
    @PUT
    @Path("/{orgName}/group/{groupName}")
    @Consumes("application/json")
    public Response setPolicyGroup(@PathParam("orgName") String orgName,
            @PathParam("groupName") String groupName, PolicyGroup group) {

        // TODO: Check if group name matches

        getRepository().updatePolicyGroup(orgName, group);

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
        return (success(getRepository().getPolicies(orgName, groupName, tag)));
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
        return (success(getRepository().getPolicy(orgName, groupName, tag, policyName)));
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
    @PUT
    @Path("/{orgName}/group/{groupName}/policy/{policyName}")
    @Consumes("application/json")
    public Response setPolicy(@PathParam("orgName") String orgName,
                    @PathParam("groupName") String groupName,
                    @PathParam("policyName") String policyName,
                    Policy policy) {

        // TODO: Check if policy name matches

        getRepository().updatePolicy(orgName, groupName, policy);

        return (success());
    }

    /**
     * This method adds a policy.
     *
     * @param orgName The organization name
     * @param groupName The policy group name
     * @param policy The policy
     * @return Whether the operation was successful
     */
    @POST
    @Path("/{orgName}/group/{groupName}/policy")
    @Consumes("application/json")
    public Response addPolicy(@PathParam("orgName") String orgName,
                    @PathParam("groupName") String groupName,
                    Policy policy) {

        // TODO: Check if policy name matches

        getRepository().addPolicy(orgName, groupName, policy);

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
        String defn=getRepository().getPolicyDefinition(orgName, groupName, tag, policyName);
        return (success(defn));
    }

    /**
     * This method sets the policy definition associated with the
     * organization, group and policy name.
     *
     * @param orgName The organization name
     * @param groupName The policy group name
     * @param policyName The policy name
     * @param definition The definition
     * @return The response
     */
    @PUT
    @Path("/{orgName}/group/{groupName}/policy/{policyName}/definition")
    @Consumes("text/plain")
    public Response setPolicyDefinition(@PathParam("orgName") String orgName,
                                    @PathParam("groupName") String groupName,
                                    @PathParam("policyName") String policyName,
                                    String definition) {
        getRepository().setPolicyDefinition(orgName, groupName, policyName, definition);
        return (success());
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
        return (success(getRepository().getResourceDefinition(orgName, groupName, tag, policyName, resourceName)));
    }

    /**
     * This method sets the resource definition associated with the
     * organization, group, policy and resource name.
     *
     * @param orgName The organization name
     * @param groupName The policy group name
     * @param policyName The policy name
     * @param resourceName The resource name
     * @param definition The definition
     * @return The response
     */
    @PUT
    @Path("/{orgName}/group/{groupName}/policy/{policyName}/resource/{resourceName}")
    @Consumes("text/plain")
    public Response setResourceDefinition(@PathParam("orgName") String orgName,
                                    @PathParam("groupName") String groupName,
                                    @PathParam("policyName") String policyName,
                                    @PathParam("resourceName") String resourceName,
                                    String definition) {
        getRepository().setResourceDefinition(orgName, groupName, policyName, resourceName, definition);
        return (success());
    }

    /**
     * This method returns the list of supported characteristics.
     *
     * @return The list of supported characteristics
     */
    @GET
    @Path("/config/characteristics")
    @Produces("application/json")
    public Response getCharacteristicTypes() {
        return (success(getGenerator().getSupportedCharacteristicTypes()));
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
