package com.prohitman.dragonage.network;

import java.util.function.Supplier;

import com.prohitman.dragonage.util.IExtendedReach;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageExtendedReachAttack implements IMessage<MessageExtendedReachAttack> {
	private int entityId;

	public MessageExtendedReachAttack() {

	}

	public MessageExtendedReachAttack(int parEntityId) {
		this.entityId = parEntityId;
	}

	@Override
	public void encode(MessageExtendedReachAttack pkt, PacketBuffer buf) {
		buf.writeInt(pkt.entityId);
	}

	@Override
	public MessageExtendedReachAttack decode(PacketBuffer buf) {
		return new MessageExtendedReachAttack(buf.readInt());
	}

	@Override
	public void handle(MessageExtendedReachAttack message, Supplier<NetworkEvent.Context> ctx) {
		// DEBUG
		System.out.println("Message received");
		// Know it will be on the server so make it thread-safe
		final ServerPlayerEntity thePlayer =  ctx.get().getSender();
		ctx.get().enqueueWork(() -> new Runnable() {
			@Override
			public void run() {
				
				Entity theEntity = thePlayer.getEntityWorld().getEntityByID(message.entityId);
				// DEBUG
				System.out.println("Entity = " + theEntity);

				// Need to ensure that hackers can't cause trick kills,
				// so double check weapon type and reach

				if (thePlayer.getHeldItemMainhand().getItem() instanceof IExtendedReach && !(thePlayer.getHeldItemMainhand().getItem() == null))

				{
					IExtendedReach theExtendedReachWeapon = (IExtendedReach) thePlayer.getHeldItemMainhand().getItem();

					double distanceSq = thePlayer.getDistanceSq(theEntity);

					double reachSq = theExtendedReachWeapon.getReach() * theExtendedReachWeapon.getReach();

					if (!thePlayer.canEntityBeSeen(theEntity)) {
						reachSq /= 4.0d;
					}

					if (reachSq >= distanceSq) {
						if (theEntity instanceof ItemEntity || theEntity instanceof ExperienceOrbEntity
								|| theEntity instanceof ArrowEntity || theEntity == thePlayer) {
							thePlayer.connection.disconnect(new TranslationTextComponent(
									"multiplayer.disconnect.invalid_entity_attacked", new Object[0]));
							 //this.serverController.logWarning("Player " + thePlayer.getName() + " tried to attack an invalid entity");
						}
						thePlayer.attackTargetEntityWithCurrentItem(theEntity);
						System.out.println("This was Successful!");
					}
				} // no response in this case
			}
		});
		ctx.get().setPacketHandled(true);// no response message
	}
}
