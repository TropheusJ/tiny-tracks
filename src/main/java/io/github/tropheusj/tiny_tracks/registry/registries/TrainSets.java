package io.github.tropheusj.tiny_tracks.registry.registries;

import io.github.tropheusj.tiny_tracks.TinyTracks;
import io.github.tropheusj.tiny_tracks.train.TrainSet;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

import static io.github.tropheusj.tiny_tracks.TinyTracks.id;
import static io.github.tropheusj.tiny_tracks.registry.registries.CarriageTypes.*;
import static io.github.tropheusj.tiny_tracks.registry.registries.TrackTypes.*;

/**
 * A registry of Train Sets. Registered last, after CarriageTypes and TrackTypes.
 * @see TrainSet
 * @see CarriageTypes
 * @see TrackTypes
 */
public enum TrainSets {;
	private static final Map<ResourceLocation, TrainSet> SETS = new HashMap<>();
	private static final Map<TrainSet, ResourceLocation> LOCATIONS = new HashMap<>();

	public static TrainSet register(ResourceLocation location, TrainSet set) {
		TrainSet old = SETS.put(location, set);
		ResourceLocation oldLoc = LOCATIONS.put(set, location);
		if (old != null || oldLoc != null) {
			throw new RuntimeException("Duplicate TrainSet registration!");
		}
		return set;
	}

	public static TrainSet get(ResourceLocation location) {
		TrainSet set = SETS.get(location);
		if (set == null) {
			TinyTracks.LOGGER.error("Unknown TrainSet: " + location);
		}
		return set;
	}

	public static ResourceLocation get(TrainSet set) {
		ResourceLocation id = LOCATIONS.get(set);
		if (id == null) {
			TinyTracks.LOGGER.error("Unknown TrainSet: " + set);
		}
		return id;
	}

	public static final TrainSet STEAM = register(id("steam"), new TrainSet(RAIL, all()));
	public static final TrainSet FREIGHT = register(id("freight"), new TrainSet(STEAM));
	public static final TrainSet MONORAIL = register(id("monorail"), new TrainSet(TrackTypes.MONORAIL, PASSENGER, BOX_CAR, TANKER));
	public static final TrainSet HANGING = register(id("hanging"), new TrainSet(TrackTypes.HANGING, STEAM.validCarriages()));
	public static final TrainSet BULLET = register(id("bullet"), new TrainSet(RAIL, MONORAIL.validCarriages()));
	public static final TrainSet DEBUG = register(id("debug"), new TrainSet(RAIL, CarriageTypes.DEBUG));

	public static void init() {
	}
}
