package io.github.tropheusj.tiny_tracks.track.connection;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import net.minecraft.core.Direction;

import net.minecraft.core.Direction.Plane;
import net.minecraft.network.FriendlyByteBuf;

import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a connection to a track; a TrackConnectionPoint in a specific direction.
 * This is basically an enum, but too many values to write them out manually.
 */
public class TrackConnection {
	private static final Table<TrackConnectionPoint, Direction, TrackConnection> TABLE = HashBasedTable.create(6, 4);
	public static final TrackConnection[] VALUES = new TrackConnection[6 * 4];

	public final TrackConnectionPoint point;
	public final Direction face;
	public final int ordinal;
	public final Vec3 pos;
	public final String name;

	private TrackConnection(TrackConnectionPoint point, Direction face, int ordinal) {
		this.point = point;
		this.face = face;
		this.ordinal = ordinal;
		this.pos = point.getPos(face);
		this.name = String.format("TrackConnection{%s/%s}", point, face);
	}

	static {
		int ordinal = 0;
		for (TrackConnectionPoint point : TrackConnectionPoint.values()) {
			for (Direction face : Plane.HORIZONTAL) {
				TrackConnection c = new TrackConnection(point, face, ordinal);
				TABLE.put(point, face, c);
				VALUES[ordinal] = c;
				ordinal++;
			}
		}
	}

	public static TrackConnection get(TrackConnectionPoint point, Direction face) {
		return TABLE.get(point, face);
	}

	public static TrackConnection get(int ordinal) {
		return VALUES[ordinal];
	}

	public static TrackConnection get(Direction face) {
		return get(TrackConnectionPoint.BOTTOM_MIDDLE, face);
	}

	public static TrackConnection get(FriendlyByteBuf buf) {
		int ordinal = buf.readVarInt();
		return get(ordinal);
	}

	@Nullable
	public TrackConnectionPoint connectingPoint(TrackConnection other) {
//		if (this == other) return this.point;
		// if the two connections' faces are not in the same place, they cannot connect
		if (this.face.getOpposite() != other.face) {
			return null;
		}
		// if the two connections' points are not the same, they cannot connect
		// for two blocks facing each other, their points will be mirrored, so we need to account for that
		if (this.point != other.point.getMirroredForConnection()) {
			return null;
		}
		return this.point;
	}

	public boolean canConnect(TrackConnection other) {
		return connectingPoint(other) != null;
	}

	public void writeToBuf(FriendlyByteBuf buf) {
		buf.writeVarInt(ordinal);
	}

	@Override
	public int hashCode() {
		return Objects.hash(point, face, ordinal, pos);
	}

	@Override
	public String toString() {
		return name;
	}
}
