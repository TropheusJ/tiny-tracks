package io.github.tropheusj.tiny_tracks.track;

import io.github.tropheusj.tiny_tracks.track.SimpleTrackBlock.Shape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleTrackItem extends BlockItem {
	public SimpleTrackItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Nullable
	@Override
	protected BlockState getPlacementState(BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockPos clicked = context.getClickedPos();
		Direction facing = context.getHorizontalDirection();
		List<Direction> connections = SimpleTrackBlock.getConnections(level, clicked, facing);
		BlockState state = Shape.fromDirections(connections).toState();
		return state.canSurvive(level, clicked) ? state : null;
	}
}
