package io.github.tropheusj.tiny_tracks.train;

import io.github.tropheusj.tiny_tracks.registry.registries.CarriageTypes;
import io.github.tropheusj.tiny_tracks.registry.registries.TrainSets;
import io.github.tropheusj.tiny_tracks.track.TrackBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

public class TrainItem extends Item {
	public TrainItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		if (!level.isClientSide()) {
			BlockPos target = context.getClickedPos();
			BlockState state = level.getBlockState(target);
			if (state.getBlock() instanceof TrackBlock track) {
				TrainEntity entity = tryCreateEntity(level, context.getItemInHand());
				if (entity != null) {
					if (track.positionTrain(entity, context)) {
						level.addFreshEntity(entity);
						return InteractionResult.SUCCESS;
					} else {
						entity.discard();
						return InteractionResult.FAIL;
					}
				}
			}
		}
		return super.useOn(context);
	}

	@Nullable
	public static TrainEntity tryCreateEntity(Level level, ItemStack stack) {
		// TODO store CarriageType and TrainSet in NBT
		return new TrainEntity(level, CarriageTypes.ENGINE, TrainSets.STEAM);
//		if (stackHoldsValidTrain(stack)) {
//			return createFromTag(level, stack.getTag());
//		}
//		return null;
	}

	public static boolean stackHoldsValidTrain(ItemStack stack) {
		if (stack.hasTag()) {
			CompoundTag tag = stack.getTag();
			if (tag.contains("CarriageType") && tag.contains("TrainSet")) {
				ResourceLocation carriage = new ResourceLocation(tag.getString("CarriageType"));
				ResourceLocation trainSet = new ResourceLocation(tag.getString("TrainSet"));
				CarriageType type = CarriageTypes.get(carriage);
				TrainSet set = TrainSets.get(trainSet);
				if (type != null && set != null) {
					return true;
				}
			}
		}
		return false;
	}

	public static TrainEntity createFromTag(Level level, CompoundTag tag) {
		ResourceLocation carriage = new ResourceLocation(tag.getString("CarriageType"));
		ResourceLocation trainSet = new ResourceLocation(tag.getString("TrainSet"));
		CarriageType type = CarriageTypes.get(carriage);
		TrainSet set = TrainSets.get(trainSet);
		return new TrainEntity(level, type, set);
	}
}
