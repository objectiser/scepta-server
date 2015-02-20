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
package io.scepta.generator.characteristics;

import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import io.scepta.generator.CharacteristicProcessor;
import io.scepta.model.Characteristic;
import io.scepta.model.Dependency;
import io.scepta.model.Endpoint;
import io.scepta.model.PolicyGroup;
import io.scepta.runtime.SceptaRuntimeVersion;
import io.scepta.server.CharacteristicType;
import io.scepta.util.DOMUtil;
import io.scepta.util.PolicyDefinitionUtil;

/**
 * This class implements the 'BatchWithRetryOnFailure' characteristic.
 *
 */
public class BatchWithRetryOnFailure implements CharacteristicProcessor {

    private static final org.w3c.dom.Element CONSUMER_PRODUCER_TEMPLATE;
    private static final org.w3c.dom.Element CONSUMER_ONLY_TEMPLATE;

    private static final java.util.Set<Dependency> DEPENDENCIES=new java.util.HashSet<Dependency>();
    private static CharacteristicType TYPE;

    static {
        TYPE = new CharacteristicType().setName(BatchWithRetryOnFailure.class.getSimpleName());
        TYPE.getPropertyDescriptors().put("batchSize",
                new CharacteristicType.PropertyDescriptor().setMandatory(true).setDefaultValue("100"));
        TYPE.getPropertyDescriptors().put("batchInterval",
                new CharacteristicType.PropertyDescriptor().setMandatory(true).setDefaultValue("1000"));
        TYPE.getPropertyDescriptors().put("maxRetry",
                new CharacteristicType.PropertyDescriptor().setMandatory(true).setDefaultValue("3"));

        org.w3c.dom.Document cpdoc=null;
        org.w3c.dom.Document codoc=null;

        try {
            cpdoc = DOMUtil.textToDoc(BatchWithRetryOnFailure.class.getResourceAsStream(
                    "/templates/BatchWithRetryOnFailure-consumer-producer.xml"));
            codoc = DOMUtil.textToDoc(BatchWithRetryOnFailure.class.getResourceAsStream(
                    "/templates/BatchWithRetryOnFailure-consumer-only.xml"));
        } catch (Exception e) {
            // TODO: ERROR
            e.printStackTrace();
        }

        if (cpdoc != null) {
            CONSUMER_PRODUCER_TEMPLATE = cpdoc.getDocumentElement();
        } else {
            CONSUMER_PRODUCER_TEMPLATE = null;
        }

        if (codoc != null) {
            CONSUMER_ONLY_TEMPLATE = codoc.getDocumentElement();
        } else {
            CONSUMER_ONLY_TEMPLATE = null;
        }

        DEPENDENCIES.add(new Dependency()
                .setGroupId("io.scepta")
                .setArtifactId("scepta-runtime")
                .setVersion(SceptaRuntimeVersion.getVersion()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharacteristicType getType() {
        return (TYPE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(PolicyGroup group, Endpoint endpoint,
                        Characteristic characteristic, Element elem) {

        if (PolicyDefinitionUtil.isOnewayProducer(elem.getNodeName())) {
            processProducer(group, endpoint, characteristic, elem);
        } else if (PolicyDefinitionUtil.isConsumer(elem.getNodeName())) {
            processConsumer(group, endpoint, characteristic, elem);
        } else {
            // TODO: Decide whether this should be an exception??
        }
    }

    protected void processConsumer(PolicyGroup group, Endpoint endpoint,
                    Characteristic characteristic, Element elem) {

        // Check if element is first action in top level route
        if (elem.getParentNode() == null
                || elem.getParentNode().getParentNode() == null
                || !elem.getParentNode().getNodeName().equals("route")
                || !elem.getParentNode().getParentNode().getNodeName().equals("camelContext")) {
            return;
        }

        // Extract final send actions, if using this same characteristic
        Element nextActions=elem.getOwnerDocument().createElement("nextActions");
        Element containedNodes=elem.getOwnerDocument().createElement("containedActions");
        boolean f_findProducers=true;

        org.w3c.dom.NodeList nl=elem.getParentNode().getChildNodes();

        for (int i=nl.getLength()-1; i >= 0; i--) {
            // Check if action is a producer
            Node n=nl.item(i);

            if (n == elem) {
                break;
            }

            if (n instanceof Element) {

                if (f_findProducers) {
                    f_findProducers = PolicyDefinitionUtil.isOnewayProducer(n.getNodeName());
                }

                if (f_findProducers) {
                    // Check if producer is an endpoint
                    String uri=((Element)n).getAttribute("uri");
                    String endpointName=PolicyDefinitionUtil.getEndpointName(uri);

                    if (endpointName != null) {
                        Endpoint ep=group.getEndpoint(endpointName);

                        if (ep != null
                                && ep.hasCharacteristic(BatchWithRetryOnFailure.class.getSimpleName())) {
                            DOMUtil.insertFirst(nextActions, n);
                            continue;
                        }
                    }
                }
            }

            DOMUtil.insertFirst(containedNodes, n);
        }

        applyTemplate(elem, containedNodes, nextActions);

        defineRetryAction(elem);

        addBean(elem.getOwnerDocument(), "aggregatorStrategy",
                io.scepta.runtime.ListAggregator.class.getName());
        addBean(elem.getOwnerDocument(), "retrySupport",
                io.scepta.runtime.RetrySupport.class.getName());
    }

    protected void addBean(Document doc, String id, String clsName) {
        NodeList nl=doc.getDocumentElement().getElementsByTagName("bean");
        boolean f_found=false;

        for (int i=0; !f_found && i < nl.getLength(); i++) {
            String idAttr=((Element)nl.item(i)).getAttribute("id");

            if (idAttr != null && idAttr.equals(id)) {
                f_found = true;
            }
        }

        if (!f_found) {
            org.w3c.dom.Element bean=doc.createElement("bean");

            bean.setAttribute("id", id);
            bean.setAttribute("class", clsName);

            doc.getDocumentElement().appendChild(bean);
        }
    }

    protected void defineRetryAction(Element elem) {
        Element retryRef=(Element)elem.getOwnerDocument().getElementsByTagName("RETRY").item(0);

        if (retryRef != null) {
            Element newNode=elem.getOwnerDocument().createElement("inOnly");
            newNode.setAttribute("uri", elem.getAttribute("uri"));

            DOMUtil.replaceNode(retryRef, newNode);
        }
    }

    protected void applyTemplate(Element elem, Element containedNodes, Element nextActions) {
        // Apply template
        Element template=(Element)elem.getOwnerDocument().importNode(
                (nextActions.getChildNodes().getLength() > 0 ? CONSUMER_PRODUCER_TEMPLATE :
                    CONSUMER_ONLY_TEMPLATE), true);

        // Append template nodes after the current element
        Element parent=(Element)elem.getParentNode();

        while (template.hasChildNodes()) {
            parent.appendChild(template.getFirstChild());
        }

        DOMUtil.replaceNodes((Element)parent.getElementsByTagName("ACTIONS").item(0), containedNodes);

        if (nextActions.getChildNodes().getLength() > 0) {
            DOMUtil.replaceNodes((Element)parent.getElementsByTagName("NEXT").item(0), nextActions);
        }
    }

   protected void processProducer(PolicyGroup group, Endpoint endpoint,
                            Characteristic characteristic, Element elem) {

        /* 1) Need to add aggregatorStrategy bean at top level if not already defined
         * 2) Need to replace the completionSize and completionInterval info with the options
         *    defined for the characteristic - so that needs to be passed in.
         * 3) The actual uri should have been replaced before being passed to this process.
         * 4) Ideally we should use a template, that contains some kind of marker where the
         *    element should be relocated to.
         *
        <route id="receiveActivityUnit">
          <aggregate strategyRef="aggregatorStrategy" completionSize="..." completionInterval="..." >
            <correlationExpression>
                <constant>true</constant>
            </correlationExpression>
            <setHeader headerName="retryCount">
              <constant>0</constant>
            </setHeader>
            <inOnly uri="..."/>
          </aggregate>
        </route>
         */

        Element route=elem.getOwnerDocument().createElement("route");

        Element aggregate=elem.getOwnerDocument().createElement("aggregate");
        route.appendChild(aggregate);

        aggregate.setAttribute("strategyRef", "aggregateStrategy");
        aggregate.setAttribute("completionSize", characteristic.getProperties().get("batchSize"));
        aggregate.setAttribute("completionInterval", characteristic.getProperties().get("batchInterval"));

        Element correlationExpression=elem.getOwnerDocument().createElement("correlationExpression");
        aggregate.appendChild(correlationExpression);

        Element constant1=elem.getOwnerDocument().createElement("constant");
        correlationExpression.appendChild(constant1);

        Text constant1Text=elem.getOwnerDocument().createTextNode("0");
        constant1.appendChild(constant1Text);

        Element setHeader=elem.getOwnerDocument().createElement("setHeader");
        aggregate.appendChild(setHeader);

        setHeader.setAttribute("headerName", "retryCount");

        Element constant2=elem.getOwnerDocument().createElement("constant");
        setHeader.appendChild(constant2);

        Text constant2Text=elem.getOwnerDocument().createTextNode("0");
        constant2.appendChild(constant2Text);

        // Add new route inplace of supplied element
        elem.getParentNode().replaceChild(route, elem);
        aggregate.appendChild(elem);

        addBean(elem.getOwnerDocument(), "aggregatorStrategy",
                io.scepta.runtime.ListAggregator.class.getName());
    }

    @Override
    public Set<Dependency> getDependencies() {
        return (DEPENDENCIES);
    }

}
