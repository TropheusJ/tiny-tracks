package io.github.tropheusj.tiny_tracks.track;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

public class SimpleTrackItem extends BlockItem {
	public final BlockState toPlace;

	public SimpleTrackItem(BlockState state, Properties properties) {
		super(state.getBlock(), properties);
		this.toPlace = state;
	}

	@Nullable
	@Override
	protected BlockState getPlacementState(BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockPos clicked = context.getClickedPos();
		Direction facing = context.getHorizontalDirection();
		Player player = context.getPlayer();
		if (player != null && player.isCrouching())
			facing = facing.getOpposite();
		BlockState state = toPlace.rotate(rotFromDir(facing));
		return state.canSurvive(level, clicked) ? state : null;
	}

	public static Rotation rotFromDir(Direction d) {
		return switch (d) {
			case SOUTH -> Rotation.NONE;
			case EAST -> Rotation.COUNTERCLOCKWISE_90;
			case NORTH -> Rotation.CLOCKWISE_180;
			case WEST -> Rotation.CLOCKWISE_90;
			case UP, DOWN -> throw new IllegalArgumentException("Direction must be horizontal");
		};
	}
}
