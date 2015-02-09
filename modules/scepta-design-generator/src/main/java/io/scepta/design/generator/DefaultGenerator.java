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
package io.scepta.design.generator;

import io.scepta.design.model.Policy;
import io.scepta.design.server.GeneratedResult;
import io.scepta.design.server.Generator;
import io.scepta.design.server.PolicyGroupInterchange;

/**
 * This class represents the default implementation of the Generator.
 *
 */
public class DefaultGenerator implements Generator {

    @Override
    public GeneratedResult generate(PolicyGroupInterchange group) {
        GeneratedResult ret=new GeneratedResult();

        // Process each policy
        for (Policy p : group.getPolicyDetails()) {

            // Process the policy definition
            generatePolicyDefinition(group, p);
        }

        return ret;
    }

    /**
     * This method converts the supplied policy definition, which references logical endpoints
     * with characteristics, into real physical endpoints using patterns associated with the
     * characteristics.
     *
     * @param group The policy group
     * @param policy The policy
     * @return The updated policy definition
     */
    protected String generatePolicyDefinition(PolicyGroupInterchange group, Policy policy) {
        String original=group.getPolicyDefinitions().get(policy.getName());

        return (original);
    }

}
