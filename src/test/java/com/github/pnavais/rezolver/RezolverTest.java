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

package com.github.pnavais.rezolver;

import org.junit.Test;

import java.net.URL;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Rezolver JUnit tests
 */
public class RezolverTest extends RezolverBaseTest {

    @Test
    public void defaultClassPathResourceTest() {
        URL clRes = Rezolver.resolve("classpath:META-INF/resource.nfo");
        assertNotNull("Error loading resource from classpath", clRes);
        URL rRes = Rezolver.newBuilder().build().lookup("META-INF/resource.nfo");
        assertNotNull("Error resolving resource from classpath", rRes);
        assertThat(clRes.toExternalForm(), is(rRes.toExternalForm()));
    }


    @Test
    public void defaultBuilderLoadingTest() {
        URL dfRes = Rezolver.resolve("classpath:META-INF/resource.nfo");
        URL bdRes = Rezolver.newBuilder().build().lookup("classpath:META-INF/resource.nfo");
        assertNotNull("Error loading resource from classpath with default chain", dfRes);
        assertNotNull("Error loading resource from classpath with default builder", bdRes);
        assertThat(dfRes.toExternalForm(), is(bdRes.toExternalForm()));
    }

}
