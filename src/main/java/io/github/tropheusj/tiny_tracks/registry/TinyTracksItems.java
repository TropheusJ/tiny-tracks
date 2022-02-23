package io.github.tropheusj.tiny_tracks.registry;

import com.tterrag.registrate.util.entry.ItemEntry;

import io.github.tropheusj.tiny_tracks.train.TrainItem;

import static io.github.tropheusj.tiny_tracks.TinyTracks.REGISTRATE;

public class TinyTracksItems {
	public static final ItemEntry<TrainItem> TRAIN = REGISTRATE.item("train", TrainItem::new)
			.register();

	public static void init() {
	}
}
