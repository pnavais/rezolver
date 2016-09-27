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

import com.github.pnavais.rezolver.Context;
import com.github.pnavais.rezolver.loader.IContextAware;
import com.github.pnavais.rezolver.loader.IResourceLoader;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * A base class with context for all resource loader implementations
 *
 * @param <R> the type parameter
 */
public abstract class ContextAwareLoader<R> implements IResourceLoader<R>, IContextAware<R> {

    /** The resolution context. */
    protected Context<R> context;

    /**
     * Resolves the given resource location  information to obtain
     * the actual resource or null when the loader cannot resolve it.
     *
     * @param location the resource location
     * @return the resolved resource
     */
    public R resolve(String location) {
        requireNonNull(location);
        R resource;

        // Initializes the resolution info
        Context<R> targetCtx = initContextData();

        // Lookup the resource using its location
        resource = lookup(location);

        // Update the context with resolution info
        updateContextData(resource, targetCtx);

        return resource;
    }

    /**
     * Initializes the context data for a new lookup
     *
     * @return the context data initialized
     */
    private Context<R> initContextData() {
        Context<R> rContext = Optional.ofNullable(getContext()).orElse(new Context<>());
        rContext.setResURL(null);
        rContext.setSourceEntity(null);
        rContext.setResolved(false);
        return rContext;
    }

    /**
     * Update the context data after a lookup.
     *
     * @param resource the found resource
     * @param context the context used during resolution
     */
    private void updateContextData(R resource, Context<R> context) {
        // Establish the resolution data
        context.setItem(resource);
        context.setResolved(resource != null);
        context.setSourceEntity(Optional.ofNullable(context.getSourceEntity()).
                orElse(context.isResolved() ? getClass().getSimpleName(): null));

        // Update the context
        setContext(context);
    }

    /**
     * Custom logic to perform a lookup of the location
     * and retrieve the actual resource.
     *
     * @param location the location of the resource
     * @return the resource
     */
    public abstract R lookup(String location);

    /**
     * Establishes the context to use
     * during resource resolution
     *
     * @param context the context to use
     */
    @Override
    public void setContext(Context<R> context) {
        this.context = context;
    }

    /**
     * Retrieves the context used
     * during resource resolution
     *
     * @return the context
     */
    @Override
    public Context<R> getContext() {
        return this.context;
    }
}
