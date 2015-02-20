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
package io.scepta.deployment.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ServiceLoader;

import io.scepta.server.DeploymentServer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

/**
 * This class represents the RESTful interface to the scepta deployment server.
 *
 */
@Path("/deployment")
public class RESTDeploymentServer {

    private DeploymentServer _deploymentServer;

    /**
     * This is the default constructor.
     */
    public RESTDeploymentServer() {
    }

    /**
     * This method sets the deployment server.
     *
     * @param ds The deployment server
     */
    public void setDeploymentServer(DeploymentServer ds) {
        _deploymentServer = ds;
    }

    /**
     * This method returns the deployment server.
     *
     * @return The deployment server
     */
    public DeploymentServer getDeploymentServer() {
        if (_deploymentServer == null) {
            ServiceLoader<DeploymentServer> sl=ServiceLoader.load(DeploymentServer.class);
            _deploymentServer = sl.iterator().next();
        }
        return (_deploymentServer);
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
    @Path("/{orgName}/{groupName}")
    @Produces("application/json")
    public Response getDeployedTags(@PathParam("orgName") String orgName,
                                    @PathParam("groupName") String groupName) {
        return (success(getDeploymentServer().getDeployedTags(orgName, groupName)));
    }

    /**
     * This method returns the deployment associated with organization,
     * policy group and tag name.
     *
     * @param orgName The organization name
     * @param groupName The policy group name
     * @param tagName The tag name
     * @return The deployment
     */
    @GET
    @Path("/{orgName}/{groupName}/{tagName}")
    @Produces("application/zip")
    public Response getDeployment(@PathParam("orgName") final String orgName,
                                    @PathParam("groupName") final String groupName,
                                    @PathParam("tagName") final String tagName) {
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException {
                getDeploymentServer().getDeployment(orgName, groupName, tagName, os);
            }
        };

        ResponseBuilder response = Response.ok(stream);
        response.header("Content-Disposition", "attachment; filename=\""+groupName+"-"+tagName+".zip\"");
        return (response.build());
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
