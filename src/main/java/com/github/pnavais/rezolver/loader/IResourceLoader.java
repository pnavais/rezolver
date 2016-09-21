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

import com.github.pnavais.rezolver.Context;

import java.net.URL;

/**
 * Common interface for all loader implementations.
 */
public interface IResourceLoader {

    /**
     * Resolves the given resource location path to obtain
     * a valid URL or null when the loader cannot resolve it.
     *
     * Examples of valid URLs :
     * <code>
     *     <ul>
     *     <li>"/home/pnavais/myfile.nfo"</li>
     *     <li>"file:///C:/Users/pnavais/test/image.png"</li>
     *     <li>"classpath:/META-INF/resource.xml"</li>
     *     <li>"https://github.com/pnavais/rezolver/"</li>
     *     </ul>
     *  </code>
     *
     * @param path the resource location path
     * @param context the resolution context
     * @return the output context
     */
    Context resolve(String path, Context context);

    /**
     * Try to resolve the file on the local file system
     * or classpath using the default path if supplied
     * in the context.
     *
     * @param resourcePath the path to the resource
     * @return the URl if the file is resolved, null otherwise
     */
    URL resolveWithFallback(String resourcePath);

    /**
     * Sets the fallback path to append
     * to a resource path when all other
     * resolution methods failed.
     *
     * @param fallbackPath the fallback path
     */
    void setFallbackPath(String fallbackPath);

}
