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
package io.scepta.design.generator.charactistics;

import static org.junit.Assert.*;
import io.scepta.design.model.Characteristic;
import io.scepta.design.model.Endpoint;
import io.scepta.design.model.PolicyGroup;
import io.scepta.design.util.DOMUtil;

import org.junit.Test;

public class BatchWithRetryOnFailureTest {

    @Test
    public void testRESTServiceProducerBatchRetry() {
        org.w3c.dom.Document doc=loadDocument("/policyDefns/unprocessed/ActivityServer.xml");

        org.w3c.dom.Element elem=(org.w3c.dom.Element)doc.getElementsByTagName("inOnly").item(0);

        BatchWithRetryOnFailure processor=new BatchWithRetryOnFailure();

        PolicyGroup group=new PolicyGroup();

        Endpoint endpoint=new Endpoint();

        Characteristic ch=new Characteristic();

        ch.setType(BatchWithRetryOnFailure.class.getName());
        ch.getProperties().put("completionSize", "111");
        ch.getProperties().put("completionInterval", "2222");

        processor.process(group, endpoint, ch, elem);

        try {
            compare(doc, "/policyDefns/processed/ActivityServer.xml");
        } catch (Exception e) {
            fail("Failed: "+e);
        }
    }

    @Test
    @org.junit.Ignore
    public void testRESTServiceConsumerProducerBatchRetry() {
        org.w3c.dom.Document doc=loadDocument("/policyDefns/unprocessed/ServiceDefinition.xml");

        org.w3c.dom.Element elem=(org.w3c.dom.Element)doc.getElementsByTagName("from").item(0);

        BatchWithRetryOnFailure processor=new BatchWithRetryOnFailure();

        PolicyGroup group=new PolicyGroup();

        Endpoint ep1=new Endpoint().setName("servicedefns");
        group.getEndpoints().add(ep1);

        Characteristic ch1=new Characteristic()
                .setType(BatchWithRetryOnFailure.class.getName());
        //ep1.getCharacteristics().add(ch1);

        Endpoint ep2=new Endpoint();
        Characteristic ch2=new Characteristic()
                .setType(BatchWithRetryOnFailure.class.getName());
        //ch.getProperties().put("completionSize", "111");
        //ch.getProperties().put("completionInterval", "2222");

        processor.process(group, ep2, ch2, elem);

        try {
            compare(doc, "/policyDefns/processed/ServiceDefinition.xml");
        } catch (Exception e) {
            fail("Failed: "+e);
        }
    }

    protected void compare(org.w3c.dom.Document doc, String expectedLoc) throws Exception {

        String modified=DOMUtil.docToText(doc);

        org.w3c.dom.Document expectedDoc=loadDocument(expectedLoc);
        String expected=DOMUtil.docToText(expectedDoc);

        if (!modified.equals(expected)) {
            int maxlen=(modified.length()>expected.length()?expected.length():modified.length());

            for (int i=0; i < maxlen; i++) {
                if (expected.charAt(i) != modified.charAt(i)) {
                    System.err.println("MISMATCH FOUND AT POS "+i+":");
                    int startpos=(i > 10 ? i-10 : 0);
                    int endpos=(maxlen-i > 10 ? i+10 : maxlen);

                    System.err.println("MODIFIED: "+modified.substring(startpos, endpos));
                    System.err.println("EXPECTED: "+expected.substring(startpos, endpos));
                }
            }

            fail("Charactistic processing produced incorrect result");
        }
    }

    protected org.w3c.dom.Document loadDocument(String loc) {
        org.w3c.dom.Document ret=null;

        try {
            java.io.InputStream is=BatchWithRetryOnFailureTest.class.getResourceAsStream(loc);

            byte[] b=new byte[is.available()];

            is.read(b);

            is.close();

            ret = DOMUtil.textToDoc(new String(b));

        } catch (Exception e) {
            fail("Failed to load XML doc '"+loc+"': "+e);
        }

        return (ret);
    }

}
