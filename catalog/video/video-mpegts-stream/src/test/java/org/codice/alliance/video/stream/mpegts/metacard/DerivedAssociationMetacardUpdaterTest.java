/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.alliance.video.stream.mpegts.metacard;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import ddf.catalog.data.Attribute;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.types.Associations;
import java.util.Arrays;
import java.util.UUID;
import org.junit.Test;

public class DerivedAssociationMetacardUpdaterTest {

  private final DerivedAssociationMetacardUpdater updater = new DerivedAssociationMetacardUpdater();

  @Test
  public void firstChild() {
    final Metacard parent = new MetacardImpl();
    final MetacardImpl child = new MetacardImpl();
    child.setId(UUID.randomUUID().toString());

    updater.update(parent, child, null);

    final Attribute derivedAssociation = parent.getAttribute(Associations.DERIVED);
    assertThat(derivedAssociation, is(notNullValue()));
    assertThat(derivedAssociation.getValues(), contains(child.getId()));
  }

  @Test
  public void multipleChildren() {
    final Metacard parent = new MetacardImpl();
    parent.setAttribute(new AttributeImpl(Associations.DERIVED, Arrays.asList("first", "second")));
    final MetacardImpl child = new MetacardImpl();
    child.setId("third");

    updater.update(parent, child, null);

    final Attribute derivedAssociation = parent.getAttribute(Associations.DERIVED);
    assertThat(derivedAssociation, is(notNullValue()));
    assertThat(derivedAssociation.getValues(), containsInAnyOrder("first", "second", "third"));
  }
}
