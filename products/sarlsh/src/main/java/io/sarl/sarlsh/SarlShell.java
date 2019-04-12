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

package io.sarl.sarlsh;

import java.util.List;

import io.bootique.help.HelpOption;

/** This class implements an interactive shell for SARL.
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.10
 */
public final class SarlShell {

	/** Constructor.
	 */
	private SarlShell() {
		//
	}

	/** Main program of the batch compiler.
	 *
	 * @param args the command line arguments.
	 */
	public static void main(String[] args) {
		final int retCode = createMainObject().runShell(args);
		System.exit(retCode);
	}

	/** Replies the default name of the program.
	 *
	 * @return the default name of the program.
	 */
	public static String getDefaultCompilerProgramName() {
		return Constants.PROGRAM_NAME;
	}

	/** Create the instance of the bootique main launcher.
	 *
	 * @return the main launcher.
	 */
	protected static BootiqueSarlShell createMainObject() {
		return new BootiqueSarlShell();
	}

	/** Replies the options of the program.
	 *
	 * @return the options of the program.
	 */
	public static List<HelpOption> getOptions() {
		return createMainObject().getOptions();
	}

}
