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

import static java.util.Objects.requireNonNull;

/**
 * Common interface for all loader implementations.
 */
public interface IResourceLoader {

    /**
     * Tries to resolve the resource location and
     * use a fallback resolution in case of failure.
     *
     * @param path the resource location path
     * @return the resolved URL or null if not resolved.
     */
    default public Context resolve(String path, Context ctx) {
        requireNonNull(path);
        URL resourceURL = null;
        Context targetCtx = (ctx != null) ? ctx : new Context();

        // Try loader's default resolution
        resourceURL = resolveURL(path, targetCtx);

        // Use the fallback
        if (resourceURL == null) {
            resourceURL = resolveWithFallback(path);
        }

        // Set the resolved resource data
        targetCtx.setResURL(resourceURL);
        targetCtx.setResolved(resourceURL!=null);

        targetCtx.setSourceEntity((resourceURL != null) ?
                (targetCtx.getSourceEntity() == null) ?
                        getClass().getSimpleName() :
                        targetCtx.getSourceEntity() : null);

        return targetCtx;
    }

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
    URL resolveURL(String path, Context context);

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
