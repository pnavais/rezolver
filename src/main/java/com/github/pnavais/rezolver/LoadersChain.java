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

import com.github.pnavais.rezolver.loader.IResourceLoader;

import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * A {@link LoadersChain} contains several context aware loader implementations
 * and is intended for sequential iteration.
 */
public class LoadersChain<R> {

    /**
     * The Loaders chain.
     */
    private Collection<IResourceLoader> loadersChain;

    /**
     * Instantiates a new Loaders chain.
     */
    public LoadersChain() {
        this.loadersChain = new ArrayDeque<>();
    }

    /**
     * Instantiates a new Loaders chain with the given
     * items.
     * @param loadersChain the loader items
     */
    public LoadersChain(Collection<IResourceLoader> loadersChain) {
        requireNonNull(loadersChain);
        this.loadersChain = loadersChain;
    }

    /**
     * Adds a new loader to the chain
     *
     * @param loader the loader to add
     */
    public void add(IResourceLoader loader) {
        this.loadersChain.add(loader);
    }

    /**
     * Retrieves the loaders chain
     *
     * @return the loaders chain
     */
    public Collection<IResourceLoader> getLoadersChain() {
        return loadersChain;
    }

    /**
     * Creates a loaders chain from the given parameterized varargs
     * of loaders
     * @param loaders the loaders
     * @param <T> the parameterized type
     * @return
     */
    @SafeVarargs
    public static <T> LoadersChain<T> from(IResourceLoader... loaders) {
        LoadersChain<T> chain = new LoadersChain<>();
        Optional.ofNullable(loaders).ifPresent(ls -> Arrays.stream(ls).forEachOrdered(chain::add));
        return chain;
    }
}
