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

import io.scepta.model.Issue;

/**
 * This interface represents a component responsible for validating the policy
 * group.
 *
 */
public interface Validator {

    /**
     * This method validates the supplied policy group and returns
     * any issues that were detected.
     *
     * @param group The policy group
     * @return The list of issues detected
     */
    java.util.List<Issue> valid(PolicyGroupInterchange group);

}
