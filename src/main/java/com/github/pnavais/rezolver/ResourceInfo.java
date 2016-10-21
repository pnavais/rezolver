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
import java.util.Optional;

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
     * This class is kept private to
     * avoid instantiation. The builder
     * must be used.
     */
    private ResourceInfo() { }

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
     * A builder for the resource info
     */
    public static class ResourceInfoBuilder {

        /** The builder instance */
        private ResourceInfo instance = new ResourceInfo();

        /**
         * Retrieves the configured resource info instance
         * @return the resource info instance
         */
        public ResourceInfo build() {
            return instance;
        }

        /**
         * Sets the search path of the resource info
         *
         * @param location the search path to look for
         * @return the resource info builder
         */
        public ResourceInfoBuilder with(String location) {
            instance.setSearchPath(location);
            return this;
        }

        /**
         * Sets the URL of the resource. If null
         * the resource is considered as not
         * resolved.
         *
         * @param resourceURL the resource's URL
         * @return the resource info builder
         */
        public ResourceInfoBuilder as(URL resourceURL) {
            instance.setURL(resourceURL);
            instance.setResolved(resourceURL!=null);
            instance.setSourceEntity("Unknown");
            return this;
        }

        /**
         * Sets the source entity of the resource
         * in case the resource is resolved.
         *
         * @param sourceEntity the source entity
         * @return the resource info builder
         */
        public ResourceInfoBuilder from(String sourceEntity) {
            instance.setSourceEntity(instance.isResolved() ? sourceEntity : "Unknown");
            return this;
        }
    }

    /**
     * Creates a new resource info builder
     *
     * @return the resource info builder
     */
    public static ResourceInfoBuilder builder() {
        return new ResourceInfoBuilder();
    }
}
