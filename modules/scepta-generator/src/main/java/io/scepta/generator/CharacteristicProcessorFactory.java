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
import io.scepta.server.CharacteristicType;

import java.util.Collections;
import java.util.Comparator;
import java.util.ServiceLoader;

/**
 * This class managers the set of characteristic processors.
 *
 */
public class CharacteristicProcessorFactory {

    private static final ServiceLoader<CharacteristicProcessor> PROCESSORS=
                    ServiceLoader.load(CharacteristicProcessor.class);

    /**
     * This method returns a processor associated with the supplied
     * characteristic.
     *
     * @param characteristic The characteristic to be processed
     * @return The characteristic processor, or null if not found
     */
    public static CharacteristicProcessor get(Characteristic characteristic) {
        for (CharacteristicProcessor cp : PROCESSORS) {
            if (cp.getType().equals(characteristic.getType())) {
                return (cp);
            }
        }

        return (null);
    }

    /**
     * This method returns the list of characteristic types.
     *
     * @return The list of characteristic types
     */
    public static java.util.List<CharacteristicType> getCharacteristicTypes() {
        java.util.List<CharacteristicType> ret=new java.util.ArrayList<CharacteristicType>();

        for (CharacteristicProcessor cp : PROCESSORS) {
            ret.add(cp.getType());
        }

        Collections.sort(ret, new Comparator<CharacteristicType>() {
            public int compare(CharacteristicType o1, CharacteristicType o2) {
                return (o1.getName().compareTo(o2.getName()));
            }

        });

        return (ret);
    }

}
