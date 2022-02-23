package io.github.tropheusj.tiny_tracks.train.render.model;

import io.github.foundationgames.jsonem.JsonEM;
import io.github.tropheusj.tiny_tracks.TinyTracks;
import io.github.tropheusj.tiny_tracks.train.render.model.debug.DebugModel;
import io.github.tropheusj.tiny_tracks.train.render.model.extras.PumpCarModel;
import io.github.tropheusj.tiny_tracks.train.render.model.steam.SteamEngineModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

import org.apache.commons.lang3.mutable.MutableObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A registry for train models.
 */
@Environment(EnvType.CLIENT)
public enum TrainModels {;
	private static final Map<String, ModelData<?>> MODELS = new HashMap<>();

	/**
	 * Register a train model.
	 * @param modelPath String used for trains to get the desired model. See defaults for
	 *                  examples - should be in the format [train set]/[carriage type]
	 * @param factory model constructor
	 * @param location ModelLayerLocation for the model - you must register it yourself.
	 */
	public static <T extends TrainModel> ModelData<T> register(String modelPath, Factory<T> factory, ModelLayerLocation location) {
		ModelData<T> info = new ModelData<>(new MutableObject<>(null), factory, modelPath, location);
		MODELS.put(modelPath, info);
		return info;
	}

	/**
	 * Same as above, but will create and register a ModelLayerLocation for you using JsonEM
	 */
	public static <T extends TrainModel> ModelData<T> register(String modelPath, Factory<T> factory) {
		ModelLayerLocation loc = new ModelLayerLocation(TinyTracks.id(modelPath), "main");
//		JsonEM.registerModelLayer(loc);
		return register(modelPath, factory, loc);
	}

	public static ModelData<SimpleTrainModel> register(String modelPath) {
		return register(modelPath, SimpleTrainModel::new);
	}

	@SuppressWarnings("unchecked")
	public static <T extends TrainModel> ModelData<T> get(String modelPath) {
		return (ModelData<T>) MODELS.get(modelPath);
	}

//	public static final ModelData<PumpCarModel> PUMP_CAR = register("extras/pump_car", PumpCarModel::new);
//	public static final ModelData<DebugModel> DEBUG = register("debug/debug", DebugModel::new);
	public static final ModelData<SteamEngineModel> STEAM_ENGINE = register("steam/engine", SteamEngineModel::new);

	@FunctionalInterface
	public interface Factory<T extends TrainModel> {
		T create(ModelPart root);
	}

	public record ModelData<T extends TrainModel>(MutableObject<T> model, Factory<T> factory, String modelPath, ModelLayerLocation layer) {
		public T getModel() {
			return model.getValue();
		}

		@SuppressWarnings("unchecked")
		public void setModel(TrainModel model) {
			this.model.setValue((T) model);
		}
	}

	// called by EntityRendererDispatcher.onResourceManagerReload
	public static void reloadModels(Context ctx) {
		for (Map.Entry<String, ModelData<?>> entry : MODELS.entrySet()) {
			ModelData<?> data = entry.getValue();
			ModelPart root = ctx.bakeLayer(data.layer);
			data.setModel(data.factory.create(root));
		}
	}
}
