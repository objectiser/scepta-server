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
package io.scepta.model;


// QUESTION:
// Should we have range? if so needs to cope with maven and osgi bundle versions. Alternatively
// could have single version field, if explicitly defined, or if blank then use whatever is available
// in the target environment - issue is that for testing will need to know version (but that could be
// provided as part of test), and diff target environments - some may have dependency, and others not
// but suppose could be explicit, but then remove if target environment config indicates to override
// dependency versions???
// FOR NOW: just use maven dependencies, and these would be included in bundle, unless overridden by
// target deployment descriptor that may include replacement details?? Might put more burden on the
// person describing the target environment though?

public class Dependency {

    private String _groupId;
    private String _artifactId;
    private String _version;

    public String getGroupId() {
        return (_groupId);
    }

    public Dependency setGroupId(String groupId) {
        _groupId = groupId;
        return (this);
    }

    public String getArtifactId() {
        return (_artifactId);
    }

    public Dependency setArtifactId(String artifactId) {
        _artifactId = artifactId;
        return (this);
    }

    public String getVersion() {
        return (_version);
    }

    public Dependency setVersion(String version) {
        _version = version;
        return (this);
    }
}
