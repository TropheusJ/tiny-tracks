package io.github.tropheusj.tiny_tracks.registry;

import com.tterrag.registrate.util.entry.ItemEntry;

import io.github.tropheusj.tiny_tracks.track.SimpleTrackBlock;
import io.github.tropheusj.tiny_tracks.track.SimpleTrackBlock.Shape;
import io.github.tropheusj.tiny_tracks.track.SimpleTrackItem;
import io.github.tropheusj.tiny_tracks.train.TrainItem;

import static io.github.tropheusj.tiny_tracks.TinyTracks.REGISTRATE;

public class TinyTracksItems {
	public static final ItemEntry<TrainItem> TRAIN = REGISTRATE.item("train", TrainItem::new)
			.register();

	public static final ItemEntry<SimpleTrackItem> STRAIGHT_TRACK = REGISTRATE.item(
			"straight_track",
					p -> new SimpleTrackItem(
							TinyTracksBlocks.SIMPLE_TRACK.getDefaultState(),
							p
					)
			)
			.register();

	public static final ItemEntry<SimpleTrackItem> CURVED_TRACK = REGISTRATE.item(
					"curved_track",
					p -> new SimpleTrackItem(
							TinyTracksBlocks.SIMPLE_TRACK.getDefaultState()
									.setValue(SimpleTrackBlock.SHAPE, Shape.NORTH_EAST),
							p
					)
			)
			.register();
//
//	public static final ItemEntry<SimpleTrackItem> T_TRACK = REGISTRATE.item(
//					"t_track",
//					p -> new SimpleTrackItem(
//							TinyTracksBlocks.SIMPLE_TRACK.getDefaultState()
//									.setValue(SimpleTrackBlock.SHAPE, Shape.NORTH_EAST_WEST),
//							p
//					)
//			)
//			.register();

//	public static final ItemEntry<SimpleTrackItem> CROSS_TRACK = REGISTRATE.item(
//					"cross_track",
//					p -> new SimpleTrackItem(
//							TinyTracksBlocks.SIMPLE_TRACK.getDefaultState()
//									.setValue(SimpleTrackBlock.SHAPE, Shape.CROSS),
//							p
//					)
//			)
//			.register();

	public static void init() {
	}
}
