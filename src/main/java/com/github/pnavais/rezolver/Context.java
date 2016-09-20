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
import java.util.WeakHashMap;

/**
 * A {@link Context} is a basic container to store the resolved
 * URL of the resource, the resolution status and any arbitrary data
 * needed during resource resolution.
 */
public class Context {

    /** The resource resolution status */
    private boolean isResolved;

    /** The resource's resolved URL */
    private URL resURL;

    /** The source entity that resolved the resource */
    private String sourceEntity;

    /** A map to store any arbitrary information needed during resolution */
    private WeakHashMap<String, Object> map;

    /**
     * Default constructor
     */
    public Context() {
        this.map = new WeakHashMap<>();
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
     * Sets the resolution source
     *
     * @param source the source entity
     */
    public void setSourceEntity(String source) {
        this.sourceEntity = source;
    }

    /**
     * Retrieves the resource's resolved
     * URL or null if not resolved.
     *
     * @return the resolved URL
     */
    public URL getResURL() {
        return resURL;
    }

    /**
     * Sets the resource's resolved URL
     *
     * @param resURL the resolved URL
     */
    public void setResURL(URL resURL) {
        this.resURL = resURL;
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
     * Keeps in the context a value for
     * the given key.
     *
     * @param key   the key
     * @param value the value
     */
    public void setProperty(String key, Object value) {
        this.map.put(key,value);
    }

    /**
     * Retrieves the value of the property
     * identified by the given key.
     *
     * @param key the key
     * @return the property
     */
    public Object getProperty(String key) {
        return this.map.get(key);
    }

    /**
     * Resets the context values
     */
    public void clear() {
        this.map.clear();
        isResolved = false;
        sourceEntity = null;
    }
}
