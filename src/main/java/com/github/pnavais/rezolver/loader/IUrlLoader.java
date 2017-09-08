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

package com.github.pnavais.rezolver.loader;

public interface IUrlLoader {

    /**
     * Retrieves the URL schema
     *
     * @return the URL schema
     */
    String getURL_Scheme();

    /**
     * Removes the URL scheme from a given location
     *
     * @param location the location
     * @return the location without URL scheme
     */
    default String stripScheme(String location) {
        return location.replaceFirst("^"+getURL_Scheme()+":", "");
    }
}
