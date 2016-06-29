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

package io.sarl.lang.ui.labeling;

import java.util.Collections;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtend.core.jvmmodel.IXtendJvmAssociations;
import org.eclipse.xtend.ide.labeling.XtendLabelProvider;
import org.eclipse.xtext.common.types.JvmDeclaredType;
import org.eclipse.xtext.common.types.JvmExecutable;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.util.Exceptions;
import org.eclipse.xtext.util.PolymorphicDispatcher;
import org.eclipse.xtext.util.PolymorphicDispatcher.ErrorHandler;
import org.eclipse.xtext.xbase.scoping.featurecalls.OperatorMapping;
import org.eclipse.xtext.xbase.ui.labeling.XbaseImageAdornments;
import org.eclipse.xtext.xbase.validation.UIStrings;

import io.sarl.lang.sarl.SarlAction;
import io.sarl.lang.sarl.SarlAgent;
import io.sarl.lang.sarl.SarlBehavior;
import io.sarl.lang.sarl.SarlBehaviorUnit;
import io.sarl.lang.sarl.SarlCapacity;
import io.sarl.lang.sarl.SarlCapacityUses;
import io.sarl.lang.sarl.SarlConstructor;
import io.sarl.lang.sarl.SarlEvent;
import io.sarl.lang.sarl.SarlField;
import io.sarl.lang.sarl.SarlRequiredCapacity;
import io.sarl.lang.sarl.SarlScript;
import io.sarl.lang.sarl.SarlSkill;
import io.sarl.lang.ui.images.SARLImages;

/**
 * Provides labels for a EObjects.
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://www.eclipse.org/Xtext/documentation/304_ide_concepts.html#label-provider"
 */
public class SARLLabelProvider extends XtendLabelProvider {

	/** Max length of the text for the behavior units.
	 */
	public static final int BEHAVIOR_UNIT_TEXT_LENGTH = 7;

	@Inject
	private UIStrings uiStrings;

	@Inject
	private OperatorMapping operatorMapping;

	@Inject
	private IXtendJvmAssociations jvmModelAssociations;

	@Inject
	private SARLImages images;

	@Inject
	private XbaseImageAdornments adornments;

	private final PolymorphicDispatcher<ImageDescriptor> imageDescriptorDispatcher;

	private final ReentrantLock imageDescriptorLock = new ReentrantLock();

	/**
	 * @param delegate - the original provider.
	 */
	@Inject
	public SARLLabelProvider(AdapterFactoryLabelProvider delegate) {
		super(delegate);
		this.imageDescriptorDispatcher = new PolymorphicDispatcher<>(
				"imageDescriptor", //$NON-NLS-1$
				1, 1,
				Collections.singletonList(this),
				new ErrorHandler<ImageDescriptor>() {
					@Override
					public ImageDescriptor handle(Object[] params, Throwable exception) {
						return handleImageDescriptorError(params, exception);
					}
				});
	}

	/** Get the image descriptor for the given element.
	 *
	 * @param element - the element.
	 * @return the image descriptor.
	 */
	protected ImageDescriptor doGetImageDescriptor(Object element) {
		return this.imageDescriptorDispatcher.invoke(element);
	}

	/** Invoked when an image descriptor cannot be found.
	 *
	 * @param params - the parameters given to the method polymorphic dispatcher.
	 * @param exception - the cause of the error.
	 * @return the image descriptor for the error.
	 */
	protected ImageDescriptor handleImageDescriptorError(Object[] params, Throwable exception) {
		if (exception instanceof NullPointerException) {
			final Object defaultImage = getDefaultImage();
			if (defaultImage instanceof ImageDescriptor) {
				return (ImageDescriptor) defaultImage;
			}
			if (defaultImage instanceof Image) {
				return ImageDescriptor.createFromImage((Image) defaultImage);
			}
			return super.imageDescriptor(params[0]);
		}
		return Exceptions.throwUncheckedException(exception);
	}

	/** Create a string representation of a signature without the return type.
	 *
	 * @param simpleName - the action name.
	 * @param element - the executable element.
	 * @return the signature.
	 */
	protected StyledString signatureWithoutReturnType(StyledString simpleName, JvmExecutable element) {
		return simpleName.append(this.uiStrings.parameters(element));
	}

	/** Create a string representation of the given element.
	 *
	 * @param reference - the element.
	 * @return the string representation.
	 */
	protected StyledString getHumanReadableName(JvmTypeReference reference) {
		if (reference == null) {
			return new StyledString("Object"); //$NON-NLS-1$
		}
		String name = this.uiStrings.referenceToString(reference, "Object"); //$NON-NLS-1$
		//
		// FIXME: https://bugs.eclipse.org/bugs/show_bug.cgi?id=443131
		final JvmType type = reference.getType();
		if (type != null && type.eIsProxy() && reference.eResource() != null) {
			// This case occurs when the reference is unknown:
			// the found "name" is the fully qualified name of the type.
			// So we should extract the simple name
			int index = name.length() - 1;
			final char dot = '.';
			final char doll = '$';
			final char dies = '#';
			char ch;
			while (index >= 0) {
				ch = name.charAt(index);
				if (ch == dot || ch == doll || ch == dies) {
					name = name.substring(index + 1);
					// break the loop
					index = -1;
				} else {
					index--;
				}
			}
		}
		// END OF FIX
		//
		return convertToStyledString(name);
	}

	// Descriptors

	@Override
	protected ImageDescriptor imageDescriptor(Object element) {
		if (this.imageDescriptorLock.isLocked()) {
			return super.imageDescriptor(element);
		}
		this.imageDescriptorLock.lock();
		try {
			return doGetImageDescriptor(element);
		} finally {
			this.imageDescriptorLock.unlock();
		}
	}

	/** Replies the image for the given element.
	 *
	 * <p>This function is a Xtext dispatch function for {@link #imageDescriptor(Object)}.
	 *
	 * @param element - the element.
	 * @return the image descriptor.
	 * @see #imageDescriptor(Object)
	 */
	protected ImageDescriptor imageDescriptor(Package element) {
		// Mostly used by the outline
		return this.images.forPackage();
	}

	/** Replies the image for the given element.
	 *
	 * <p>This function is a Xtext dispatch function for {@link #imageDescriptor(Object)}.
	 *
	 * @param element - the element.
	 * @return the image descriptor.
	 * @see #imageDescriptor(Object)
	 */
	protected ImageDescriptor imageDescriptor(SarlScript element) {
		return this.images.forFile();
	}

	/** Replies the image for the given element.
	 *
	 * <p>This function is a Xtext dispatch function for {@link #imageDescriptor(Object)}.
	 *
	 * @param element - the element.
	 * @return the image descriptor.
	 * @see #imageDescriptor(Object)
	 */
	protected ImageDescriptor imageDescriptor(SarlAgent element) {
		final JvmDeclaredType jvmElement = this.jvmModelAssociations.getInferredType(element);
		return this.images.forAgent(
				element.getVisibility(),
				this.adornments.get(jvmElement));
	}

	/** Replies the image for the given element.
	 *
	 * <p>This function is a Xtext dispatch function for {@link #imageDescriptor(Object)}.
	 *
	 * @param element - the element.
	 * @return the image descriptor.
	 * @see #imageDescriptor(Object)
	 */
	protected ImageDescriptor imageDescriptor(SarlEvent element) {
		final JvmDeclaredType jvmElement = this.jvmModelAssociations.getInferredType(element);
		return this.images.forEvent(
				element.getVisibility(),
				this.adornments.get(jvmElement));
	}

	/** Replies the image for the given element.
	 *
	 * <p>This function is a Xtext dispatch function for {@link #imageDescriptor(Object)}.
	 *
	 * @param element - the element.
	 * @return the image descriptor.
	 * @see #imageDescriptor(Object)
	 */
	protected ImageDescriptor imageDescriptor(SarlCapacity element) {
		final JvmDeclaredType jvmElement = this.jvmModelAssociations.getInferredType(element);
		return this.images.forCapacity(
				element.getVisibility(),
				this.adornments.get(jvmElement));
	}

	/** Replies the image for the given element.
	 *
	 * <p>This function is a Xtext dispatch function for {@link #imageDescriptor(Object)}.
	 *
	 * @param element - the element.
	 * @return the image descriptor.
	 * @see #imageDescriptor(Object)
	 */
	protected ImageDescriptor imageDescriptor(SarlSkill element) {
		final JvmDeclaredType jvmElement = this.jvmModelAssociations.getInferredType(element);
		return this.images.forSkill(
				element.getVisibility(),
				this.adornments.get(jvmElement));
	}

	/** Replies the image for the given element.
	 *
	 * <p>This function is a Xtext dispatch function for {@link #imageDescriptor(Object)}.
	 *
	 * @param element - the element.
	 * @return the image descriptor.
	 * @see #imageDescriptor(Object)
	 */
	protected ImageDescriptor imageDescriptor(SarlBehavior element) {
		final JvmDeclaredType jvmElement = this.jvmModelAssociations.getInferredType(element);
		return this.images.forBehavior(
				element.getVisibility(),
				this.adornments.get(jvmElement));
	}

	/** Replies the image for the given element.
	 *
	 * <p>This function is a Xtext dispatch function for {@link #imageDescriptor(Object)}.
	 *
	 * @param element - the element.
	 * @return the image descriptor.
	 * @see #imageDescriptor(Object)
	 */
	protected ImageDescriptor imageDescriptor(SarlField element) {
		return this.images.forField(
				element.getVisibility(),
				this.adornments.get(this.jvmModelAssociations.getJvmField(element)));
	}

	/** Replies the image for the given element.
	 *
	 * <p>This function is a Xtext dispatch function for {@link #imageDescriptor(Object)}.
	 *
	 * @param element - the element.
	 * @return the image descriptor.
	 * @see #imageDescriptor(Object)
	 */
	protected ImageDescriptor imageDescriptor(SarlConstructor element) {
		return this.images.forConstructor(
				element.getVisibility(),
				this.adornments.get(this.jvmModelAssociations.getInferredConstructor(element)));
	}

	/** Replies the image for the given element.
	 *
	 * <p>This function is a Xtext dispatch function for {@link #imageDescriptor(Object)}.
	 *
	 * @param element - the element.
	 * @return the image descriptor.
	 * @see #imageDescriptor(Object)
	 */
	protected ImageDescriptor imageDescriptor(SarlAction element) {
		final JvmOperation jvmElement = this.jvmModelAssociations.getDirectlyInferredOperation(element);
		return this.images.forOperation(
				element.getVisibility(),
				this.adornments.get(jvmElement));
	}

	/** Replies the image for the given element.
	 *
	 * <p>This function is a Xtext dispatch function for {@link #imageDescriptor(Object)}.
	 *
	 * @param element - the element.
	 * @return the image descriptor.
	 * @see #imageDescriptor(Object)
	 */
	protected ImageDescriptor imageDescriptor(SarlCapacityUses element) {
		return this.images.forCapacityUses();
	}

	/** Replies the image for the given element.
	 *
	 * <p>This function is a Xtext dispatch function for {@link #imageDescriptor(Object)}.
	 *
	 * @param element - the element.
	 * @return the image descriptor.
	 * @see #imageDescriptor(Object)
	 */
	protected ImageDescriptor imageDescriptor(SarlRequiredCapacity element) {
		return this.images.forCapacityRequirements();
	}

	/** Replies the image for the given element.
	 *
	 * <p>This function is a Xtext dispatch function for {@link #imageDescriptor(Object)}.
	 *
	 * @param element - the element.
	 * @return the image descriptor.
	 * @see #imageDescriptor(Object)
	 */
	protected ImageDescriptor imageDescriptor(SarlBehaviorUnit element) {
		return this.images.forBehaviorUnit();
	}

	// Texts

	/** Replies the text for the given element.
	 *
	 * @param element - the element.
	 * @return the text.
	 */
	protected StyledString text(JvmTypeReference element) {
		return getHumanReadableName(element);
	}

	/** Replies the text for the given element.
	 *
	 * @param element - the element.
	 * @return the text.
	 */
	protected StyledString text(SarlAgent element) {
		return convertToStyledString(element.getName());
	}

	/** Replies the text for the given element.
	 *
	 * @param element - the element.
	 * @return the text.
	 */
	protected StyledString text(SarlEvent element) {
		return convertToStyledString(element.getName());
	}

	/** Replies the text for the given element.
	 *
	 * @param element - the element.
	 * @return the text.
	 */
	protected StyledString text(SarlCapacity element) {
		return convertToStyledString(element.getName());
	}

	/** Replies the text for the given element.
	 *
	 * @param element - the element.
	 * @return the text.
	 */
	protected StyledString text(SarlSkill element) {
		return convertToStyledString(element.getName());
	}

	/** Replies the text for the given element.
	 *
	 * @param element - the element.
	 * @return the text.
	 */
	protected StyledString text(SarlBehavior element) {
		return convertToStyledString(element.getName());
	}

	/** Replies the text for the given element.
	 *
	 * @param element - the element.
	 * @return the text.
	 */
	protected StyledString text(SarlAction element) {
		final JvmIdentifiableElement jvmElement = this.jvmModelAssociations.getDirectlyInferredOperation(element);
		final String simpleName = element.getName();
		if (simpleName != null) {
			final QualifiedName qnName = QualifiedName.create(simpleName);
			final QualifiedName operator = this.operatorMapping.getOperator(qnName);
			if (operator != null) {
				final StyledString result = signature(operator.getFirstSegment(), jvmElement);
				result.append(" (" + simpleName + ")", StyledString.COUNTER_STYLER); //$NON-NLS-1$//$NON-NLS-2$
				return result;
			}
		}
		return signature(element.getName(), jvmElement);
	}

	/** Replies the text for the given element.
	 *
	 * @param element - the element.
	 * @return the text.
	 */
	@SuppressWarnings("static-method")
	protected StyledString text(SarlCapacityUses element) {
		return new StyledString(Messages.SARLLabelProvider_0, StyledString.QUALIFIER_STYLER);
	}

	/** Replies the text for the given element.
	 *
	 * @param element - the element.
	 * @return the text.
	 */
	@SuppressWarnings("static-method")
	protected StyledString text(SarlRequiredCapacity element) {
		return new StyledString(Messages.SARLLabelProvider_1, StyledString.QUALIFIER_STYLER);
	}

	/** Replies the text for the given element.
	 *
	 * @param element - the element.
	 * @return the text.
	 */
	protected StyledString text(SarlBehaviorUnit element) {
		final StyledString text = new StyledString("on ", StyledString.DECORATIONS_STYLER); //$NON-NLS-1$
		text.append(getHumanReadableName(element.getName()));
		if (element.getGuard() != null) {
			String txt = null;
			final ICompositeNode node = NodeModelUtils.getNode(element.getGuard());
			if (node != null) {
				txt = node.getText().trim();
			}
			if (Strings.isNullOrEmpty(txt)) {
				txt = "[" + Messages.SARLLabelProvider_2 + "]"; //$NON-NLS-1$//$NON-NLS-2$
			} else {
				assert txt != null;
				final String dots = "..."; //$NON-NLS-1$
				if (txt.length() > BEHAVIOR_UNIT_TEXT_LENGTH + dots.length()) {
					txt = "[" + txt.substring(0, BEHAVIOR_UNIT_TEXT_LENGTH) + dots + "]"; //$NON-NLS-1$//$NON-NLS-2$
				} else {
					txt = "[" + txt + "]"; //$NON-NLS-1$//$NON-NLS-2$
				}
			}
			text.append(" "); //$NON-NLS-1$
			text.append(txt, StyledString.DECORATIONS_STYLER);
		}
		return text;
	}

}
