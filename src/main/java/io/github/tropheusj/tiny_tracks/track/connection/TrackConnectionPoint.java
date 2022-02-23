package io.github.tropheusj.tiny_tracks.track.connection;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;

/**
 * Tracks can connect to one of 6 points on each of a block's 4 horizontal faces.
 */
public enum TrackConnectionPoint {
	// 0.3 -> offset east/left
	// -0.3 -> offset west/right
	// 0.6 -> offset up for top points
	// 0.5 -> offset south; all points are based on south block face, so add half a block
	TOP_LEFT(new Vec3(0.3, 0.6, 0.5)),
	TOP_MIDDLE(new Vec3(0, 0.6, 0.5)),
	TOP_RIGHT(new Vec3(-0.3, 0.6, 0.5)),
	BOTTOM_LEFT(new Vec3(0.3, 0, 0.5)),
	BOTTOM_MIDDLE(new Vec3(0, 0, 0.5)),
	BOTTOM_RIGHT(new Vec3(-0.3, 0, 0.5));

	public final Vec3 southPos;
	public final Vec3 eastPos;
	public final Vec3 northPos;
	public final Vec3 westPos;

	TrackConnectionPoint(Vec3 pos) {
		this.southPos = pos;
		eastPos = southPos.yRot((float) (Math.PI / 2));
		northPos = southPos.yRot((float) (Math.PI));
		westPos = southPos.yRot((float) ((Math.PI * 3) / 2));
	}

	public Vec3 getPos(Direction rotation) {
		return switch (rotation) {
			case SOUTH -> southPos;
			case EAST -> eastPos;
			case NORTH -> northPos;
			case WEST -> westPos;
			case UP, DOWN -> throw new IllegalArgumentException("Direction must be horizontal");
		};
	}

	/**
	 * For a connection to be made, the connection points of segments must align.
	 * Two track blocks attempting to connect will have their points mirrored
	 * relative to each other, so we need to account for this.
	 */
	public TrackConnectionPoint getMirroredForConnection() {
		return switch (this) {
			case TOP_LEFT -> TOP_RIGHT;
			case TOP_RIGHT -> TOP_LEFT;
			case BOTTOM_LEFT -> BOTTOM_RIGHT;
			case BOTTOM_RIGHT -> BOTTOM_LEFT;
			default -> this;
		};
	}

	@Override
	public String toString() {
		return super.toString().toLowerCase(Locale.ROOT);
	}
}
