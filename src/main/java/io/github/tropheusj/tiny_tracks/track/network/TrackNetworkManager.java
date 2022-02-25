package io.github.tropheusj.tiny_tracks.track.network;

import com.google.common.collect.Multimap;

import io.github.tropheusj.tiny_tracks.TinyTracks;

import io.github.tropheusj.tiny_tracks.track.connection.TrackSegment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import org.checkerframework.checker.units.qual.K;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.CallbackI.V;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	public static Map<Level, TrackNetworkManager> ALL = new HashMap<>(4);

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
	public TrackNetwork get(TrackSegment segment, BlockPos pos) {
		for (TrackNetwork network : this.networks) {
			if (network.contains(segment, pos)) {
				return network;
			}
		}
		return null;
	}

	private TrackNetwork create() {
		TrackNetwork network = new TrackNetwork(level);
		networks.add(network);
		return network;
	}

	/**
	 * When a track segment is added, it must be given a network. This can be done
	 * through creating a new one or expanding a pre-existing one.
	 */
	private void handleAdd(TrackSegment added, BlockPos pos, TrackNetwork toAdd) {
		toAdd.recalculate(added, pos);
	}

	/**
	 * Sometimes an added segment is able to connect multiple pre-existing networks.
	 * For example, take two track blocks spaced 1 block apart.
	 * @param segment The track segment added causing the networks to combine
	 * @param toCombine The track networks to combine
	 */
	private void handleCombine(TrackSegment segment, BlockPos pos, List<TrackNetwork> toCombine) {
		if (toCombine.size() == 0) {
			TinyTracks.LOGGER.warn("tried to combine 0 networks?");
			return;
		}
		TrackNetwork base = toCombine.get(0);
		base.segments.put(pos, segment);
		for (TrackNetwork network : toCombine) {
			if (network != base) {
				base.segments.putAll(network.segments);
				remove(network);
			}
		}
	}

	/**
	 * When a TrackSegment with one connection is removed, there is no need to recalculate
	 * the network; the edge connection is simply removed.
	 */
	private void handleRemove(TrackSegment removed, BlockPos pos, TrackNetwork toRemove) {
		toRemove.segments.remove(pos, removed);
	}

	/**
	 * When a segment is removed, the segment's network must be split up into
	 * smaller ones. For example, take a line of 3 tracks. Remove the middle track block.
	 * The two edge segments will now each become their own network.
	 */
	private void handleSplit(TrackSegment removed, BlockPos pos, Multimap<BlockPos, TrackSegment> connections, TrackNetwork toSplit) {
		Entry<BlockPos, TrackSegment> first = getFirstEntry(connections);
		toSplit.recalculate(first.getValue(), first.getKey());
		List<TrackNetwork> splitNetworks = new ArrayList<>();
		splitNetworks.add(toSplit);
		for (Entry<BlockPos, TrackSegment> entry : connections.entries()) {
			TrackSegment segment = entry.getValue();
			// skip the first entry, it has already been handled
			if (!entry.equals(first)) {
				BlockPos segmentPos = entry.getKey();
				// these networks have already been recalculated.
				// if they do not contain the connection, another split is needed.
				TrackNetwork newSplit = null;
				for (TrackNetwork network : splitNetworks) {
					if (!network.contains(segment, segmentPos)) {
						newSplit = create();
						newSplit.recalculate(segment, segmentPos);
					}
				}
				if (newSplit != null) splitNetworks.add(newSplit);
			}
		}
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
	public static void trackPlace(TrackSegment placed, BlockPos pos, Level level) {
		TrackNetworkManager manager = ALL.get(level);
		if (manager == null) {
			warnMissing(level);
			return;
		}
		Multimap<BlockPos, TrackSegment> connections = placed.getConnections(pos, level);
		if (connections.isEmpty()) {
			// create new network
			manager.handleAdd(placed, pos, manager.create());
		} else if (connections.size() == 1) {
			// add to existing
			Entry<BlockPos, TrackSegment> entry = getFirstEntry(connections);
			TrackNetwork network = manager.get(entry.getValue(), entry.getKey());
			if (network == null) {
				TinyTracks.LOGGER.error("Tried to connect to a segment with no network!");
				return;
			}
			manager.handleAdd(placed, pos, network);
		} else {
			// check how many networks are being connected to
			List<TrackNetwork> networks = new ArrayList<>();
			for (Entry<BlockPos, TrackSegment> entry : connections.entries()) {
				TrackNetwork network = manager.get(entry.getValue(), entry.getKey());
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
				manager.handleAdd(placed, pos, network);
			} else {
				manager.handleCombine(placed, pos, networks);
			}
		}
		level.players().forEach(player -> {
			if (player instanceof ServerPlayer serverPlayer) {
				ServerPlayNetworking.send(serverPlayer, TRACK_ADDED_PACKET, placed.toBuf().writeBlockPos(pos));
			}
		});
	}

	/**
	 * Should only be called server-side.
	 */
	public static void trackRemoved(TrackSegment removed, BlockPos pos, Level level) {
		TrackNetworkManager manager = ALL.get(level);
		if (manager == null) {
			warnMissing(level);
			return;
		}
		// find network of this segment
		TrackNetwork network = manager.get(removed, pos);
		if (network == null) {
			TinyTracks.LOGGER.error("removed track already has no network!");
			return;
		}
		// check if the network must split
		Multimap<BlockPos, TrackSegment> connections = removed.getConnections(pos, level);
		if (connections.size() == 0) { // no connections - simply kill the network
			network.invalidate();
		} else if (connections.size() == 1) { // exactly 1 connecting segment - can't possibly split into multiple networks
			manager.handleRemove(removed, pos, network);
		} else { // multiple connections - might need to split
			manager.handleSplit(removed, pos, connections, network);
		}
		level.players().forEach(player -> {
			if (player instanceof ServerPlayer serverPlayer) {
				ServerPlayNetworking.send(serverPlayer, TRACK_REMOVED_PACKET, removed.toBuf().writeBlockPos(pos));
			}
		});
	}

	public static void warnMissing(Level level) {
		TinyTracks.LOGGER.warn("Track Network Manager not found for level {}", level.dimension().location());
	}

	public static <K, V> Entry<K, V> getFirstEntry(Multimap<K, V> map) {
		for (Entry<K, V> entry : map.entries()) {
			return entry;
		}
		throw new RuntimeException("Map contains no entries!");
	}
}
