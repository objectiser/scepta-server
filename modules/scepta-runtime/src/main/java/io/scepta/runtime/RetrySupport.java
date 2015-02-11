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
package io.scepta.runtime;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

public class RetrySupport {

    public void init(Exchange exchange) {
        Message mesg=exchange.getIn();

        int retryCount=mesg.getHeader("retryCount", Integer.class);
        mesg.setHeader("retryCount", ++retryCount);

        mesg.setHeader("retryList", new java.util.ArrayList<Object>());
    }

    public void saveContent(Exchange exchange) {
        exchange.getIn().setHeader("originalValue", exchange.getIn().getBody());
    }

    public void addToRetryList(Exchange exchange) {
        Message mesg=exchange.getIn();

        @SuppressWarnings("unchecked")
        java.util.List<Object> retryList=(java.util.List<Object>)mesg.getHeader("retryList");

        retryList.add(mesg.getHeader("originalValue"));

        mesg.setHeader("retryList", retryList);
    }
}
