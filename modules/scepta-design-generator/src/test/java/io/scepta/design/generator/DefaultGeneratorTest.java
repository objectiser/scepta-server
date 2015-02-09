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

import static org.junit.Assert.*;
import io.scepta.design.model.Policy;
import io.scepta.design.server.PolicyGroupInterchange;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultGeneratorTest {

    private static final ObjectMapper MAPPER=new ObjectMapper();

    @Test
    @org.junit.Ignore
    public void testGeneratePolicyDefinitionRTGov() {
        DefaultGenerator generator=new DefaultGenerator();

        PolicyGroupInterchange group=getPolicyGroup("RTGov");

        for (Policy policy : group.getPolicyDetails()) {
            String updated=generator.generatePolicyDefinition(group, policy);

            String pdefn=getPolicyDefinition(group.getGroupDetails().getName(), policy.getName());

            if (!updated.equals(pdefn)) {
                fail("Updated polcy definition ["+group.getGroupDetails().getName()+","+policy.getName()+"] mismatch");
            }
        }
    }

    protected PolicyGroupInterchange getPolicyGroup(String name) {
        PolicyGroupInterchange ret=null;

        try {
            java.io.InputStream is=DefaultGeneratorTest.class.getResourceAsStream("/groups/"+name+".json");

            if (is != null) {
                byte[] b=new byte[is.available()];

                is.read(b);

                is.close();

                ret = MAPPER.readValue(b, PolicyGroupInterchange.class);
            }

        } catch (Exception e) {
            fail("Failed to load policy group '"+name+"'"+e);
        }

        return (ret);
    }

    protected String getPolicyDefinition(String group, String policy) {
        String ret=null;

        try {
            java.io.InputStream is=DefaultGeneratorTest.class.getResourceAsStream("/groups/policyDefns/"
                            +group+"/"+policy+".xml");

            if (is != null) {
                byte[] b=new byte[is.available()];

                is.read(b);

                is.close();

                ret = new String(b);
            }

        } catch (Exception e) {
            fail("Failed to load policy definition for group="+group+" and policy="+policy+" : "+e);
        }

        return (ret);
    }
}
