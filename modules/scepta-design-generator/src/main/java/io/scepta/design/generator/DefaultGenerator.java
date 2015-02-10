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

import java.util.Collections;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import io.scepta.design.model.Endpoint;
import io.scepta.design.model.Policy;
import io.scepta.design.model.PolicyGroup;
import io.scepta.design.server.GeneratedResult;
import io.scepta.design.server.Generator;
import io.scepta.design.server.PolicyGroupInterchange;
import io.scepta.design.util.DOMUtil;

/**
 * This class represents the default implementation of the Generator.
 *
 */
public class DefaultGenerator implements Generator {

    public static final String SCEPTA_PREFIX = "scepta:";

    @Override
    public GeneratedResult generate(PolicyGroupInterchange group) {
        GeneratedResult ret=new GeneratedResult();

        // Process each policy
        for (Policy policy : group.getPolicyDetails()) {

            // Process the policy definition
            generatePolicyDefinition(group.getGroupDetails(),
                    group.getPolicyDefinitions().get(policy.getName()));
        }

        return ret;
    }

    /**
     * This method converts the supplied policy definition, which references logical endpoints
     * with characteristics, into real physical endpoints using patterns associated with the
     * characteristics.
     *
     * @param group The policy group
     * @param policyDefn The policy definition
     * @return The updated policy definition, or null if failed
     */
    protected static String generatePolicyDefinition(PolicyGroup group, String policyDefn) {
        String ret=null;

        // Scan for 'from', 'inOnly' and 'to' elements - if 'scepta' prefix, then locate
        // endpoint definition - then apply actual uri, as well as relevant consumer/producer
        // options and process characteristics

        try {
            // Convert to DOM representation
            org.w3c.dom.Document doc=DOMUtil.textToDoc(policyDefn);

            // Locate 'from' elements
            boolean f_changed;

            do {
                f_changed = false;

                org.w3c.dom.NodeList nl=doc.getElementsByTagName("from");

                for (int i=0; i < nl.getLength(); i++) {
                    Node node=nl.item(i);

                    if (node instanceof Element) {
                        f_changed = processEndpoint(group, (Element)node);

                        if (f_changed) {
                            continue;
                        }
                    }
                }
            } while (f_changed);

            do {
                f_changed = false;

                org.w3c.dom.NodeList nl=doc.getElementsByTagName("to");

                for (int i=0; i < nl.getLength(); i++) {
                    Node node=nl.item(i);

                    if (node instanceof Element) {
                        f_changed = processEndpoint(group, (Element)node);

                        if (f_changed) {
                            continue;
                        }
                    }
                }
            } while (f_changed);

            do {
                f_changed = false;

                org.w3c.dom.NodeList nl=doc.getElementsByTagName("inOnly");

                for (int i=0; i < nl.getLength(); i++) {
                    Node node=nl.item(i);

                    if (node instanceof Element) {
                        f_changed = processEndpoint(group, (Element)node);

                        if (f_changed) {
                            continue;
                        }
                    }
                }
            } while (f_changed);

            // Convert back to text
            ret = DOMUtil.docToText(doc);

        } catch (Exception e) {
            // TODO: LOG EXCEPTION
            e.printStackTrace();
        }

        return (ret);
    }

    /**
     * This method processes the endpoint. If the element represents a SCEPTA endpoint,
     * then it will be configured with the actual physical URI and any relevant options.
     *
     * @param group The policy group
     * @param elem The DOM element representing the input/output element
     * @return Whether this element has been modified
     * @throws Exception Failed to process endpoint
     */
    protected static boolean processEndpoint(PolicyGroup group, Element elem)
                                throws Exception {
        String uri=elem.getAttribute("uri");

        if (uri != null && uri.startsWith(SCEPTA_PREFIX)) {
            String newuri=processEndpointURI(group, elem.getNodeName(), uri);

            if (newuri != null) {
                elem.setAttribute("uri", newuri);

                // TODO: Apply characteristics

                return (true);
            }
        }

        return (false);
    }

    /**
     * This method returns the endpoint name related to
     * a supplied logical URI.
     *
     * @param uri The logical URI
     * @return The endpoint name, or null if not found
     */
    protected static String getEndpointName(String uri) {
        String endpointName=null;

        if (uri != null && uri.startsWith(SCEPTA_PREFIX)) {
            endpointName = uri.substring(SCEPTA_PREFIX.length());

            int pos=endpointName.indexOf('?');
            if (pos != -1) {
                endpointName = endpointName.substring(0, pos);
            }
        }

        return (endpointName);
    }

    /**
     * This method processes the endpoint. If the URI represents a SCEPTA endpoint,
     * then it will be configured with the actual physical URI and any relevant options.
     *
     * @param group The policy group
     * @param elemName The element name
     * @param uri The original URI
     * @return The new URI, or null if not relevant
     * @throws Exception Failed to process endpoint
     */
    protected static String processEndpointURI(PolicyGroup group, String elemName, String uri)
                                throws Exception {
        String newuri=null;

        String endpointName=getEndpointName(uri);

        if (endpointName != null) {

            // Lookup endpoint
            Endpoint endpoint=group.getEndpoint(endpointName);

            if (endpoint == null) {
                // TODO: ERROR
                throw new Exception("Unable to find endpoint '"+endpointName+"'");
            }

            newuri = endpoint.getURI();

            boolean firstOption=(newuri.indexOf('?') == -1);

            if (isConsumer(elemName)) {

                // Sort the option keys to make the end result reproducible
                java.util.List<String> list=
                        new java.util.ArrayList<String>(endpoint.getConsumerOptions().keySet());
                Collections.sort(list);

                for (String key : list) {
                    String value=endpoint.getConsumerOptions().get(key);

                    newuri += (firstOption?'?':'&') + key + '=' + value;

                    firstOption = false;
                }

            } else if (isProducer(elemName)) {

                // Sort the option keys to make the end result reproducible
                java.util.List<String> list=
                        new java.util.ArrayList<String>(endpoint.getProducerOptions().keySet());
                Collections.sort(list);

                for (String key : list) {
                    String value=endpoint.getProducerOptions().get(key);

                    newuri += (firstOption?'?':'&') + key + '=' + value;

                    firstOption = false;
                }
            } else {
                // TODO: ERROR
                throw new Exception("Invalid element '"+elemName+"'");
            }

        } else if (uri == null) {
            // TODO: ERROR
            throw new Exception("Missing 'uri' attribute");
        }

        return (newuri);
    }

    /**
     * This method determines whether the supplied element name represents
     * a consumer.
     *
     * @param elemName The element name
     * @return Whether the element is a consumer
     */
    protected static final boolean isConsumer(String elemName) {
        return (elemName.equals("from"));
    }

    /**
     * This method determines whether the supplied element name represents
     * a producer.
     *
     * @param elemName The element name
     * @return Whether the element is a producer
     */
    protected static final boolean isProducer(String elemName) {
        return (elemName.equals("to") || elemName.equals("inOnly"));
    }
}
