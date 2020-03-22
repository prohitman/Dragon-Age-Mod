package com.prohitman.dragonage.items;

import com.prohitman.dragonage.init.ModItems;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

public class SteelShieldItem extends ShieldItem
{

	public SteelShieldItem(Item.Properties builder) 
	{
		super(builder);
	}
	
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) 
	{
		return repair.getItem() == ModItems.STEEL_INGOT.get() || super.getIsRepairable(toRepair, repair);
	}
	
	@Override
	public boolean isShield(ItemStack stack, LivingEntity entity)
	{
		return true;
	}
}
