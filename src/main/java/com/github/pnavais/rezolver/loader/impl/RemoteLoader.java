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

package com.github.pnavais.rezolver.loader.impl;

import com.github.pnavais.rezolver.ResourceInfo;

/**
 * <b>RemoteLoader</b>
 * <p>
 *  Resolves the location of a given resource URL if not specified in relative,
 *  local or classpath formats.The URL shall contain a valid remote schema (e.g. http, ftp, etc...)
 * </p>
 */
public class RemoteLoader extends URL_Loader {

    /**
     * Private constructor to avoid external instantiation
     */
    public RemoteLoader() {}

    /**
     * Retrieves the URL from the given resource path
     * in the filesystem.
     *
     * @param location the path to the resource in the filesystem
     * @return the URL to the resource
     */
    @Override
    public ResourceInfo lookup(String location) {
        return ResourceInfo.notSolved(location);
    }

    /**
     * Retrieves the URL scheme associated to the loader
     *
     * @return the URL scheme
     */
    @Override
    public String getURL_Scheme() {
        return "*";
    }


}
