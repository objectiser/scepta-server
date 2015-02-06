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
package io.scepta.design.server;

import java.util.ServiceLoader;

import io.scepta.design.model.Policy;
import io.scepta.design.model.Resource;

public abstract class AbstractDesignServer implements DesignServer {

    private DesignRepository _repository;

    public void setRepository(DesignRepository repo) {
        _repository = repo;
    }

    public DesignRepository getRepository() {
        if (_repository == null) {
            ServiceLoader<DesignRepository> sl=ServiceLoader.load(DesignRepository.class);
            _repository = sl.iterator().next();
            System.out.println("REPOSITORY="+_repository);
        }
        return (_repository);
    }

    public void importPolicyGroup(String org, ImportExportDefinition defn) {

        // TODO: Check permission

    }

    public ImportExportDefinition exportPolicyGroup(String org, String group, String tag) {
        ImportExportDefinition defn=new ImportExportDefinition()
            .setGroupDetails(getRepository().getPolicyGroup(org, group, tag))
            .setPolicyDetails(getRepository().getPolicies(org, group, tag));

        // For each policy, we need to export its definition and its resource definitions
        java.util.Map<String,String> policyDefns=new java.util.HashMap<String,String>();
        java.util.Map<String,String> resourceDefns=new java.util.HashMap<String,String>();

        for (Policy p : defn.getPolicyDetails()) {
            policyDefns.put(p.getName(), getRepository().getPolicyDefinition(org, group, tag, p.getName()));

            for (Resource r : p.getResources()) {
                String existingResource=resourceDefns.get(r.getName());
                String currentResource=getRepository().getResourceDefinition(org, group, tag,
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
}
