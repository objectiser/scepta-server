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

import io.scepta.design.model.Endpoint;
import io.scepta.design.model.PolicyGroup;
import io.scepta.design.server.PolicyGroupInterchange;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultGeneratorTest {

    private static final String TO_ELEMENT = "to";
    private static final String FROM_ELEMENT = "from";
    private static final String ACTIVEMQ_QUEUE_TEST = "activemq:queue:test";
    private static final String TEST_ENDPOINT = "test";
    private static final ObjectMapper MAPPER=new ObjectMapper();

    @Test
    public void testGetEndpointName() {
        assertEquals(DefaultGenerator.getEndpointName("scepta:test"), TEST_ENDPOINT);
        assertEquals(DefaultGenerator.getEndpointName("scepta:test?op1=val1"), TEST_ENDPOINT);
        assertNull(DefaultGenerator.getEndpointName("jms:test"));
    }

    @Test
    public void testProcessEndpointURIConsumer() {
        String uri=DefaultGenerator.SCEPTA_PREFIX+TEST_ENDPOINT;

        PolicyGroup group=new PolicyGroup();

        Endpoint endpoint=new Endpoint()
                .setName(TEST_ENDPOINT)
                .setURI(ACTIVEMQ_QUEUE_TEST);

        endpoint.getConsumerOptions().put("op1", "val1");
        endpoint.getConsumerOptions().put("op2", "val2");

        group.getEndpoints().add(endpoint);

        try {
            String newuri=DefaultGenerator.processEndpointURI(group, FROM_ELEMENT, uri);

            assertNotNull(newuri);

            assertEquals(ACTIVEMQ_QUEUE_TEST+"?op1=val1&op2=val2", newuri);
        } catch (Exception e) {
            fail("Failed to process endpoint uri: "+e);
        }
    }

    @Test
    public void testProcessEndpointURIConsumerWithExistingOptions() {
        String uri=DefaultGenerator.SCEPTA_PREFIX+TEST_ENDPOINT;

        PolicyGroup group=new PolicyGroup();

        Endpoint endpoint=new Endpoint()
                .setName(TEST_ENDPOINT)
                .setURI(ACTIVEMQ_QUEUE_TEST+"?existing=value");

        endpoint.getConsumerOptions().put("op1", "val1");
        endpoint.getConsumerOptions().put("op2", "val2");

        group.getEndpoints().add(endpoint);

        try {
            String newuri=DefaultGenerator.processEndpointURI(group, FROM_ELEMENT, uri);

            assertNotNull(newuri);

            assertEquals(ACTIVEMQ_QUEUE_TEST+"?existing=value&op1=val1&op2=val2", newuri);
        } catch (Exception e) {
            fail("Failed to process endpoint uri: "+e);
        }
    }

    @Test
    public void testProcessEndpointURIProducer() {
        String uri=DefaultGenerator.SCEPTA_PREFIX+TEST_ENDPOINT;

        PolicyGroup group=new PolicyGroup();

        Endpoint endpoint=new Endpoint()
                .setName(TEST_ENDPOINT)
                .setURI(ACTIVEMQ_QUEUE_TEST);

        endpoint.getProducerOptions().put("op1", "val1");
        endpoint.getProducerOptions().put("op2", "val2");

        group.getEndpoints().add(endpoint);

        try {
            String newuri=DefaultGenerator.processEndpointURI(group, TO_ELEMENT, uri);

            assertNotNull(newuri);

            assertEquals(ACTIVEMQ_QUEUE_TEST+"?op1=val1&op2=val2", newuri);
        } catch (Exception e) {
            fail("Failed to process endpoint uri: "+e);
        }
    }

    @Test
    public void testProcessEndpointURIProducerWithExistingOptions() {
        String uri=DefaultGenerator.SCEPTA_PREFIX+TEST_ENDPOINT;

        PolicyGroup group=new PolicyGroup();

        Endpoint endpoint=new Endpoint()
                .setName(TEST_ENDPOINT)
                .setURI(ACTIVEMQ_QUEUE_TEST+"?existing=value");

        endpoint.getProducerOptions().put("op1", "val1");
        endpoint.getProducerOptions().put("op2", "val2");

        group.getEndpoints().add(endpoint);

        try {
            String newuri=DefaultGenerator.processEndpointURI(group, TO_ELEMENT, uri);

            assertNotNull(newuri);

            assertEquals(ACTIVEMQ_QUEUE_TEST+"?existing=value&op1=val1&op2=val2", newuri);
        } catch (Exception e) {
            fail("Failed to process endpoint uri: "+e);
        }
    }

    protected PolicyGroupInterchange getPolicyGroupInterchange(String name) {
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
}
