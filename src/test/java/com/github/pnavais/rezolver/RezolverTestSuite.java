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

import com.github.pnavais.rezolver.core.RezolverFolderLoaderTest;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.runner.RunWith;
import com.github.pnavais.rezolver.core.RezolverLocalTest;
import com.github.pnavais.rezolver.core.RezolverParallelTest;

/**
 * Rezolver JUnit test suite
 */

@RunWith(JUnitPlatform.class)
@SuiteDisplayName("Rezolver tests")
@SelectClasses( { RezolverLocalTest.class , RezolverParallelTest.class, RezolverFolderLoaderTest.class })
public class RezolverTestSuite {
}
