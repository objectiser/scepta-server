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
package io.scepta.generator;

import io.scepta.model.Characteristic;
import io.scepta.model.Dependency;
import io.scepta.model.Endpoint;
import io.scepta.model.PolicyGroup;
import io.scepta.server.CharacteristicType;

/**
 * This interface represents a processor associated with an endpoint characteristic.
 *
 */
public interface CharacteristicProcessor {

    /**
     * The characteristic type associated with the processor.
     *
     * @return The characteristic type
     */
    CharacteristicType getType();

    /**
     * This method processes the supplied policy definition based on the
     * characteristic. The policy group and endpoint are provided for
     * context, and in some cases, to enable the processor to access
     * information about other endpoints in the group.
     *
     * @param group The policy group
     * @param endpoint The endpoint
     * @param characteristic The characteristic
     * @param elem The element being processed
     */
    void process(PolicyGroup group, Endpoint endpoint,
                Characteristic characteristic, org.w3c.dom.Element elem);

    /**
     * This method returns any dependencies that are specific to the chacteristic.
     *
     * @return The characteristic's dependency set
     */
    java.util.Set<Dependency> getDependencies();

}
