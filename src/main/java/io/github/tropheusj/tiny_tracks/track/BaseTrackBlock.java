package io.github.tropheusj.tiny_tracks.track;

import io.github.tropheusj.tiny_tracks.track.connection.TrackSegment;
import io.github.tropheusj.tiny_tracks.track.network.TrackNetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A base implementation of a block capable of holding track segments.
 */
public abstract class BaseTrackBlock extends Block implements TrackBlock {
	public BaseTrackBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		for (TrackSegment segment : getTrackSegments(pos, state, level)) {
			TrackNetworkManager.trackPlace(segment, pos, level);
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		for (TrackSegment segment : getTrackSegments(pos, state, level)) {
			TrackNetworkManager.trackRemoved(segment, pos, level);
		}
	}
}
