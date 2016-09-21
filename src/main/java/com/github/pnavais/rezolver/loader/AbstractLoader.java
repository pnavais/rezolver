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
 * <b>AbstractLoader</b>
 * <p>
 *     The common base class for all resource loader's implementations.
 * </p>
 */
public abstract class AbstractLoader implements IResourceLoader {

    /** The path to append in case resolution failed */
    protected String fallbackPath;

    /**
     * Tries to resolve the resource location on the local file system
     * or classpath.
     *
     * @param path the resource location path
     * @return the resolved URL or null if not resolved.
     */
    @Override
    public Context resolve(String path, Context context) {
        requireNonNull(path);
        URL resourceURL = null;
        Context result = (context != null) ? context : new Context();

        // Try to resolve it using the schema prefix
        if (path.startsWith(getUrlScheme()+":")) {
            resourceURL = resolveResource(path.replaceFirst("^[^:]+:", ""));
        }

        // If the file is not resolved yet try to guess its location
        if (resourceURL == null) {
            resourceURL = resolveWithFallback(path);
        }

        // Set the resolved resource if any
        result.setResURL(resourceURL);
        result.setResolved(resourceURL!=null);
        if (resourceURL!=null) {
            result.setSourceEntity(getClass().getSimpleName());
        }

        return result;
    }

    /**
     * Tries to resolve the file on the local file system
     * or classpath using the default path if supplied
     * in the context.
     *
     * @param resourcePath the path to the resource
     * @return the URl if the file is resolved, null otherwise
     */
    @Override
    public URL resolveWithFallback(String resourcePath) {

        // Strip scheme if present
        resourcePath = resourcePath.replaceFirst("^[^:]+:", "");

        URL resourceURL =  resolveResource(resourcePath);
        // Last resort, try to resolve it using the fallback path as prefix
        if (resourceURL == null) {
            if ((fallbackPath != null) && (!resourcePath.startsWith(fallbackPath))) {
                resourceURL = resolveResource(fallbackPath + getPathSeparator() + resourcePath);
            }
        }
        return resourceURL;
    }

    /**
     * Sets the fallback path as last resort for
     * resource resolution
     *
     * @param fallbackPath the fallback path
     */
    @Override
    public void setFallbackPath(String fallbackPath) {
        this.fallbackPath = fallbackPath;
    }

    /**
     * Tries to resolve the URL of the given resource path.
     *
     * @param resourcePath the path to the resource
     * @return the resolved resource or null otherwise
     */
    public abstract URL resolveResource(String resourcePath);

    /**
     * Retrieves the URL scheme associated to the loader
     *
     * @return the URL scheme
     */
    protected abstract String getUrlScheme();

    /**
     * Retrieves the path separator for the loader.
     * *
     * @return the path separator
     */
    protected abstract String getPathSeparator();

}
