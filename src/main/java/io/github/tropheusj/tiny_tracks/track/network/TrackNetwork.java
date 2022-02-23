package io.github.tropheusj.tiny_tracks.track.network;

import io.github.tropheusj.tiny_tracks.TinyTracks;
import io.github.tropheusj.tiny_tracks.track.BaseTrackBlock;
import io.github.tropheusj.tiny_tracks.track.connection.WorldlyTrackSegment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * A track network represents a graph of track segments.
 * A track with multiple paths is considered a node.
 * The goal of this class is to keep track of paths and
 * allow for pathfinding once implemented.
 */
public class TrackNetwork {
	public final List<TrackNode> nodes = new ArrayList<>();
	public final List<TrackEdge> edges = new ArrayList<>();
	private boolean removed = false;

	/**
	 * Starting form the given segment, find all connections and reform this network.
	 */
	public void recalculate(WorldlyTrackSegment start) {
		if (removed) {
			TinyTracks.LOGGER.warn("tried to recalculate a removed network!");
			return;
		}
		nodes.clear();
		edges.clear();
		if (!(start.getState().getBlock() instanceof BaseTrackBlock startTrack)) {
			TinyTracks.LOGGER.error("Tried to calculate a WorldlyTrackSegment whose block is not a TrackBlock");
			return;
		}

		TrackNode startNode = new TrackNode(start);
		nodes.add(startNode);

		for (WorldlyTrackSegment connection : start.findConnections()) {
			TrackNode node = new TrackNode(connection);
//			if (!tracks.contains(node)) {
//				tracks.add(connection);
//				recalculate(connection);
//			}
		}
	}

	/**
	 * Called after this network has already been removed from its manager's list of networks
	 */
	public void invalidate() {
//		tracks.clear();
		removed = true;
	}

	public boolean contains(WorldlyTrackSegment segment) {
		return false;//tracks.contains(segment);
	}
}
