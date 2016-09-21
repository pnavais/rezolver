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

import java.net.URL;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Ease the creation of IResourceLoader implementations using function predicates.
 */
public class LoaderBuilder {

    /**
     * Creates a simple resource solver with only a resource solver function
     * and no fallback options.
     *
     * @param resFunction the resource solver function
     * @return the new resource loader
     */
    public static IResourceLoader with(BiFunction<String, Context, URL> resFunction) {
        return with(resFunction, null, null);
    }

    /**
     * Creates a custom resource loader with the given function predicates :
     * <ul>
     *     <li>A Context resolver function</li>
     *     <li>A fallback resolver function</li>
     *     <li>A fallback path setter function</li>
     * </ul>
     * @param resFunction the resolver function
     * @param fbFunction the fallback resolver function
     * @param fbSetter the fallback path setter function
     * @return a new IResourceLoader implemented with the given predicates.
     */
    public static IResourceLoader with(BiFunction<String, Context, URL> resFunction, Function<String, URL> fbFunction, Consumer<String> fbSetter) {
        return new IResourceLoader() {
            @Override
            public URL resolveURL(String path, Context context) {
                return Optional.ofNullable(resFunction).map(f -> f.apply(path, context)).orElse(null);
            }

            @Override
            public URL resolveWithFallback(String resourcePath) {
                return Optional.ofNullable(fbFunction).map(f -> f.apply(resourcePath)).orElse(null);
            }

            @Override
            public void setFallbackPath(String fallbackPath) {
                Optional.ofNullable(fbSetter).ifPresent(f -> f.accept(fallbackPath));
            }
        };
    }
}
