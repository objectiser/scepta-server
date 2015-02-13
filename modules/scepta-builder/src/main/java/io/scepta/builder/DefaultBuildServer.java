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
package io.scepta.builder;

import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.scepta.model.Tag;
import io.scepta.model.Tag.BuildStatus;
import io.scepta.server.BuildServer;
import io.scepta.server.DeploymentServer;
import io.scepta.server.DesignRepository;
import io.scepta.server.GeneratedResult;
import io.scepta.server.Generator;
import io.scepta.server.GeneratorFactory;
import io.scepta.server.PolicyGroupInterchange;

/**
 * This class represents the default implementation of the Build Server.
 *
 */
public class DefaultBuildServer implements BuildServer {

    private static final int POOL_SIZE = 10;

    private DesignRepository _repository;
    private Generator _generator;
    private DeploymentServer _deploymentServer;
    private ExecutorService _executorService=Executors.newFixedThreadPool(POOL_SIZE);

    /**
     * This method sets the design repository.
     *
     * @param repo The repository
     */
    public void setRepository(DesignRepository repo) {
        _repository = repo;
    }

    /**
     * This method returns the design repository.
     *
     * @return The repository
     */
    public DesignRepository getRepository() {
        if (_repository == null) {
            ServiceLoader<DesignRepository> sl=ServiceLoader.load(DesignRepository.class);
            _repository = sl.iterator().next();
        }
        return (_repository);
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
     * This method returns the generator.
     *
     * @return The generator
     */
    protected Generator getGenerator() {
        if (_generator == null) {
            _generator = GeneratorFactory.get();
        }

        return (_generator);
    }

    /**
     * {@inheritDoc}
     */
    public void schedule(final String org, final String group, final String tagName) {

        // Update tag
        Tag tag=getRepository().getTag(org, group, tagName);

        if (tag == null) {
            // TODO: REPORT ERROR
            throw new RuntimeException("Could not find tag '"+tagName+"'");
        }

        tag.setBuildStatus(BuildStatus.Scheduled);
        getRepository().updateTag(org, group, tag);

        _executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Retrieve the policy group
                PolicyGroupInterchange pgi=getRepository().exportPolicyGroup(org, group, tagName);
                Tag tag=getRepository().getTag(org, group, tagName);

                if (pgi != null && tag != null) {
                    tag.setBuildStatus(BuildStatus.Building);
                    getRepository().updateTag(org, group, tag);

                    // Generate the policy group
                    Generator gen=getGenerator();

                    if (gen != null) {
                        GeneratedResult result=gen.generate(pgi);

                        if (result != null) {
                            // Store the zip representation
                            try {
                                java.io.ByteArrayOutputStream os=new java.io.ByteArrayOutputStream();

                                result.asZip(os);

                                java.io.ByteArrayInputStream is=new java.io.ByteArrayInputStream(os.toByteArray());

                                os.close();

                                getDeploymentServer().deploy(org, group, tag.getName(), is);

                                is.close();

                                tag.setBuildStatus(BuildStatus.Successful);
                                getRepository().updateTag(org, group, tag);
                            } catch (Exception e) {
                                // TODO: REPORT ERROR
                                e.printStackTrace();

                                tag.setBuildStatus(BuildStatus.Failed);
                                getRepository().updateTag(org, group, tag);
                            }
                        } else {
                            tag.setBuildStatus(BuildStatus.Failed);
                            getRepository().updateTag(org, group, tag);

                            // TODO: REPORT ERROR
                            System.err.println("NO GENERATED RESULT");
                        }
                    } else {
                        tag.setBuildStatus(BuildStatus.Failed);
                        getRepository().updateTag(org, group, tag);

                        // TODO: REPORT ERROR
                        System.err.println("NO GENERATOR");
                    }

                } else {
                    tag.setBuildStatus(BuildStatus.Failed);
                    getRepository().updateTag(org, group, tag);

                    // TODO: REPORT ERROR
                    System.err.println("COULD NOT FIND POLICY GROUP INTERCHANGE ("
                                    +pgi+") OR TAG ("+tag+")");
                }
            }
        });
    }
}
