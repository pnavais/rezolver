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

import java.net.URL;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;

/**
 * A {@link ResourceInfo} is a basic container to store the resolved
 * URL of the resource, the resolution status and any arbitrary data
 * needed during resource resolution.
 */
public class ResourceInfo {

    /** The path that triggered the search */
    private String searchPath;

    /** The resource resolution status */
    private boolean isResolved;

    /** The resource's resolved URL (if available)*/
    private URL url;

    /** The source entity that resolved the resource */
    private String sourceEntity;

    /**
     * Sets the search path
     *
     * @param searchPath the search path
     */
    public void setSearchPath(String searchPath) {
        this.searchPath = searchPath;
    }

    /**
     * Retrieves the search path
     *
     * @return the search path
     */
    public String getSearchPath() {
        return searchPath;
    }

    /**
     * Checks whether the resource can be
     * considered as resolved or not.
     *
     * @return true if resolved, false otherwise
     */
    public boolean isResolved() {
        return isResolved;
    }

    /**
     * Sets the resource resolution status
     *
     * @param resolved the resolution status
     */
    public void setResolved(boolean resolved) {
        isResolved = resolved;
    }

    /**
     * Retrieves the resource's resolved
     * URL or null if not resolved.
     *
     * @return the resolved URL
     */
    public URL getURL() {
        return url;
    }

    /**
     * Sets the resource's resolved URL
     *
     * @param resURL the resolved URL
     */
    public void setURL(URL resURL) {
        this.url = resURL;
    }

    /**
     * Sets the resolution source
     *
     * @param source the source entity
     */
    public void setSourceEntity(String source) {
        this.sourceEntity = source;
    }

    /**
     * Retrieves the last source entity
     * that processed a resource location.
     * This entity may or not be the one
     * who resolved the source.
     *
     * @return the source entity
     */
    public String getSourceEntity() {
        return sourceEntity;
    }

    /**
     * Creates a new @{@link ResourceInfo} with no
     * resolution status for the given location
     *
     * @param location the location to search
     * @return the resource info
     */
    public static ResourceInfo notSolved(String location) {
        return solved(location, null);
    }

    /**
     * Creates a new @{@link ResourceInfo} with no
     * resolution status for the given location
     *
     * @param location the location to search
     * @return the resource info
     */
    public static ResourceInfo solved(String location, URL resURL) {
        ResourceInfo info = new ResourceInfo();
        info.setSearchPath(location);
        info.setURL(resURL);
        info.setResolved(resURL!=null);
        info.setSourceEntity(info.isResolved() ? info.lookupSourceEntity() : "Unknown");
        return info;
    }

    /**
     * Creates a new @{@link ResourceInfo} from the
     * supplied resource information.
     *
     * @param resourcePath the requested resource location
     * @param resourceURL the resolved resource URL
     * @return the resource info
     */
    public static ResourceInfo from(String resourcePath, URL resourceURL) {
        return (resourceURL != null) ? solved(resourcePath, resourceURL) : notSolved(resourcePath);
    }

    /**
     * Lookup in the thread stack for the caller's class name
     * @return the caller's class name
     */
    private String lookupSourceEntity() {
        return Arrays.stream(Thread.currentThread().getStackTrace())
                .skip(1)
                .filter(st -> !st.getClassName().equals(this.getClass().getName()))
                .findFirst().map(StackTraceElement::getClassName).orElse("Unknown");
    }

    @Override
    public String toString() {
        return "ResourceInfo{" +
                "searchPath='" + searchPath + '\'' +
                ", isResolved=" + isResolved +
                ", url=" + url +
                ", sourceEntity='" + sourceEntity + '\'' +
                '}';
    }
}
