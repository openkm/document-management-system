package com.openkm.servlet.frontend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

/**
 * Abstract comparator of type T. Hashmap {@link comparators} holds all available comparators keyed
 * by name of comparator's class. It uses collator {@link collator} for comparing objects.
 *
 * @param <T> type to be compared
 * @author Franta
 */
public abstract class CultureComparator<T> implements Comparator<T> {

	private static Logger log = LoggerFactory.getLogger(CultureComparator.class);
	protected static final String DEFAULT_LOCALE = "en";

	// Collator used for comparing values according to the locale
	protected Collator collator;

	// all available comparators. First key is the name of comparator's class, second is the locale
	// e.g. en-GB, cs-CZ, etc.
	private static HashMap<String, HashMap<String, CultureComparator<?>>> comparators = new HashMap<String, HashMap<String, CultureComparator<?>>>();

	// objet for synchronized access
	private static final Object SYNC_LOCK = new Object();

	/**
	 * Protected construcotr that creates collator according to the locale.
	 *
	 * @param sLocale locale
	 */
	protected CultureComparator(String sLocale) {
		try {
			if (sLocale == null) {
				log.debug("no locale defined, using default collator");
				collator = Collator.getInstance();
				return;
			}
			String[] split = sLocale.split("-");
			Locale locale = split.length > 1 ? new Locale(split[0], split[1]) : new Locale(split[0]);

			collator = Collator.getInstance(locale);
			log.debug("created new collator (locale={}, lang={}, country={}", new Object[]{sLocale, locale.getLanguage(), locale.getCountry()});
		} catch (Exception e) {
			log.warn(String.format("Unable to create collator for %1$s, creating default", sLocale), e);
			collator = Collator.getInstance();
		}
	}

	/**
	 * Comparing method.
	 */
	public abstract int compare(T arg0, T arg1);

	/**
	 * Return instance of comparator of class className for locale locale. If no instance is present
	 * in hashmap, creates a new one.
	 *
	 * @param className name of comparator's class
	 * @param locale    locale
	 * @param hashMap   hashmap of comparator keyd by locales
	 * @return CultureComparator
	 * @throws Exception
	 */
	private static CultureComparator<?> getComparator(String className, String locale, HashMap<String, CultureComparator<?>> hashMap) throws Exception {
		// get required comparator
		CultureComparator<?> comparator = hashMap.get(className);
		if (comparator != null) {
			return comparator;
		}

		// comparator probably does not exist, create a new one
		synchronized (SYNC_LOCK) {
			if (!hashMap.containsKey(className)) {
				Class<?> clazz = Class.forName(className);
				comparator = (CultureComparator<?>) clazz.getDeclaredConstructor(String.class).newInstance(locale);
				hashMap.put(className, comparator);
			} else {
				comparator = hashMap.get(className);
			}

			return comparator;
		}
	}

	/**
	 * Gets instance of CultureComparator stored in hashmap {@link comparators} by comparator's class
	 * and locale. If no instance is present in hashmap, a new instance is created.
	 *
	 * @param clazz  comparator's class
	 * @param locale locale
	 * @return CultureComparator
	 * @throws Exception
	 */
	protected static CultureComparator<?> getInstance(Class<?> clazz, String locale) throws Exception {
		CultureComparator<?> comparator;
		String className = clazz.getName();

		// get hashmap of comparators
		HashMap<String, CultureComparator<?>> hashMap = comparators.get(locale);
		if (hashMap != null) {
			// get instance of comparator
			comparator = getComparator(className, locale, hashMap);
			return comparator;
		}

		// probably no hasmap defined for class name, create a new one
		synchronized (SYNC_LOCK) {
			if (!comparators.containsKey(locale)) {
				hashMap = new HashMap<String, CultureComparator<?>>();
				comparator = (CultureComparator<?>) clazz.getDeclaredConstructor(String.class).newInstance(locale);
				hashMap.put(className, comparator);
				comparators.put(locale, hashMap);
			} else {
				hashMap = comparators.get(locale);
				comparator = getComparator(className, locale, hashMap);
			}
		}

		return comparator;
	}
}
