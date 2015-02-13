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
package io.scepta.design.util;

import static org.junit.Assert.*;
import io.scepta.util.DOMUtil;
import io.scepta.util.PolicyDefinitionUtil;

import org.junit.Test;

public class DOMUtilTest {

    private static final String TEST_ENDPOINT = "test";

    @Test
    public void testGetEndpointName() {
        assertEquals(PolicyDefinitionUtil.getEndpointName("scepta:test"), TEST_ENDPOINT);
        assertEquals(PolicyDefinitionUtil.getEndpointName("scepta:test?op1=val1"), TEST_ENDPOINT);
        assertNull(PolicyDefinitionUtil.getEndpointName("jms:test"));
    }

    @Test
    public void testReplaceNodes() {
        String expected="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"+
                "<root>\n"+
                "  <from/>\n"+
                "  <to>\n"+
                "    <pre/>\n"+
                "    <one/>\n"+
                "    <two/>\n"+
                "    <three/>\n"+
                "    <post/>\n"+
                "  </to>\n"+
                "</root>\n";

        try {
            org.w3c.dom.Document doc=
                DOMUtil.textToDoc("<root><from><one/><two/><three/></from><to><pre/><ref/><post/></to></root>");

            org.w3c.dom.Element ref=(org.w3c.dom.Element)doc.getElementsByTagName("ref").item(0);
            org.w3c.dom.Element from=(org.w3c.dom.Element)doc.getElementsByTagName("from").item(0);

            DOMUtil.replaceNodes(ref, from);

            String result=DOMUtil.docToText(doc);

            assertEquals(expected, result);

        } catch (Exception e) {
            fail("Failed to replace nodes: "+e);
        }
    }
}
