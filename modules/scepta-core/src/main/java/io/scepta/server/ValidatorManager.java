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

import java.util.ServiceLoader;

// TODO: Review how best to support validation, as we want to be able to
// validate the whole policy group interchange (i.e. internally consistent, etc)
// but also need localised validation (e.g. policy or resource definitions) but
// they may equally require other information within the policy group.

/**
 * This class managers the set of validators.
 *
 */
public class ValidatorManager {

    private ServiceLoader<Validator> _validators=ServiceLoader.load(Validator.class);

    /**
     * This method validates the supplied policy group and returns
     * any issues that were detected.
     *
     * @param group The policy group
     * @return The list of issues detected
     */
    public java.util.List<Issue> valid(PolicyGroupInterchange group) {
        java.util.List<Issue> ret=new java.util.ArrayList<Issue>();

        for (Validator v : _validators) {
            ret.addAll(v.valid(group));
        }

        return (ret);
    }

}
