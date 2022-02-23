package io.github.tropheusj.tiny_tracks.registry.registries;

import io.github.tropheusj.tiny_tracks.TinyTracks;
import io.github.tropheusj.tiny_tracks.train.CarriageType;
import io.github.tropheusj.tiny_tracks.util.Utils;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static io.github.tropheusj.tiny_tracks.TinyTracks.id;

/**
 * A registry of Carriage Types. Registered before TrainSets, but after TrackTypes.
 * @see CarriageType
 * @see TrainSets
 * @see TrackTypes
 */
public enum CarriageTypes {;
	private static final Map<ResourceLocation, CarriageType> TYPES = new HashMap<>();
	private static final Map<CarriageType, ResourceLocation> LOCATIONS = new HashMap<>();

	public static CarriageType register(ResourceLocation location, CarriageType type) {
		CarriageType old = TYPES.put(location, type);
		ResourceLocation oldLoc = LOCATIONS.put(type, location);
		if (old != null || oldLoc != null) {
			throw new RuntimeException("Duplicate CarriageType registration!");
		}
		return type;
	}

	public static CarriageType get(ResourceLocation location) {
		CarriageType type = TYPES.get(location);
		if (type == null) {
			TinyTracks.LOGGER.error("Unknown CarriageType: " + location);
		}
		return type;
	}

	public static ResourceLocation get(CarriageType type) {
		ResourceLocation id = LOCATIONS.get(type);
		if (id == null) {
			TinyTracks.LOGGER.error("Unknown CarriageType: " + type);
		}
		return id;
	}

	public static Set<CarriageType> all() {
		return Utils.toMutableSet(TYPES.values().toArray(CarriageType[]::new));
	}

	public static final CarriageType ENGINE = register(id("engine"), new CarriageType());
	public static final CarriageType BOX_CAR = register(id("box_car"), new CarriageType());
	public static final CarriageType TANKER = register(id("tanker"), new CarriageType());
	public static final CarriageType FLATBED = register(id("flatbed"), new CarriageType());
	public static final CarriageType COAL_CAR = register(id("coal_car"), new CarriageType());
	public static final CarriageType CABOOSE = register(id("caboose"), new CarriageType());
	public static final CarriageType PASSENGER = register(id("passenger"), new CarriageType());
	public static final CarriageType GARDEN = register(id("garden"), new CarriageType());
	public static final CarriageType DEBUG = register(id("debug"), new CarriageType());

	public static void init() {
	}
}
