package io.github.tropheusj.tiny_tracks.train;

import io.github.tropheusj.tiny_tracks.TinyTracks;
import io.github.tropheusj.tiny_tracks.registry.TinyTracksEntities;
import io.github.tropheusj.tiny_tracks.registry.registries.CarriageTypes;
import io.github.tropheusj.tiny_tracks.registry.registries.TrainSets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

public class TrainEntity extends Entity {
	public static final ResourceLocation SPAWN_PACKET = TinyTracks.id("train_entity_spawn");

	private ResourceLocation texture;
	private String model;
	public CarriageType carriage;
	public TrainSet trainSet;
	@Nullable
	public Train train;

	public TrainEntity(EntityType<?> entityType, Level level) {
		super(entityType, level);
		this.carriage = CarriageTypes.DEBUG;
		this.trainSet = TrainSets.DEBUG;
	}

	// use this one
	public TrainEntity(Level level, CarriageType carriage, TrainSet set) {
		this(TinyTracksEntities.TRAIN_ENTITY.get(), level);
		this.carriage = carriage;
		this.trainSet = set;
	}

	@Override
	public void tick() {
		super.tick();
		carriage.tick(this);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		discard();
		return true;
	}

	@Override
	public boolean isPickable() {
		return !isRemoved();
	}

	@Override
	public boolean isPushable() {
		return !isRemoved();
	}

	@Nullable
	@Override
	public Entity changeDimension(ServerLevel destination) {
		Entity inOtherDimension = super.changeDimension(destination);
		if (train != null && inOtherDimension instanceof TrainEntity TrainEntity) {
			train.entityChangedDimension(this, TrainEntity);
		}
		return inOtherDimension;
	}

	@Override
	public void setRemoved(RemovalReason reason) {
		super.setRemoved(reason);
		if (train != null) {
			train.entityRemoved(this);
		}
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		String carriageType = compound.getString("CarriageType");
		ResourceLocation loc = new ResourceLocation(carriageType);
		this.carriage = CarriageTypes.get(loc);
		String trainSet = compound.getString("TrainSet");
		ResourceLocation trainSetLoc = new ResourceLocation(trainSet);
		this.trainSet = TrainSets.get(trainSetLoc);
		this.carriage.readData(this, compound.getCompound("CarriageData"));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		compound.putString("CarriageType", CarriageTypes.get(carriage).toString());
		compound.putString("TrainSet", TrainSets.get(trainSet).toString());
		CompoundTag carriageData = new CompoundTag();
		carriage.writeData(this, carriageData);
		compound.put("CarriageData", carriageData);
	}

	public void readPacketData(FriendlyByteBuf data) {
		trainSet = TrainSets.get(data.readResourceLocation());
		carriage = CarriageTypes.get(data.readResourceLocation());
		carriage.readData(this, data);
	}

	public void writePacketData(FriendlyByteBuf data) {
		data.writeResourceLocation(TrainSets.get(trainSet));
		data.writeResourceLocation(CarriageTypes.get(carriage));
		carriage.writeData(this, data);
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		FriendlyByteBuf data = PacketByteBufs.create();
		new ClientboundAddEntityPacket(this).write(data);
		writePacketData(data);
		return ServerPlayNetworking.createS2CPacket(SPAWN_PACKET, data);
	}

	@Environment(EnvType.CLIENT)
	public static void recieveAddEntityPacket(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
		FriendlyByteBuf copy = PacketByteBufs.copy(buf); // copy so it survives client.execute()
		client.execute(() -> {
			ClientboundAddEntityPacket spawnPacket = new ClientboundAddEntityPacket(copy);
			int entityId = spawnPacket.getId();
			handler.handleAddEntity(spawnPacket);
			Entity entity = client.level.getEntity(entityId);
			if (entity instanceof TrainEntity train) {
				train.readPacketData(copy);
			} else {
				TinyTracks.LOGGER.error("TrainEntity spawn data received, but no corresponding entity was found! Entity: [{}]", entity);
			}
			copy.release();
		});
	}

	public String getModel() {
		if (model == null)
			initAssets();
		return model;
	}

	public ResourceLocation getTexture() {
		if (texture == null)
			initAssets();
		return texture;
	}

	private void initAssets() {
		ResourceLocation carriageId = CarriageTypes.get(carriage);
		ResourceLocation setId = TrainSets.get(trainSet);
		this.model = String.format("%s/%s", setId.getPath(), carriageId.getPath());
		this.texture = TinyTracks.id(String.format("textures/train/%s.png", this.model));
	}

	@Override
	protected void defineSynchedData() {
	}
}
