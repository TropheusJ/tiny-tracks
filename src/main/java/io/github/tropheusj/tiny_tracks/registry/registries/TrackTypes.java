package io.github.tropheusj.tiny_tracks.registry.registries;

import io.github.tropheusj.tiny_tracks.track.TrackType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

import static io.github.tropheusj.tiny_tracks.TinyTracks.id;

/**
 * A registry of Track Types. Registered first, before CarriageTypes and TrainSets.
 * @see TrackType
 * @see CarriageTypes
 * @see TrainSets
 */
public enum TrackTypes {;
	private static final Map<ResourceLocation, TrackType> TYPES = new HashMap<>();

	public static TrackType register(ResourceLocation location, TrackType type) {
		TrackType old = TYPES.put(location, type);
		if (old != null) {
			throw new RuntimeException("Duplicate TrackType registration!");
		}
		return type;
	}

	public static TrackType get(ResourceLocation location) {
		TrackType type = TYPES.get(location);
		if (type == null) {
			throw new RuntimeException("Unknown TrackType: " + location);
		}
		return type;
	}

	public static final TrackType RAIL = register(id("rail"), new TrackType());
	public static final TrackType MONORAIL = register(id("monorail"), new TrackType());
	public static final TrackType HANGING = register(id("hanging"), new TrackType());

	public static void init() {
	}
}
