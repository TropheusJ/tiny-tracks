package io.github.tropheusj.tiny_tracks;

import com.tterrag.registrate.Registrate;

import io.github.tropheusj.tiny_tracks.registry.TinyTracksBlocks;
import io.github.tropheusj.tiny_tracks.registry.TinyTracksEntities;
import io.github.tropheusj.tiny_tracks.registry.TinyTracksItems;
import io.github.tropheusj.tiny_tracks.registry.registries.CarriageTypes;
import io.github.tropheusj.tiny_tracks.registry.registries.TrackTypes;
import io.github.tropheusj.tiny_tracks.registry.registries.TrainSets;
import io.github.tropheusj.tiny_tracks.track.network.TrackNetworkManager;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TinyTracks implements ModInitializer {
	public static final String ID = "tiny_tracks";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);
	public static final Registrate REGISTRATE = Registrate.create(ID)
			.creativeModeTab(() -> FabricItemGroupBuilder.build(TinyTracks.id("tab"), TinyTracksItems.TRAIN::asStack));

	@Override
	public void onInitialize() {
		register();
		ServerTickEvents.END_WORLD_TICK.register(TrackNetworkManager::tick);
	}

	private static void register() {
		TrackTypes.init();
		CarriageTypes.init();
		TrainSets.init();
		TinyTracksBlocks.init();
		TinyTracksItems.init();
		TinyTracksEntities.init();
		REGISTRATE.register();
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(ID, path);
	}
}
