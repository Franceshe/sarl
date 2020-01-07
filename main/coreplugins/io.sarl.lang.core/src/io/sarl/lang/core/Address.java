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

import java.io.Serializable;
import java.util.UUID;

import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * This class describes all the addresses used by the kernel to identify its
 * elements.
 *
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Address extends SRESpecificDataContainer implements Serializable, Comparable<Address> {

	private static final long serialVersionUID = 1519844913685586094L;

	/**
	 * The global unique identifier of this kernel.
	 */
	private final UUID agentId;

	private final SpaceID spaceId;

	/**
	 * Create an address based on the specified identifier.
	 *
	 * @param spaceId the identifier of the space related to this address.
	 * @param agentId is the identifier of the address.
	 */
	public Address(SpaceID spaceId, UUID agentId) {
		assert agentId != null;
		assert spaceId != null;
		this.agentId = agentId;
		this.spaceId = spaceId;
	}

	@Override
	@Pure
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);
		builder.add("type", getClass().getSimpleName()); //$NON-NLS-1$
		builder.add("agentId", this.agentId); //$NON-NLS-1$
		builder.add("spaceId", this.spaceId); //$NON-NLS-1$
		return builder.toString();
	}

	/**
	 * Replies the identifier associated to this address.
	 *
	 * @return the identifier associated to this address.
	 */
	@Pure
	public UUID getUUID() {
		return this.agentId;
	}

	@Override
	@Pure
	public int hashCode() {
		int result = 1;
		result = 31 * result + ((this.agentId == null) ? 0 : this.agentId.hashCode());
		result = 31 * result + ((this.spaceId == null) ? 0 : this.spaceId.hashCode());
		return result;
	}

	@Override
	@Pure
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (obj instanceof Address) {
			return equals((Address) obj);
		}
		return false;
	}

	/**
	 * Test if this address and the given one are equal.
	 *
	 * @param address is the address to be compared.
	 * @return <code>true</code> if this address and the given one are equal,
	 *         otherwise <code>false</code>
	 */
	@Pure
	public boolean equals(Address address) {
		return address != null && this.agentId.equals(address.getUUID())
				&& this.spaceId.equals(address.getSpaceID());
	}

	/**
	 * Compares this object with the specified object for order. Returns a
	 * negative integer, zero, or a positive integer as this object is less
	 * than, equal to, or greater than the specified object.
	 *
	 * <p>The implementor must ensure <code>sgn(x.compareTo(y)) ==
	 * -sgn(y.compareTo(x))</code> for all <code>x</code> and <code>y</code>. (This
	 * implies that <code>x.compareTo(y)</code> must throw an exception iff
	 * <code>y.compareTo(x)</code> throws an exception.)
	 *
	 * <p>The implementor must also ensure that the relation is transitive:
	 * <code>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</code> implies
	 * <code>x.compareTo(z)&gt;0</code>.
	 *
	 * <p>Finally, the implementor must ensure that <code>x.compareTo(y)==0</code>
	 * implies that <code>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</code>, for all
	 * <code>z</code>.
	 *
	 * <p>It is strongly recommended, but <i>not</i> strictly required that
	 * <code>(x.compareTo(y)==0) == (x.equals(y))</code>. Generally speaking, any
	 * class that implements the <code>Comparable</code> interface and violates this
	 * condition should clearly indicate this fact. The recommended language is
	 * "Note: this class has a natural ordering that is inconsistent with
	 * equals."
	 *
	 * <p>In the foregoing description, the notation <code>sgn(</code><i>expression</i>
	 * <code>)</code> designates the mathematical <i>signum</i> function, which is
	 * defined to return one of <code>-1</code>, <code>0</code>, or <code>1</code> according
	 * to whether the value of <i>expression</i> is negative, zero or positive.
	 *
	 * @param address is the address to be compared.
	 * @return a negative integer, zero, or a positive integer as this object is
	 *         less than, equal to, or greater than the specified object.
	 */
	@Override
	@Pure
	public int compareTo(Address address) {
		if (address == null) {
			return 1;
		}
		return this.agentId.compareTo(address.getUUID());
	}

	/** Replies the ID of the space related to this address.
	 *
	 * @return the space ID.
	 * @since 0.7
	 */
	@Pure
	public SpaceID getSpaceID() {
		return this.spaceId;
	}

	/** Replies the ID of the space related to this address.
	 *
	 * @return the space ID.
	 * @deprecated since 0.7, see {@link #getSpaceID()} for replacement.
	 */
	@Pure
	@Deprecated(since = "0.7", forRemoval = true)
	public SpaceID getSpaceId() {
		return getSpaceID();
	}

}
