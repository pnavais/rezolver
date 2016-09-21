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
 * <b>RemoteLoader</b>
 * <p>
 *  Resolves the location of a given resource URL if not specified in relative,
 *  local or classpath formats.The URL shall contain a valid remote schema (e.g. http, ftp, etc...)
 * </p>
 */
public class RemoteLoader implements IResourceLoader {

    /**
     * Private constructor to avoid external instantiation
     */
    public RemoteLoader() {}

    /**
     * Tries to resolve the resource location on the local file system
     * or classpath.
     *
     * @param path the resource location path
     * @return the resolved URL or null if not resolved.
     */
    public URL resolveURL(String path, Context context) {
        requireNonNull(path);
        URL resourceURL = null;
        //Context result = (context != null) ? context : new Context();

        return resourceURL;
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
        return null;
    }

    /**
     * Sets the fallback path as last resort for
     * resource resolution
     *
     * @param fallbackPath the fallback path
     */
    @Override
    public void setFallbackPath(String fallbackPath) {
    }

}
