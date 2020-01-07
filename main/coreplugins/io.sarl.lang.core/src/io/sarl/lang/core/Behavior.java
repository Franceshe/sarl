/*
 * $Id$
 *
 * SARL is an general-purpose agent programming language.
 * More details on http://www.sarl.io
 *
 * Copyright (C) 2014-2020 the original authors or authors.
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

package io.sarl.lang.core;

import io.sarl.lang.core.util.ClearableReference;

/**
 * A behavior maps a collection of perceptions represented by events to a collection of a task.
 *
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class Behavior extends AgentTrait {

	/**
	 * Construct a behavior for the given agent.
	 *
	 * @param agent the owner of this behavior.
	 */
	public Behavior(Agent agent) {
		super(agent);
	}

	@Override
	protected <S extends Capacity> S $castSkill(Class<S> capacity, ClearableReference<Skill> skillReference) {
		final Skill original = skillReference.get();
		if (original == null) {
			throw new UnimplementedCapacityException(capacity, getOwner().getID());
		}
		return Capacities.createSkillDelegatorIfPossible(original, capacity, this);
	}

}
