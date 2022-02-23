package io.github.tropheusj.tiny_tracks.track;

import io.github.tropheusj.tiny_tracks.track.connection.WorldlyTrackSegment;
import io.github.tropheusj.tiny_tracks.track.network.TrackNetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * A base implementation of a block capable of holding track segments.
 */
public abstract class BaseTrackBlock extends Block implements TrackBlock {
	public BaseTrackBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		for (WorldlyTrackSegment segment : getTrackSegments(level, pos, state)) {
			TrackNetworkManager.trackPlace(segment);
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		for (WorldlyTrackSegment segment : getTrackSegments(level, pos, state)) {
			TrackNetworkManager.trackRemoved(segment);
		}
	}
}
