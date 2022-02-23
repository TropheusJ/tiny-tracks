package io.github.tropheusj.tiny_tracks.registry;

import com.tterrag.registrate.util.entry.BlockEntry;

import io.github.tropheusj.tiny_tracks.track.SimpleTrackBlock;
import io.github.tropheusj.tiny_tracks.track.SimpleTrackItem;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

import static io.github.tropheusj.tiny_tracks.TinyTracks.REGISTRATE;

public class TinyTracksBlocks {
	public static final BlockEntry<SimpleTrackBlock> SIMPLE_TRACK = REGISTRATE.block("simple_track", SimpleTrackBlock::new)
			.initialProperties(Material.DECORATION)
			.properties(p -> p.strength(0.4F).sound(SoundType.METAL))
			.lang("Track")
			.item(SimpleTrackItem::new)
			.defaultModel()
			.build()
			.blockstate(SimpleTrackBlock::stateGen)
			.register();

	public static void init() {
	}
}
