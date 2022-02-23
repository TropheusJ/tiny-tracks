package io.github.tropheusj.tiny_tracks.train;

import io.github.tropheusj.tiny_tracks.track.TrackType;
import io.github.tropheusj.tiny_tracks.util.Utils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Train Set represents a series of Carriage Types associated with it.
 * Sets use mutable carriage lists - addons can simply add to them.
 * @param validCarriages the CarriageTypes valid for this set. For example, a bullet
 *                       train will not have a coal car.
 * @param validTracks the TrackTypes valid for this set
 */
public record TrainSet(Set<TrackType> validTracks, Set<CarriageType> validCarriages) {
	public TrainSet(TrackType type, CarriageType... types) {
		this(Utils.toMutableSet(type), Utils.toMutableSet(types));
	}

	public TrainSet(TrackType type, Set<CarriageType> validCarriages) {
		this(Utils.toMutableSet(type), new HashSet<>(validCarriages));
	}

	public TrainSet(TrainSet copy) {
		this(new HashSet<>(copy.validTracks), new HashSet<>(copy.validCarriages));
	}

	@Override
	public boolean equals(Object o) {
		return this == o;
	}

	@Override
	public int hashCode() {
		return Objects.hash(validTracks, validCarriages);
	}
}
