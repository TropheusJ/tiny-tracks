package io.github.tropheusj.tiny_tracks;

import io.github.tropheusj.tiny_tracks.track.connection.WorldlyTrackSegment;
import io.github.tropheusj.tiny_tracks.track.network.TrackNetworkManager;
import io.github.tropheusj.tiny_tracks.train.TrainEntity;
import io.github.tropheusj.tiny_tracks.train.render.model.TrainModels;
import io.github.tropheusj.tiny_tracks.train.render.model.debug.DebugModel;
import io.github.tropheusj.tiny_tracks.train.render.model.extras.PumpCarModel;
import io.github.tropheusj.tiny_tracks.train.render.model.steam.SteamEngineModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

@Environment(EnvType.CLIENT)
public class TinyTracksClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityModelLayerRegistry.registerModelLayer(TrainModels.STEAM_ENGINE.layer(), SteamEngineModel::createBodyLayer);
		ClientTickEvents.END_WORLD_TICK.register(TrackNetworkManager::tick);
		ClientPlayNetworking.registerGlobalReceiver(TrackNetworkManager.TRACK_ADDED_PACKET, (client, handler, buf, sender) -> {
			ClientLevel level = client.level;
			WorldlyTrackSegment placed = WorldlyTrackSegment.readFromBuf(buf, level);
			TrackNetworkManager.trackPlace(placed);
		});
		ClientPlayNetworking.registerGlobalReceiver(TrackNetworkManager.TRACK_REMOVED_PACKET, (client, handler, buf, sender) -> {
			ClientLevel level = client.level;
			WorldlyTrackSegment removed = WorldlyTrackSegment.readFromBuf(buf, level);
			TrackNetworkManager.trackRemoved(removed);
		});
		ClientPlayNetworking.registerGlobalReceiver(TrainEntity.SPAWN_PACKET, TrainEntity::recieveAddEntityPacket);
	}
}
