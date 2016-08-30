/*
 * $Id$
 *
 * SARL is an general-purpose agent programming language.
 * More details on http://www.sarl.io
 *
 * Copyright (C) 2014-2016 the original authors or authors.
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

import java.lang.ref.WeakReference;

import org.eclipse.xtext.xbase.lib.Inline;
import org.eclipse.xtext.xbase.lib.Pure;

/** This class represents a part of trait of an agent.
 *
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
abstract class AgentTrait {

	private WeakReference<Agent> agentRef;

	/** Construct a trait to the given agent.
	 *
	 * @param agent - the owner of this trait.
	 */
	AgentTrait(Agent agent) {
		this.agentRef = new WeakReference<>(agent);
	}

	/** Construct a trait.
	 */
	AgentTrait() {
		this.agentRef = new WeakReference<>(null);
	}

	/**
	 * Returns a String representation of the Event E1 attributes only.
	 *
	 * @return the string representation of the attributes of this Event.
	 */
	@Pure
	protected String attributesToString() {
		final StringBuilder result = new StringBuilder();
		result.append("owner = "); //$NON-NLS-1$
		result.append(getOwner());
		return result.toString();
	}

	@Override
	@Pure
	public String toString() {
		return getClass().getSimpleName()
				+ " [" + attributesToString() //$NON-NLS-1$
				+ "]"; //$NON-NLS-1$
	}

	/** Set the agent that has this trait.
	 *
	 * @param agent - the owner of this trait.
	 */
	void setOwner(Agent agent) {
		this.agentRef = new WeakReference<>(agent);
	}

	/** Replies the agent that has this trait.
	 *
	 * @return the owner.
	 */
	@Pure
	protected Agent getOwner() {
		return this.agentRef.get();
	}

	/** Replies the skill corresponding to the given capacity.
	 *
	 * <p>The return may never be <code>null</code>. If not capacity
	 * was set, the exception {@link UnimplementedCapacityException}
	 * is thrown.
	 *
	 * @param <S> - type of the capacity.
	 * @param capacity - the capacity to search for the implementation.
	 * @return the skill, never <code>null</code>
	 * @see Agent#getSkill(Class)
	 * @throws UnimplementedCapacityException - if no skill is owned by the agent for the given capacity.
	 */
	@Pure
	protected <S extends Capacity> S getSkill(Class<S> capacity) {
		return getOwner().getSkill(capacity);
	}

	/** Defines the implementation of the "capacity maps-to skill" operator.
	 *
	 * @param <S> - type of skill to be mapped to.
	 * @param capacity - the implemented capacity.
	 * @param skill - the skill to associate to the capacity.
	 */
	@Inline("$setSkill($2, $1)")
	protected <S extends Skill & Capacity> void operator_mappedTo(Class<? extends Capacity> capacity, S skill) {
		setSkill(skill, capacity);
	}

	/**
	 * Set the skill for the {@link Capacity} <code>capacity</code>.
	 *
	 * @param <S> - type of the skill.
	 * @param capacities the capacity or the capacities to set.
	 * @param skill implementaion of <code>capacity</code>.
	 * @return the skill that was set.
	 * @since 0.4
	 */
	@SafeVarargs
	@Inline("$setSkill($1, $2)")
	protected final <S extends Skill> S setSkill(S skill, Class<? extends Capacity>... capacities) {
		return $setSkill(skill, capacities);
	}

	/**
	 * Set the skill for the {@link Capacity} <code>capacity</code>.
	 *
	 * @param <S> - type of the skill.
	 * @param capacities the capacity or the capacities to set.
	 * @param skill implementaion of <code>capacity</code>.
	 * @return the skill that was set.
	 * @since 0.4
	 */
	@SuppressWarnings("unchecked")
    protected <S extends Skill> S $setSkill(S skill, Class<? extends Capacity>... capacities) {
		return getOwner().$setSkill(skill, capacities);
	}

	/**
	 * Clears the Skill associated with the capacity.
	 *
	 * @param <S> - the type of the capacity.
	 * @param capacity - the capacity for which the skill must be cleared.
	 * @return the skill that was removed
	 * @since 0.4
	 */
	protected <S extends Capacity> S clearSkill(Class<S> capacity) {
		return getOwner().clearSkill(capacity);
	}

}
