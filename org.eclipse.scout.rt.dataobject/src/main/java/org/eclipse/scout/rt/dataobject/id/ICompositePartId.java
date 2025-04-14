/*
 * Copyright (c) 2010, 2025 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.rt.dataobject.id;

/**
 * Marker interface for simple parts of an {@link ICompositeId} which are not used separately in API or persistence outside the
 * composite id and therefore do not need a separate class type for their own.
 * <p>
 * <b>Note:</b> Subclasses of {@link ICompositePartId} do not have a distinct {@link IdTypeName} and may therefore not be addressed in data object migrations.
 *
 * @see ICompositeId
 */
@IdSignature(false)
public interface ICompositePartId extends IRootId {
}
