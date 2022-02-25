package io.github.tropheusj.tiny_tracks;

import io.github.tropheusj.tiny_tracks.track.connection.TrackSegment;
import io.github.tropheusj.tiny_tracks.track.network.TrackNetworkManager;
import io.github.tropheusj.tiny_tracks.train.TrainEntity;
import io.github.tropheusj.tiny_tracks.train.render.model.TrainModels;
import io.github.tropheusj.tiny_tracks.train.render.model.steam.SteamEngineModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;

@Environment(EnvType.CLIENT)
public class TinyTracksClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityModelLayerRegistry.registerModelLayer(TrainModels.STEAM_ENGINE.layer(), SteamEngineModel::createBodyLayer);
		ClientTickEvents.END_WORLD_TICK.register(TrackNetworkManager::tick);
		ClientPlayNetworking.registerGlobalReceiver(TrackNetworkManager.TRACK_ADDED_PACKET, (client, handler, buf, sender) -> {
			TrackSegment placed = TrackSegment.fromBuf(buf);
			BlockPos pos = buf.readBlockPos();
			client.execute(() -> {
				ClientLevel level = client.level;
				TrackNetworkManager.trackPlace(placed, pos, level);
			});
		});
		ClientPlayNetworking.registerGlobalReceiver(TrackNetworkManager.TRACK_REMOVED_PACKET, (client, handler, buf, sender) -> {
			TrackSegment removed = TrackSegment.fromBuf(buf);
			BlockPos pos = buf.readBlockPos();
			client.execute(() -> {
				ClientLevel level = client.level;
				TrackNetworkManager.trackRemoved(removed, pos, level);
			});
		});
		ClientPlayNetworking.registerGlobalReceiver(TrainEntity.SPAWN_PACKET, TrainEntity::recieveAddEntityPacket);
	}
}
