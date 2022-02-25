package io.github.tropheusj.tiny_tracks.track;

import io.github.tropheusj.tiny_tracks.track.connection.TrackSegment;
import io.github.tropheusj.tiny_tracks.train.Train;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A Track Type is, well, a type of track. All track blocks must be
 * associated with a type. Ex. railway, monorail, hanging.
 * Track types can also hold several other properties, such as
 * max speed and max acceleration. They can also handle trains
 * more freely using tick methods.
 */
public class TrackType {
	/**
	 * Tick method called when a train is on a TrackSegment of this type.
	 */
	public void tickWithTrain(Train train, TrackSegment segment, BlockPos pos, BlockState state, Level level) {
	}

	public float maxSpeed() {
		return 0.1f;
	}

	public float maxAcceleration() {
		return 0.1f;
	}
}
