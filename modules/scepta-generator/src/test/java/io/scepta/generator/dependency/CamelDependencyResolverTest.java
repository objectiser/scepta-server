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
package io.scepta.generator.dependency;

import static org.junit.Assert.*;
import io.scepta.model.Dependency;

import org.junit.Test;

public class CamelDependencyResolverTest {

    @Test
    public void testGetCamelComponentDependenciesFound() {
        String uri="jms";

        java.util.Set<Dependency> dependencies=
                    CamelDependencyResolver.getCamelComponentDependencies(uri);

        assertNotNull(dependencies);

        assertTrue(dependencies.size() == 1);
    }

    @Test
    public void testGetCamelComponentDependenciesNotFound() {
        String uri="madeup";

        java.util.Set<Dependency> dependencies=
                CamelDependencyResolver.getCamelComponentDependencies(uri);

        assertNotNull(dependencies);

        assertTrue(dependencies.size() == 0);
    }
}
