package io.github.tropheusj.tiny_tracks.registry;

import com.tterrag.registrate.util.entry.EntityEntry;

import io.github.tropheusj.tiny_tracks.train.TrainEntity;
import io.github.tropheusj.tiny_tracks.train.render.TrainEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;

import static io.github.tropheusj.tiny_tracks.TinyTracks.REGISTRATE;

public class TinyTracksEntities {
	public static final EntityEntry<Entity> TRAIN_ENTITY = REGISTRATE.entity("train", TrainEntity::new, MobCategory.MISC)
			.renderer(() -> (ctx) -> (EntityRenderer<? super Entity>) (Object) new TrainEntityRenderer(ctx))
			.register();

	public static void init() {
	}
}
