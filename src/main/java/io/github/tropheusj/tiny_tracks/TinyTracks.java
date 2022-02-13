package io.github.tropheusj.tiny_tracks;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TinyTracks implements ModInitializer {
	public static final String ID = "tiny_tracks";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
		TinyTracksRegistry.init();
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(ID, path);
	}
}
