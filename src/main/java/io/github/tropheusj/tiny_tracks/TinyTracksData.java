package io.github.tropheusj.tiny_tracks;

import com.tterrag.registrate.fabric.GatherDataEvent;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

public class TinyTracksData implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		var existingData = System.getProperty("ExistingData").split(";");
		var existingFileHelper = new ExistingFileHelper(Arrays.stream(existingData).map(Paths::get).toList(), Collections.emptySet(),
				true, null, null);
		GatherDataEvent.EVENT.invoker().gatherData(generator, existingFileHelper);
	}
}
