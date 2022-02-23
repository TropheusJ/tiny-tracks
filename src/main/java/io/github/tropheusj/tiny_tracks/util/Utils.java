package io.github.tropheusj.tiny_tracks.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Utils {
	@SafeVarargs
	public static <T> Set<T> toMutableSet(T... things) {
		Set<T> set = new HashSet<>();
		Collections.addAll(set, things);
		return set;
	}
}
