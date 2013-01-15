/*
Copyright 2008-2010 Gephi
Authors : Cezary Bartosiak
Website : http://www.gephi.org

This file is part of Gephi.

DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 2011 Gephi Consortium. All rights reserved.

The contents of this file are subject to the terms of either the GNU
General Public License Version 3 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://gephi.org/about/legal/license-notice/
or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License files at
/cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 3, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 3] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 3 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 3 code and therefore, elected the GPL
Version 3 license, then the option applies only if the new code is
made subject to such option by the copyright holder.

Contributor(s):

Portions Copyrighted 2011 Gephi Consortium.
*/
package org.gephi.data.attributes.type;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Hashtable;
import java.util.List;
import org.gephi.data.attributes.api.Estimator;

/**
 * Represents {@link Double} type which can have got different values in
 * different time intervals.
 * 
 * @author Cezary Bartosiak
 */
public final class DynamicDouble extends DynamicType<Double> {
	/**
	 * Constructs a new {@code DynamicType} instance with no intervals.
	 */
	public DynamicDouble() {
		super();
	}

	/**
	 * Constructs a new {@code DynamicType} instance that contains a given
	 * {@code Interval<T>} in.
	 *
	 * @param in interval to add (could be null)
	 */
	public DynamicDouble(Interval<Double> in) {
		super(in);
	}

	/**
	 * Constructs a new {@code DynamicType} instance with intervals given by
	 * {@code List<Interval<T>>} in.
	 *
	 * @param in intervals to add (could be null)
	 */
	public DynamicDouble(List<Interval<Double>> in) {
		super(in);
	}

	/**
	 * Constructs a deep copy of {@code source}.
	 *
	 * @param source an object to copy from (could be null, then completely new
	 *               instance is created)
	 */
	public DynamicDouble(DynamicDouble source) {
		super(source);
	}

	/**
	 * Constructs a deep copy of {@code source} that contains a given
	 * {@code Interval<T>} in.
	 *
	 * @param source an object to copy from (could be null, then completely new
	 *               instance is created)
	 * @param in     interval to add (could be null)
	 */
	public DynamicDouble(DynamicDouble source, Interval<Double> in) {
		super(source, in);
	}

	/**
	 * Constructs a deep copy of {@code source} that contains a given
	 * {@code Interval<T>} in. Before add it removes from the newly created
	 * object all intervals that overlap with a given {@code Interval<T>} out.
	 *
	 * @param source an object to copy from (could be null, then completely new
	 *               instance is created)
	 * @param in     interval to add (could be null)
	 * @param out    interval to remove (could be null)
	 */
	public DynamicDouble(DynamicDouble source, Interval<Double> in, Interval<Double> out) {
		super(source, in, out);
	}

	/**
	 * Constructs a deep copy of {@code source} with additional intervals
	 * given by {@code List<Interval<T>>} in.
	 *
	 * @param source an object to copy from (could be null, then completely new
	 *               instance is created)
	 * @param in     intervals to add (could be null)
	 */
	public DynamicDouble(DynamicDouble source, List<Interval<Double>> in) {
		super(source, in);
	}

	/**
	 * Constructs a deep copy of {@code source} with additional intervals
	 * given by {@code List<Interval<T>>} in. Before add it removes from the
	 * newly created object all intervals that overlap with intervals given by
	 * {@code List<Interval<T>>} out.
	 *
	 * @param source an object to copy from (could be null, then completely new
	 *               instance is created)
	 * @param in     intervals to add (could be null)
	 * @param out    intervals to remove (could be null)
	 */
	public DynamicDouble(DynamicDouble source, List<Interval<Double>> in, List<Interval<Double>> out) {
		super(source, in, out);
	}

	@Override
	public Double getValue(Interval interval, Estimator estimator) {
		List<Double> values = getValues(interval);
		if (values.isEmpty())
			return null;

		switch (estimator) {
			case AVERAGE:
				if (values.size() == 1)
					return values.get(0);
				BigDecimal total = new BigDecimal(0);
				for (int i = 0; i < values.size(); ++i)
					total = total.add(BigDecimal.valueOf(values.get(i)));
				return total.divide(BigDecimal.valueOf((long)values.size()), 10, RoundingMode.HALF_EVEN).doubleValue();
			case MEDIAN:
				if (values.size() % 2 == 1)
					return values.get(values.size() / 2);
				BigDecimal bd = new BigDecimal(
						values.get(values.size() / 2 - 1));
				bd = bd.add(new BigDecimal(values.get(values.size() / 2)));
				return bd.divide(new BigDecimal(2)).doubleValue();
			case MODE:
				Hashtable<Integer, Integer> map =
						new Hashtable<Integer, Integer>();
				for (int i = 0; i < values.size(); ++i) {
					int prev = 0;
					if (map.containsKey(values.get(i).hashCode()))
						prev = map.get(values.get(i).hashCode());
					map.put(values.get(i).hashCode(), prev + 1);
				}
				int max   = map.get(values.get(0).hashCode());
				int index = 0;
				for (int i = 1; i < values.size(); ++i)
					if (max < map.get(values.get(i).hashCode())) {
						max   = map.get(values.get(i).hashCode());
						index = i;
					}
				return values.get(index);
			case SUM:
				BigDecimal sum = new BigDecimal(0);
				for (int i = 0; i < values.size(); ++i)
					sum = sum.add(new BigDecimal(values.get(i)));
				return sum.doubleValue();
			case MIN:
				BigDecimal minimum = new BigDecimal(values.get(0));
				for (int i = 1; i < values.size(); ++i)
					if (minimum.compareTo(new BigDecimal(values.get(i))) > 0)
						minimum = new BigDecimal(values.get(i));
				return minimum.doubleValue();
			case MAX:
				BigDecimal maximum = new BigDecimal(values.get(0));
				for (int i = 1; i < values.size(); ++i)
					if (maximum.compareTo(new BigDecimal(values.get(i))) < 0)
						maximum = new BigDecimal(values.get(i));
				return maximum.doubleValue();
			case FIRST:
				return values.get(0);
			case LAST:
				return values.get(values.size() - 1);
			default:
				throw new IllegalArgumentException("Unknown estimator.");
		}
	}

	@Override
	public Class getUnderlyingType() {
		return Double.class;
	}
}
