package io.github.tropheusj.tiny_tracks.track;

import io.github.tropheusj.tiny_tracks.track.connection.TrackSegment;
import io.github.tropheusj.tiny_tracks.train.TrainEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public interface TrackBlock {
	/**
	 * @return a list of TrackSegments available with the BlockState
	 */
	List<TrackSegment> getTrackSegments(BlockPos pos, BlockState state, Level level);

	/**
	 * Place a TrainEntity onto the tracks at the correct position.
	 * @return true if train placed successfully, false otherwise
	 */
	default boolean positionTrain(TrainEntity train, UseOnContext context) {
		train.setPos(context.getClickLocation().add(0, 1, 0));
		return true;
	}
}
