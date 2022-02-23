package io.github.tropheusj.tiny_tracks.track;

import io.github.tropheusj.tiny_tracks.track.connection.TrackConnection;
import io.github.tropheusj.tiny_tracks.track.connection.TrackSegment;
import io.github.tropheusj.tiny_tracks.track.connection.WorldlyTrackSegment;
import io.github.tropheusj.tiny_tracks.train.TrainEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public interface TrackBlock {
	/**
	 * @return a list of TrackSegments available with the BlockState
	 */
	List<TrackSegment> getTrackSegments(BlockState state);

	/**
	 * @return a list of TrackSegments available with the BlockState, but with the context of a world and pos.
	 */
	default List<WorldlyTrackSegment> getTrackSegments(Level level, BlockPos pos, BlockState state) {
		List<WorldlyTrackSegment> worldySegments = new ArrayList<>();
		for (TrackSegment segment : getTrackSegments(state)) {
			worldySegments.add(new WorldlyTrackSegment(segment, level, pos));
		}
		return worldySegments;
	}

	/**
	 * @return this TrackBlock can connect to the supplied TrackConnection
	 */
	default boolean connectsWith(TrackConnection other, BlockState thisState) {
		for (TrackSegment segment : getTrackSegments(thisState)) {
			if (segment.hasConnection(other)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Place a TrainEntity onto the tracks at the correct position.
	 * @return true if train placed successfully, false otherwise
	 */
	default boolean positionTrain(TrainEntity train, UseOnContext context) {
		train.setPos(context.getClickLocation().add(0, 1, 0));
		return true;
	}
}
