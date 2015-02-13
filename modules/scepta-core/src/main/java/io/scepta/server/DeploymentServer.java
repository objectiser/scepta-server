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

/**
 * This interface represents a deployment server.
 *
 */
public interface DeploymentServer {

    /**
     * This method deploys the executable version of a policy group associated with
     * a particular tag.
     *
     * @param organization The organization
     * @param group The policy group
     * @param tag The tag name
     * @param is The executable representation
     */
    void deploy(String organization, String group, String tag, java.io.InputStream is);

    /**
     * This method retrieves the list of deployed tags for the organization and
     * policy group
     *
     * @param organization The organization
     * @param group The policy group
     * @return The list of tags
     */
    java.util.List<String> getDeployedTags(String organization, String group);

    /**
     * This method retrieves the executable representation of the requested policy group
     * and tag.
     *
     * @param organization The organization
     * @param group The policy group
     * @param tag The tag name
     * @param os The output stream to return the executable
     */
    void getDeployment(String organization, String group, String tag, java.io.OutputStream os);

}
