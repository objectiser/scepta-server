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
package io.scepta.server;

public interface Generator {

    /**
     * This method generates an executable version of the
     * supplied policy group.
     *
     * @param group The policy group
     * @return The executable representation
     */
    GeneratedResult generate(PolicyGroupInterchange group);

    /**
     * This method returns the list of supported characteristic types.
     *
     * @return The list of characteristic types
     */
    java.util.List<CharacteristicType> getSupportedCharacteristicTypes();

}
