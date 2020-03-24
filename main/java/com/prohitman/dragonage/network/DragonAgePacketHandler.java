package com.prohitman.dragonage.network;

import com.prohitman.dragonage.DragonAge;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class DragonAgePacketHandler 
{
	private static final String PROTOCOL_VERSION = "1";
	private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(DragonAge.MOD_ID, "network"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();

	public static void register(Class<MessageExtendedReachAttack> classIn, IMessage<MessageExtendedReachAttack> message)
	{
		int id = 0;

		HANDLER.registerMessage(id++, classIn, message::encode, message::decode, message::handle);
	}
	
}
