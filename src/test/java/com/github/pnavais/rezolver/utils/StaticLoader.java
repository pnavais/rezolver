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
package com.github.pnavais.rezolver.utils;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A <b> StaticLoader </b> allows to run a loader code only
 * once per singleton instance initialized.
 *
 * @author pnavais
 */
public class StaticLoader {
	
	/** The instance. */
	private Runnable loader;

	/** The current instance */
	private static StaticLoader instance;

	/** The constant lock. */
	private final static Lock lock = new ReentrantLock();

	/**
	 * Instantiates a new StaticLoader().
	 */
	private StaticLoader(Runnable l) {
		loader = l;
	}

	/**
	 * Run.
	 */
	private static void run(Runnable loader) {
		instance = new StaticLoader(loader);
		instance.loader.run();
	}

	/**
	 * Gets the payball StaticLoader instance.
	 *
	 * @param r the runnable
	 */
	public static void runOnce(Runnable r) {
		Optional.ofNullable(r).ifPresent(l -> {
			lock.lock();
			// Execute the runnable if not launched before
			if ((instance == null) || (l != instance.loader)) {
				try {
					run(l);
				} finally {
					lock.unlock();
				}
			}
		});
	}

	/**
	 * Resets the StaticLoader instance in order to
	 * allow another run of an external loader.
	 */
	public static void reset() {
		lock.lock();
		StaticLoader.instance = null;
		lock.unlock();
	}

}
