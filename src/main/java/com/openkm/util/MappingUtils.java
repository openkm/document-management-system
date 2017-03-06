/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017  Paco Avila & Josep Llort
 * <p>
 * No bytes were intentionally harmed during the development of this application.
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.util;

import org.dozer.DozerBeanMapperSingletonWrapper;
import org.dozer.Mapper;

import java.util.*;
import java.util.Map.Entry;

public class MappingUtils {
	private static final Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();

	public static Mapper getMapper() {
		return mapper;
	}

	/**
	 * Initialize collection
	 */
	public static <E> List<E> map(List<E> input) {
		List<E> ret = new ArrayList<E>();

		for (E tmp : input) {
			ret.add(tmp);
		}

		return ret;
	}

	/**
	 * Initialize set
	 */
	public static <E> Set<E> map(Set<E> input) {
		Set<E> ret = new HashSet<E>();

		for (E tmp : input) {
			ret.add(tmp);
		}

		return ret;
	}

	/**
	 * Initialize map
	 */
	public static <K, V> Map<K, V> map(Map<K, V> input) {
		Map<K, V> ret = new HashMap<K, V>();

		for (Entry<K, V> tmp : input.entrySet()) {
			ret.put(tmp.getKey(), tmp.getValue());
		}

		return ret;
	}
}
