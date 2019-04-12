/*
 * $Id$
 *
 * SARL is an general-purpose agent programming language.
 * More details on http://www.sarl.io
 *
 * Copyright (C) 2014-2019 the original authors or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sarl.sarlsh.modules.internal;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import io.sarl.lang.interpreter.SarlInterpreter;

/** Module for creating the SARL interpreter with the configuration provided by bootique modules.
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.10
 */
public class SarlInterpreterModule extends AbstractModule {

	@Override
	protected void configure() {
		//
	}

	/** Replies the SARL interpreter.
	 *
	 * @param injector the current injector.
	 * @return the SARL batch compiler
	 */
	@SuppressWarnings({"static-method", "checkstyle:npathcomplexity"})
	@Provides
	@Singleton
	public SarlInterpreter provideSarlInterpreter(Injector injector) {
		final SarlInterpreter interpreter = new SarlInterpreter();
		injector.injectMembers(interpreter);
		return interpreter;
	}

}
