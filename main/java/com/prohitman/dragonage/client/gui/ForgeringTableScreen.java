package com.prohitman.dragonage.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.prohitman.dragonage.DragonAge;
import com.prohitman.dragonage.containers.ForgeringTableContainer;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ForgeringTableScreen extends ContainerScreen<ForgeringTableContainer> {
	public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonAge.MOD_ID,
			"textures/gui/forging_table_gui.png");

	public ForgeringTableScreen(ForgeringTableContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		this.guiLeft = 0;
		this.guiTop = 0;
	}

	@Override
	public void render(final int mouseX, final int mouseY, final float partialTicks) {
		this.renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		this.font.drawString(this.title.getFormattedText(), 8.0f, 6.0f, 4210752);
		// this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(),
		// 8.0f, 90.0f, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		int i = (this.width - 176) / 2;
		int j = (this.height - 190) / 2;
		this.blit(i, j, 0, 0, 175, 189);
	}
	
	
}
