package io.github.tropheusj.tiny_tracks.track.network;

import io.github.tropheusj.tiny_tracks.track.connection.WorldlyTrackSegment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TrackNode {
	public final Map<TrackEdge, TrackNode> connections = new HashMap<>();
	public final WorldlyTrackSegment segment;

	public TrackNode(WorldlyTrackSegment segment) {
		this.segment = segment;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TrackNode trackNode = (TrackNode) o;
		return Objects.equals(segment, trackNode.segment);
	}

	@Override
	public int hashCode() {
		return segment.hashCode();
	}
}
