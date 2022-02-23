package io.github.tropheusj.tiny_tracks.track.network;

import io.github.tropheusj.tiny_tracks.TinyTracks;

import io.github.tropheusj.tiny_tracks.track.connection.WorldlyTrackSegment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Each instance of this class is linked to a Level instance.
 * Each level has its own manager, which manages all networks.
 * The job of this class is to allow for finding, creating, and removing networks.
 * This class exists and ticks on both client and server, but modifications are
 * made server side before being synced to clients.
 */
public class TrackNetworkManager {
	public static final ResourceLocation TRACK_ADDED_PACKET = TinyTracks.id("track_added");
	public static final ResourceLocation TRACK_REMOVED_PACKET = TinyTracks.id("track_removed");
	public static Map<Level, TrackNetworkManager> ALL = new HashMap<>(3);

	public final List<TrackNetwork> networks = new ArrayList<>();
	public final Level level;

	public TrackNetworkManager(Level level) {
		ALL.put(level, this);
		this.level = level;
	}

	public static void tick(Level level) {
		TrackNetworkManager manager = ALL.get(level);
		if (manager != null) {
			manager.tick();
		} else {
			warnMissing(level);
		}
	}

	public void tick() {
	}

	@Nullable
	public TrackNetwork get(WorldlyTrackSegment segment) {
		for (TrackNetwork network : this.networks) {
			if (network.contains(segment)) {
				return network;
			}
		}
		return null;
	}

	private TrackNetwork create() {
		TrackNetwork network = new TrackNetwork();
		networks.add(network);
		return network;
	}

	/**
	 * When a track segment is added, it must be given a network. This can be done
	 * through creating a new one or expanding a pre-existing one.
	 */
	private void handleAdd(WorldlyTrackSegment added, TrackNetwork toAdd) {
		toAdd.recalculate(added);
	}

	/**
	 * Sometimes an added segment is able to connect multiple pre-existing networks.
	 * For example, take two track blocks spaced 1 block apart.
	 * @param segment The track segment added causing the networks to combine
	 * @param toCombine The track networks to combine
	 */
	private void handleCombine(WorldlyTrackSegment segment, List<TrackNetwork> toCombine) {
		if (toCombine.size() == 0) {
			TinyTracks.LOGGER.warn("tried to combine 0 networks?");
			return;
		}
		TrackNetwork base = toCombine.get(0);
		for (TrackNetwork network : toCombine) {
			if (network != base) {
//				base.tracks.addAll(network.tracks);
				remove(network);
			}
		}
//		base.tracks.add(segment);
	}

	/**
	 * When a track segment is removed, it's network must be recalculated.
	 * The network can either be removed entirely, split into smaller ones, or
	 * simply recalculated without the missing segment.
	 */
	private void handleRemove(WorldlyTrackSegment removed, TrackNetwork toRemove) {
		List<WorldlyTrackSegment> adjacentSegments = removed.findConnections();

	}

	/**
	 * When a segment is removed, the segment's network must be split up into
	 * smaller ones. For example, take a line of 3 tracks. Remove the middle track block.
	 * The two edge segments will now each become their own network.
	 */
	private void handleSplit(WorldlyTrackSegment removed, TrackNetwork toSplit) {

	}

	/**
	 * Invalidate a track network and remove it from the world.
	 */
	private void remove(TrackNetwork network) {
		networks.remove(network);
		network.invalidate();
	}

	/**
	 * Should only be called server-side.
	 */
	public static void trackPlace(WorldlyTrackSegment placed) {
		Level level = placed.level();
		TrackNetworkManager manager = ALL.get(level);
		if (manager == null) {
			warnMissing(level);
			return;
		}
		List<WorldlyTrackSegment> connections = placed.findConnections();
		if (connections.isEmpty()) {
			// create new network
			manager.handleAdd(placed, manager.create());
		} else if (connections.size() == 1) {
			// add to existing
			WorldlyTrackSegment connected = connections.get(0);
			TrackNetwork network = connected.getNetwork();
			if (network == null) {
				TinyTracks.LOGGER.error("Tried to connect to a segment with no network!");
				return;
			}
			manager.handleAdd(placed, network);
		} else {
			// check how many networks are being connected to
			List<TrackNetwork> networks = new ArrayList<>();
			for (WorldlyTrackSegment segment : connections) {
				TrackNetwork network = segment.getNetwork();
				if (network == null) {
					TinyTracks.LOGGER.error("Tried to connect to a segment with no network!");
					return;
				}
				if (!networks.contains(network)) {
					networks.add(network);
				}
			}
			if (networks.size() == 1) {
				TrackNetwork network = networks.get(0);
				manager.handleAdd(placed, network);
			} else {
				manager.handleCombine(placed, networks);
			}
		}
		level.players().forEach(player -> {
			if (player instanceof ServerPlayer serverPlayer) {
				ServerPlayNetworking.send(serverPlayer, TRACK_ADDED_PACKET, placed.toBuf());
			}
		});
	}

	/**
	 * Should only be called server-side.
	 */
	public static void trackRemoved(WorldlyTrackSegment removed) {
		Level level = removed.level();
		TrackNetworkManager manager = ALL.get(level);
		if (manager == null) {
			warnMissing(level);
			return;
		}
		// find network of this segment
		TrackNetwork network = removed.getNetwork();
		if (network == null) {
			TinyTracks.LOGGER.error("removed track already has no network!");
			return;
		}
		// check if the network must split
		List<WorldlyTrackSegment> adjacent = removed.findConnections();
		if (adjacent.size() == 0) { // no connections - simply kill the network
			network.invalidate();
		} else if (adjacent.size() == 1) { // exactly 1 connecting segment - can't possibly split into multiple networks
			manager.handleRemove(removed, network);
		} else { // multiple connections - might need to split
			// check if we actually need to split
			manager.handleSplit(removed, network);
		}
		level.players().forEach(player -> {
			if (player instanceof ServerPlayer serverPlayer) {
				ServerPlayNetworking.send(serverPlayer, TRACK_REMOVED_PACKET, removed.toBuf());
			}
		});
	}

	public static void warnMissing(Level level) {
		TinyTracks.LOGGER.warn("Track Network Manager not found for level {}", level.dimension().location());
	}
}
