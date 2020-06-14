package com.prohitman.dragonage.util;

import com.prohitman.dragonage.DragonAge;
import com.prohitman.dragonage.client.gui.ForgingTableScreen;
import com.prohitman.dragonage.init.ModBlocks;
import com.prohitman.dragonage.init.ModContainerTypes;

import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = DragonAge.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber 
{
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		ScreenManager.registerFactory(ModContainerTypes.FORGING_TABLE_CONTAINER.get(), ForgingTableScreen::new);
		RenderTypeLookup.setRenderLayer(ModBlocks.FORGING_TABLE.get().getBlock(), RenderType.getCutout());
	}
}
