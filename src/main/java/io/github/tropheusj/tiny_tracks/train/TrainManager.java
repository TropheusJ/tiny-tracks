package io.github.tropheusj.tiny_tracks.train;

import io.github.tropheusj.tiny_tracks.TinyTracks;

import java.util.List;

/**
 * The TrainManager manages Train instances across all Levels.
 */
public class TrainManager {
	public static final TrainManager INSTANCE = new TrainManager();

	private List<Train> trains;

	public void registerTrain(Train train) {
		trains.add(train);
	}

	public void removeTrain(Train train) {
		if (trains.contains(train)) {
			trains.remove(train);
		} else {
			TinyTracks.LOGGER.error("Tried to remove a Train that wasn't registered!");
		}
	}

	public static void tick() {
	}

	private TrainManager() {
	}
}
