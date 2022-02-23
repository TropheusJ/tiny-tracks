package io.github.tropheusj.tiny_tracks.train;

import io.github.tropheusj.tiny_tracks.TinyTracks;
import net.minecraft.world.entity.Entity.RemovalReason;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A Train represents a series of TrainEntities linked together.
 * A Train handles the behavior of all linked entities.
 */
public class Train {
	/**
	 * The list of TrainEntities in this train. Lower indexes are considered
	 * closer to the front.
	 */
	private final List<TrainEntity> entities;

	public Train() {
		this(new ArrayList<>());
	}

	/**
	 * List must be mutable.
	 */
	public Train(List<TrainEntity> entities) {
		this.entities = entities;
		TrainManager.INSTANCE.registerTrain(this);
	}

	public void invalidate() {
		entities.clear();
		TrainManager.INSTANCE.removeTrain(this);
	}

	public void delete() {
		entities.forEach(e -> e.train = null);
		invalidate();
	}

	/**
	 * Add a TrainEntity to the start of this train, setting its Train field.
	 */
	public void prepend(TrainEntity entity) {
		entities.add(0, entity);
		entity.train = this;
	}

	/**
	 * Add a TrainEntity to the end of this train, setting its Train field.
	 */
	public void append(TrainEntity entity) {
		entities.add(entity);
		entity.train = this;
	}

	/**
	 * Split this Train into two smaller ones, such as when decoupling.
	 * This method will resize this Train to be the size of 'entitiesInHead',
	 * splitting all that come after into a new Train, which will be returned.
	 * For example, we have a Train of 4 entities: An engine, two cargo carriages,
	 * and one caboose. if 'entitiesInHead' is three, the pre-existing Train will
	 * lose the caboose, and a new Train will be created with just the caboose.
	 *
	 * @param entitiesInHead the number of entities to leave in this train.
	 * @return a new Train with the remaining entities, or null if there's not enough
	 * entities to split.
	 */
	@Nullable
	public Train split(int entitiesInHead) {
		if (entitiesInHead >= entities.size()) {
			TinyTracks.LOGGER.error("Tried to split more entities than available!");
			return null;
		}
		List<TrainEntity> toRemove = new ArrayList<>();
		for (int i = 0; i < entities.size(); i++) {
			if (i > entitiesInHead) toRemove.add(entities.get(i));
		}
		if (toRemove.isEmpty()) return null; // none to remove - no train created
		this.entities.removeAll(toRemove);
		if (entities.isEmpty()) invalidate(); // removed all - invalidate this Train
		return new Train(toRemove);
	}

	/**
	 * Merge two Trains into one larger one, such as when coupling.
	 * @param other the Train to merge with
	 * @param head if true, this Train will become the head, or front,
	 *             of the new train. Otherwise, the other Train will.
	 */
	public void merge(Train other, boolean head) {
		if (!head) {
			other.merge(this, true);
			return;
		}
		other.entities.forEach(this::append);
		other.invalidate();
	}

	/**
	 * Remove a TrainEntity from this Train.
	 * If the entity is in the front or the back, it will simply
	 * be removed. Otherwise, this Train will be split.
	 * @param entity the entity to remove
	 * @see Train#split(int)
	 */
	public void remove(TrainEntity entity) {
		if (entities.size() <= 1) {
			invalidate();
			return;
		}
		int index = entities.indexOf(entity);
		if (index == -1) {
			TinyTracks.LOGGER.error("Tried to remove entity [{}] from a train, but the train does not contain the entity!", entity.getId());
			return;
		}
		if (index == 0 || index == entities.size() - 1) { // first entity or last entity - let list take care of it
			entities.remove(index);
			entity.train = null;
		} else { // somewhere in the middle - need to split
			Train split = split(index);
			split.remove(entity); // safe recursion - entity will now be first in the split train
		}
	}

	public void entityRemoved(TrainEntity entity) {
		RemovalReason reason = entity.getRemovalReason();
		if (reason == null) {
			TinyTracks.LOGGER.error("An entity was removed, but it wasn't?");
			return;
		}
		if (reason == RemovalReason.UNLOADED_TO_CHUNK) {
			entityUnloadedToChunk(entity);
		} else if (reason == RemovalReason.CHANGED_DIMENSION) {
			// handed by TrainEntities, don't worry about it
		} else if (reason == RemovalReason.UNLOADED_WITH_PLAYER) {
			// TODO: entity transport, this will need handling
			remove(entity);
		} else { // killed or discarded - simply remove them from the Train
			remove(entity);
		}
	}

	private void entityUnloadedToChunk(TrainEntity entity) {
		// TODO what do I do here? Do I want trains to continue through unloaded chunks?
		// what if only part of a Train unloads? Unloaded chunks can be safely assumed to
		// be immutable, so continuing on pre-defined paths could work. If no pathing was
		// done in advance, then the usual freeze-at-edge works.
	}

	/**
	 * @param oldEntity the entity in the old dimension
	 * @param newEntity the entity in the new dimension
	 */
	public void entityChangedDimension(TrainEntity oldEntity, TrainEntity newEntity) {
		int index = entities.indexOf(oldEntity);
		if (index == -1) {
			TinyTracks.LOGGER.error("Tried to remove entity [{}] from a train on dimension change to replace it with [{}], but the train does not contain the entity!", oldEntity.getId(), newEntity.getId());
			return;
		}
		entities.set(index, newEntity);
		// TODO: interdimensional trains
		// what if part goes through and then stops?
		// what if part goes through and the portal breaks?
		// what if part goes through and then reverses?
		// what if there's a lava pit waiting on the other side and the entity is destroyed?
		// maybe prevent regular portals and require teleporting entire Train at once with some fancy blocks.
	}
}
