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

import java.util.ServiceLoader;

/**
 * This class represents an abstract implementation of the design server.
 *
 */
public abstract class AbstractDesignServer {

    private DesignRepository _repository;
    private BuildServer _buildServer;
    private Generator _generator;

    public void setRepository(DesignRepository repo) {
        _repository = repo;
    }

    public DesignRepository getRepository() {
        if (_repository == null) {
            ServiceLoader<DesignRepository> sl=ServiceLoader.load(DesignRepository.class);
            _repository = sl.iterator().next();
        }
        return (_repository);
    }

    public void setBuildServer(BuildServer buildServer) {
        _buildServer = buildServer;
    }

    public BuildServer getBuildServer() {
        if (_buildServer == null) {
            ServiceLoader<BuildServer> sl=ServiceLoader.load(BuildServer.class);
            _buildServer = sl.iterator().next();
        }
        return (_buildServer);
    }

    public void setGenerator(Generator generator) {
        _generator = generator;
    }

    public Generator getGenerator() {
        if (_generator == null) {
            ServiceLoader<Generator> sl=ServiceLoader.load(Generator.class);
            _generator = sl.iterator().next();
        }
        return (_generator);
    }

    /**
     * This method initiates the build associated with a supplied
     * tag.
     *
     * @param organization The organization
     * @param group The group
     * @param tag The tag
     */
    public void initBuild(String organization, String group, String tag) {
        getBuildServer().schedule(organization, group, tag);
    }
}
