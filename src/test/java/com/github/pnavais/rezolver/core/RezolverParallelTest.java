/*
 * Copyright 2016 Pablo Navais
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.pnavais.rezolver.core;

import com.github.pnavais.rezolver.ResourceInfo;
import com.github.pnavais.rezolver.Rezolver;
import org.junit.Test;

import java.net.URL;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Rezolver Local files tests in parallel
 */
public class RezolverParallelTest extends RezolverTestBase {

    @Test
    public void resolveMultipleFilesTest() {
        // Resolve with custom loader in parallel
        final Rezolver r = Rezolver.builder().add(localLoader).build();

        IntStream.range(0, MAX_TEST_FILES).parallel().forEach(i -> {
            String rezName = "/tmp/fs_resource_"+i+".nfo";
            ResourceInfo info = r.resolve(rezName);
            assertNotNull("Error processing resource "+rezName, info);
            assertNotNull("Error resolving resource "+rezName, info.getURL());
            URL expectedURL = localLoader.lookup(rezName);
            assertEquals("URL resolution mismatch", expectedURL, info.getURL());
            assertNotNull("Error retrieving source entity for resource"+rezName, info.getSourceEntity());
        });
    }

}
