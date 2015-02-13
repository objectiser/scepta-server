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
package io.scepta.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

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

    /**
     * This method replaces the reference element with the child nodes
     * of the other element.
     *
     * @param ref The reference element
     * @param newNodes The element containing the new nodes
     */
    public static void replaceNodes(Element ref, Element newNodes) {
        Element parent=(Element)ref.getParentNode();

        while (newNodes.hasChildNodes()) {
            parent.insertBefore(newNodes.getFirstChild(), ref);
        }

        // Remove referenced node
        parent.removeChild(ref);
    }

    /**
     * This method replaces the reference element with the new node.
     *
     * @param ref The reference element
     * @param newNode The element
     */
    public static void replaceNode(Element ref, Element newNode) {
        Element parent=(Element)ref.getParentNode();

        parent.insertBefore(newNode, ref);

        // Remove referenced node
        parent.removeChild(ref);
    }

    /**
     * This method adds the supplied node as the first element within
     * the container.
     *
     * @param container The container
     * @param node The node
     */
    public static void insertFirst(Element container, Node node) {
        if (container.getFirstChild() == null) {
            container.appendChild(node);
        } else {
            container.insertBefore(node, container.getFirstChild());
        }
    }
}
