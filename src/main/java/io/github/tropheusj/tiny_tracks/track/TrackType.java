package io.github.tropheusj.tiny_tracks.track;

import io.github.tropheusj.tiny_tracks.track.connection.WorldlyTrackSegment;
import io.github.tropheusj.tiny_tracks.train.TrainEntity;

import java.util.List;

/**
 * A Track Type is, well, a type of track. All track blocks must be
 * associated with a type. Ex. railway, monorail, hanging.
 * Track types can also hold several other properties, such as
 * max speed and max acceleration. They can also handle trains
 * more freely using tick methods.
 */
public class TrackType {
	/**
	 * Tick method called when a train is on this track.
	 * @param trains list of train entities currently on this track
	 */
	public void tickWithTrain(List<TrainEntity> trains, WorldlyTrackSegment track) {
	}

	public float maxSpeed() {
		return 0.1f;
	}

	public float maxAcceleration() {
		return 0.1f;
	}
}
