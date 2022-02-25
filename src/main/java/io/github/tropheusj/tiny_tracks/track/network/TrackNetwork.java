package io.github.tropheusj.tiny_tracks.track.network;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import io.github.tropheusj.tiny_tracks.TinyTracks;
import io.github.tropheusj.tiny_tracks.track.connection.TrackSegment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Map.Entry;
import java.util.Random;

/**
 * A track network represents a graph of track segments.
 * A track with multiple paths is considered a node.
 * The goal of this class is to keep track of paths and
 * allow for pathfinding once implemented.
 */
public class TrackNetwork {
	public static final int MAX_ITERATIONS = 10_000;
	public final Multimap<BlockPos, TrackSegment> segments = HashMultimap.create();
	public final Level level;
	public final float r;
	public final float g;
	public final float b;
	private boolean removed = false;
	private int iterations = 0;

	public TrackNetwork(Level level) {
		this.level = level;
		Random r = new Random();
		this.r = r.nextFloat();
		this.g = r.nextFloat();
		this.b = r.nextFloat();
	}

	/**
	 * Starting from the given segment, find all connections and reform this network.
	 */
	public void recalculate(TrackSegment startSegment, BlockPos startPos) {
		if (removed) {
			TinyTracks.LOGGER.warn("tried to recalculate a removed network!");
			return;
		}
		segments.clear();
		iterations = 0;
		segments.put(startPos, startSegment);
		doRecalculate(startSegment, startPos);
	}

	/**
	 * Recursively find all connections and add them to the network.
	 */
	private void doRecalculate(TrackSegment segment, BlockPos pos) {
		if (iterations > MAX_ITERATIONS) {
			TinyTracks.LOGGER.error("Network too complex!");
			return;
		}
		iterations++;
		Multimap<BlockPos, TrackSegment> connections = segment.getConnections(pos, level);
		for (Entry<BlockPos, TrackSegment> entry : connections.entries()) {
			BlockPos connectedPos = entry.getKey();
			TrackSegment connectedSegment = entry.getValue();
			if (!segments.containsEntry(connectedPos, connectedSegment)) {
				segments.put(connectedPos, connectedSegment);
				doRecalculate(connectedSegment, connectedPos);
			}
		}
	}

	/**
	 * Called after this network has already been removed from its manager's list of networks
	 */
	public void invalidate() {
		segments.clear();
		removed = true;
	}

	public boolean contains(TrackSegment segment, BlockPos pos) {
		return segments.containsEntry(pos, segment);
	}
}
