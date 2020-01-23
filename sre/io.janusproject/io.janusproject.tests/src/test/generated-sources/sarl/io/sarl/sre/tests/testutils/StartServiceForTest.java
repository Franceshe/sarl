/**
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
package io.sarl.sre.tests.testutils;

import io.sarl.lang.annotation.SarlElementType;
import io.sarl.lang.annotation.SarlSpecification;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Start the service for the unit test.
 * 
 * See the {@link AvoidServiceStartForTest} for avoiding service start.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SarlSpecification("0.11")
@SarlElementType(13)
public @interface StartServiceForTest {
  /**
   * Replies if the service start is done before or after the execution of the functions marked with <code>@Before</code<.
   * 
   * @return <code>true</code> if the service should be started after the marked functions; <code>false</code> if the service
   *         should be started after.
   */
  public boolean startAfterSetUp() default false;
  /**
   * Replies if the service is created before or after the execution of the functions marked with <code>@Before</code<.
   * 
   * @return <code>true</code> if the service should be created after the marked functions; <code>false</code> if the service
   *         should be created after.
   */
  public boolean createAfterSetUp() default false;
}
