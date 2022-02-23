package io.github.tropheusj.tiny_tracks.mixin;

import io.github.tropheusj.tiny_tracks.track.network.TrackNetworkManager;
import net.minecraft.resources.ResourceKey;

import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

import net.minecraft.world.level.storage.WritableLevelData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(Level.class)
public abstract class LevelMixin {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void tracks$init(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey,
							 DimensionType dimensionType, Supplier<ProfilerFiller> supplier, boolean bl,
							 boolean bl2, long l, CallbackInfo ci) {
		new TrackNetworkManager((Level) (Object) this);
	}
}
