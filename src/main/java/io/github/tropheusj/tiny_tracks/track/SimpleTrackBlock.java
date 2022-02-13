package io.github.tropheusj.tiny_tracks.track;

import com.google.common.collect.ImmutableList;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;

import io.github.tropheusj.tiny_tracks.TinyTracks;
import io.github.tropheusj.tiny_tracks.TinyTracksRegistry;
import io.github.tropheusj.tiny_tracks.util.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.generators.ConfiguredModel;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static net.minecraft.core.Direction.*;
import static net.minecraft.core.Direction.Plane.HORIZONTAL;

public class SimpleTrackBlock extends Block implements TrackBlock, SimpleWaterloggedBlock {
	private static final VoxelShaper shaper = VoxelShaper.forHorizontal(Block.box(3, 0, 9, 13, 3, 16), SOUTH);
	private static final VoxelShape base = Block.box(3, 0, 3, 13, 3, 13);

	public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public SimpleTrackBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(SHAPE, Shape.NORTH_SOUTH).setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(SHAPE, WATERLOGGED);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (!canSurvive(state, level, pos) && level instanceof ServerLevel server) {
			server.destroyBlock(pos, true);
			return;
		}
		List<Direction> connections = getConnections(level, pos, NORTH);
		Shape current = state.getValue(SHAPE);
		Shape newShape = Shape.fromDirections(connections);
		if (current != newShape && !level.isClientSide()) {
			level.setBlock(pos, state.setValue(SHAPE, newShape), UPDATE_ALL);
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		Shape shape = state.getValue(SimpleTrackBlock.SHAPE);
		List<VoxelShape> shapes = new ArrayList<>();
		for (Direction dir : Direction.values()) {
			if (shape.has(dir)) shapes.add(shaper.get(dir));
		}
		VoxelShape actual = base;
		for (VoxelShape vShape : shapes) {
			actual = Shapes.join(actual, vShape, BooleanOp.OR);
		}

		return actual;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return Block.canSupportRigidBlock(level, pos.below());
	}

	public static List<Direction> getConnections(Level level, BlockPos pos, @Nullable Direction fallback) {
		List<Direction> connections = new ArrayList<>(4);

		for (Direction direction : HORIZONTAL) {
			BlockPos neighborPos = pos.relative(direction);
			BlockState neighbor = level.getBlockState(neighborPos);

			if (neighbor.getBlock() instanceof TrackBlock track) {
				// todo checks for ability to connect
				connections.add(direction);
			}
		}

		if (connections.isEmpty() && fallback != null) {
			connections.add(fallback);
		}

		if (connections.size() == 1) { // 1 connection, face away from it
			connections.add(connections.get(0).getOpposite());
		}

		return connections;
	}

	public static void stateGen(DataGenContext<Block, SimpleTrackBlock> ctx, RegistrateBlockstateProvider prov) {
		prov.getVariantBuilder(ctx.get()).forAllStates(state -> {
			Shape shape = state.getValue(SimpleTrackBlock.SHAPE);
			String modelName = "block/simple_track" + (shape.type == ShapeType.STRAIGHT ? "" : "_" + shape.type);
			return ConfiguredModel.builder()
					.modelFile(prov.models().getExistingFile(prov.modLoc(modelName)))
					.rotationY(shape.rotFromBase)
					.build();
		});
	}

	public enum Shape implements StringRepresentable {
		NORTH_SOUTH(0, NORTH, SOUTH),
		EAST_WEST(90, EAST, WEST),

		NORTH_EAST(0, NORTH, EAST),
		SOUTH_EAST(90, SOUTH, EAST),
		NORTH_WEST(270, NORTH, WEST),
		SOUTH_WEST(180, SOUTH, WEST),

		NORTH_EAST_SOUTH(0, NORTH, EAST, SOUTH),
		EAST_SOUTH_WEST(90, EAST, SOUTH, WEST),
		NORTH_SOUTH_WEST(180, NORTH, SOUTH, WEST),
		NORTH_EAST_WEST(270, NORTH, EAST, WEST),

		CROSS(0, NORTH, SOUTH, EAST, WEST);

		public final Direction[] connections;
		public final ImmutableList<Direction> connectionsList;
		public final ShapeType type;
		public final int rotFromBase;

		Shape(int rotFromBase, Direction... connections) {
			this.rotFromBase = rotFromBase;
			this.connections = connections;
			this.connectionsList = ImmutableList.copyOf(connections);
			if (connections.length == 2) {
				if (connections[0].getOpposite() == connections[1]) {
					type = ShapeType.STRAIGHT;
				} else {
					type = ShapeType.CURVED;
				}
			} else {
				type = connections.length == 4 ? ShapeType.CROSS : ShapeType.T;
			}
		}

		public boolean has(Direction d) {
			for (Direction dir : connections) {
				if (dir == d) return true;
			}
			return false;
		}

		@Override
		public String getSerializedName() {
			return toString().toLowerCase(Locale.ROOT);
		}

		public BlockState toState() {
			return TinyTracksRegistry.SIMPLE_TRACK.getDefaultState().setValue(SHAPE, this);
		}

		public static Shape fromDirections(List<Direction> directions) {
			shapes: for (Shape shape : values()) {
			if (shape.connections.length != directions.size()) continue;
				for (Direction dir : shape.connections) {
					if (!directions.contains(dir)) {
						continue shapes;
					}
				}
				return shape;
			}
			TinyTracks.LOGGER.warn("Could not find shape for directions {}", directions);
			return NORTH_SOUTH;
		}
	}

	public enum ShapeType {
		STRAIGHT,
		CURVED,
		T,
		CROSS;

		@Override
		public String toString() {
			return super.toString().toLowerCase(Locale.ROOT);
		}
	}
}
