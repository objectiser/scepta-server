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

import org.w3c.dom.Element;
import org.w3c.dom.Text;

import io.scepta.design.generator.CharacteristicProcessor;
import io.scepta.design.model.Characteristic;
import io.scepta.design.util.DOMUtil;

/**
 * This class implements the 'BatchWithRetryOnFailure' characteristic.
 *
 */
public class BatchWithRetryOnFailure implements CharacteristicProcessor {

    private static final org.w3c.dom.Element CONSUMER_PRODUCER_TEMPLATE;

    static {
        org.w3c.dom.Document doc=null;

        try {
            doc = DOMUtil.textToDoc(BatchWithRetryOnFailure.class.getResourceAsStream(
                    "/templates/BatchWithRetryOnFailure-consumer-producer.xml"));
        } catch (Exception e) {
            // TODO: ERROR
            e.printStackTrace();
        }

        if (doc != null) {
            CONSUMER_PRODUCER_TEMPLATE = doc.getDocumentElement();
        } else {
            CONSUMER_PRODUCER_TEMPLATE = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return getClass().getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(Characteristic charactistic, Element elem) {

        if (elem.getNodeName().equals("to") || elem.getNodeName().equals("inOnly")) {
            processProducer(charactistic, elem);
        } else if (elem.getNodeName().equals("from")) {
            processConsumer(charactistic, elem);
        } else {
            // TODO: Decide whether this should be an exception??
        }
    }

    protected void processConsumer(Characteristic charactistic, Element elem) {

    }

    protected void processProducer(Characteristic charactistic, Element elem) {

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
        aggregate.setAttribute("completionSize", charactistic.getProperties().get("completionSize"));
        aggregate.setAttribute("completionInterval", charactistic.getProperties().get("completionInterval"));

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
    }

}
