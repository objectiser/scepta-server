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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * This class provides DOM utility functions.
 *
 */
public class DOMUtil {

    /**
     * This method converts the supplied text into a DOM representation.
     *
     * @param text The text
     * @return The DOM representation
     * @throws Exception Failed to convert
     */
    public static org.w3c.dom.Document textToDoc(String text) throws Exception {
        java.io.InputStream is=new java.io.ByteArrayInputStream(text.getBytes());

        return (textToDoc(is));
    }

    /**
     * This method converts the supplied text into a DOM representation.
     *
     * @param is The input stream
     * @return The DOM representation
     * @throws Exception Failed to convert
     */
    public static org.w3c.dom.Document textToDoc(java.io.InputStream is) throws Exception {
        org.w3c.dom.Document ret=null;

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        ret = dBuilder.parse(is);

        is.close();

        return (ret);
    }

    /**
     * This method converts the supplied DOM into a text representation.
     *
     * @param doc The DOM representation
     * @return The text
     * @throws Exception Failed to convert
     */
    public static String docToText(org.w3c.dom.Document doc) throws Exception {
        String ret=null;

        TransformerFactory factory=TransformerFactory.newInstance();
        Transformer transformer=factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source=new DOMSource(doc);

        java.io.ByteArrayOutputStream os=new java.io.ByteArrayOutputStream();

        StreamResult result = new StreamResult(os);
        transformer.transform(source, result);

        ret = new String(os.toByteArray());

        os.close();

        return (ret);
    }

}
