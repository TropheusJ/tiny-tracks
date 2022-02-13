package io.github.tropheusj.tiny_tracks;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntry;

import io.github.tropheusj.tiny_tracks.track.SimpleTrackBlock;
import io.github.tropheusj.tiny_tracks.track.SimpleTrackItem;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class TinyTracksRegistry {
	public static final Registrate REGISTRATE = Registrate.create(TinyTracks.ID);

	public static final BlockEntry<SimpleTrackBlock> SIMPLE_TRACK = REGISTRATE.block("simple_track", SimpleTrackBlock::new)
			.initialProperties(Material.DECORATION)
			.properties(p -> p.strength(0.7F).sound(SoundType.METAL))
			.lang("Track")
			.item(SimpleTrackItem::new)
			.model(SimpleTrackItem::modelGen)
			.build()
			.blockstate(SimpleTrackBlock::stateGen)
			.register();

	public static void init() {
		REGISTRATE.register();
	}
}
