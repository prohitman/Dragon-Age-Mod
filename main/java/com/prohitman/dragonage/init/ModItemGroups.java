package com.prohitman.dragonage.init;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModItemGroups 
{
	public static final ItemGroup DRAGON_AGE_WEAPONS = (new ItemGroup("dragon_age_weapons") {
	      @OnlyIn(Dist.CLIENT)
	      public ItemStack createIcon() {
	         return new ItemStack(ModItems.STEEL_SWORD.get());
	      }
	   }).setNoScrollbar().setNoTitle();
}
