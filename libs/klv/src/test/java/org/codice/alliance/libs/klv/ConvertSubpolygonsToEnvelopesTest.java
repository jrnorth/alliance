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
package org.codice.alliance.libs.klv;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public class ConvertSubpolygonsToEnvelopesTest {

  private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

  private final ConvertSubpolygonsToEnvelopes convertSubpolygonsToEnvelopes =
      new ConvertSubpolygonsToEnvelopes();

  @Test
  public void testNullSubpolygon() {

    Geometry geometry = null;

    Geometry actual = convertSubpolygonsToEnvelopes.apply(geometry, new GeometryOperator.Context());

    assertThat(actual, nullValue());
  }

  @Test
  public void testEmptySubpolygon() {

    Geometry geometry = GEOMETRY_FACTORY.createMultiPolygon(null);

    Geometry actual = convertSubpolygonsToEnvelopes.apply(geometry, new GeometryOperator.Context());

    assertThat(actual.isEmpty(), is(true));
  }

  @Test
  public void testSingleSubpolygon() throws ParseException {

    String wkt = "POLYGON ((0 0, 0 10, 10 10, 10 0, 0 0))";

    WKTReader wktReader = new WKTReader();

    Geometry geometry = wktReader.read(wkt);

    Geometry actual = convertSubpolygonsToEnvelopes.apply(geometry, new GeometryOperator.Context());

    assertThat(actual, is(geometry));
  }

  @Test
  public void testTwoSubpolygons() throws ParseException {

    String wkt =
        "MULTIPOLYGON (((0 0, 2 10, 10 20, 20 20, 20 0, 0 0)),((0 40, 2 50, 10 60, 20 60, 20 40, 0 40)))";

    WKTReader wktReader = new WKTReader();

    Geometry geometry = wktReader.read(wkt);

    Geometry actual = convertSubpolygonsToEnvelopes.apply(geometry, new GeometryOperator.Context());

    Geometry expected =
        wktReader.read(
            "MULTIPOLYGON (((0 0, 0 20, 20 20, 20 0, 0 0)), ((0 40, 0 60, 20 60, 20 40, 0 40)))");

    assertThat(actual, is(expected));
  }

  // This test was adapted from a real video stream that failed the `ConvertSubpolygonsToEnvelopes`
  // step. It failed because the geometry was a GeometryCollection of different types of geometries
  // and the union code couldn't handle unioning GeometryCollections.
  @Test
  public void testGeometryCollection() throws ParseException {
    final String wkt =
        "GEOMETRYCOLLECTION (POINT (0 0), LINESTRING (10 10, 20 20), LINESTRING (20 20, 10 30))";

    final WKTReader wktReader = new WKTReader();

    final Geometry geometry = wktReader.read(wkt);

    final Geometry actual =
        convertSubpolygonsToEnvelopes.apply(geometry, new GeometryOperator.Context());

    final Geometry expected =
        wktReader.read(
            "GEOMETRYCOLLECTION( POINT (0 0), POLYGON ((10 10, 10 20, 10 30, 20 30, 20 20, 20 10, 10 10)))");

    assertThat(actual, is(expected));
  }
}
