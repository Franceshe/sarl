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
package io.sarl.maven.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.apache.maven.it.Verifier;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import io.sarl.lang.SARLVersion;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
public class Bug799Test extends AbstractMojoTest {

	private static final String EXPECTED_A = multilineString(
			"package io.sarl.maven.bug799;",
			"import io.sarl.lang.annotation.SarlElementType;",
			"import io.sarl.lang.annotation.SarlSpecification;",
			"import io.sarl.lang.annotation.SyntheticMember;",
			"import io.sarl.lang.core.Agent;",
			"import io.sarl.lang.core.Skill;",
			"import io.sarl.maven.bug799.Cap;",
			"import org.eclipse.xtext.xbase.lib.Pure;",
			"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
			"@SarlElementType(21)",
			"@SuppressWarnings(\"all\")",
			"public class A extends Skill implements Cap {",
			"@Pure",
			"public void act_connectToSimulator(final String address, final int port) {",
			"while (true) {",
			"}",
			"}",
			"@SyntheticMember",
			"public A() {",
			"super();",
			"}",
			"@SyntheticMember",
			"public A(final Agent arg0) {",
			"super(arg0);",
			"}",
			"}");
	
	// FIXME: Enable the tests when the issue on the Maven compiler is fixed.
	@Ignore
	@Test
	public void compile() throws Exception {
		Verifier verifier = executeMojo("bug799", "compile");
		Path path = FileSystems.getDefault().getPath(
				"src", "main", "generated-sources", "sarl",
				"io", "sarl", "maven", "bug799", "A.java");
		assertNotNull(path);
		verifier.assertFilePresent(path.toString());
		assertEquals(EXPECTED_A, readFile(verifier, path));
	}

}
