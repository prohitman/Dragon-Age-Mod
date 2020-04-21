package com.prohitman.dragonage.init;

import com.prohitman.dragonage.DragonAge;
import com.prohitman.dragonage.items.HalberdItem;
import com.prohitman.dragonage.items.ModItemTiers;
import com.prohitman.dragonage.items.SteelShieldItem;

import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems 
{
	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, DragonAge.MOD_ID);
	
	//Materials
	public static final RegistryObject<Item> STEEL_INGOT = ITEMS.register("steel_ingot", () -> new Item(new Item.Properties().group(ItemGroup.MATERIALS).maxStackSize(64)));
	
	//Weapons
	public static final RegistryObject<Item> STEEL_SWORD = ITEMS.register("steel_sword", () -> new SwordItem(ModItemTiers.STEEL, 3, -2.4F, new Item.Properties().group(ModItemGroups.DRAGON_AGE_WEAPONS)));
	public static final RegistryObject<Item> GREAT_STEEL_SWORD = ITEMS.register("great_steel_sword", () -> new SwordItem(ModItemTiers.STEEL, 4, -3.0F, new Item.Properties().group(ModItemGroups.DRAGON_AGE_WEAPONS)));
	public static final RegistryObject<Item> STEEL_SHIELD = ITEMS.register("steel_shield", () -> new SteelShieldItem(new Item.Properties().maxDamage(500).group(ModItemGroups.DRAGON_AGE_WEAPONS)));
	public static final RegistryObject<Item> STEEL_HALBERD = ITEMS.register("steel_halberd", () -> new HalberdItem(ModItemTiers.STEEL, 4, -3.0F, 9.0F, new Item.Properties().group(ModItemGroups.DRAGON_AGE_WEAPONS)));
	public static final RegistryObject<Item> STEEL_BATTLE_AXE = ITEMS.register("steel_battle_axe", () -> new AxeItem(ModItemTiers.STEEL, 8.0F, -3.1F, new Item.Properties().group(ModItemGroups.DRAGON_AGE_WEAPONS)));
	
	//Tools
	public static final RegistryObject<Item> STEEL_SHOVEL = ITEMS.register("steel_shovel", () -> new ShovelItem(ModItemTiers.STEEL, 1.5F, -3.0F, new Item.Properties().group(ModItemGroups.DRAGON_AGE_TOOLS)));
	public static final RegistryObject<Item> STEEL_PICKAXE = ITEMS.register("steel_pickaxe", () -> new PickaxeItem(ModItemTiers.STEEL, 1, -2.8F, new Item.Properties().group(ModItemGroups.DRAGON_AGE_TOOLS)));
	public static final RegistryObject<Item> STEEL_AXE = ITEMS.register("steel_axe", () -> new AxeItem(ModItemTiers.STEEL, 6.0F, -3.1F, new Item.Properties().group(ModItemGroups.DRAGON_AGE_TOOLS)));
	public static final RegistryObject<Item> STEEL_HOE = ITEMS.register("steel_hoe", () -> new HoeItem(ModItemTiers.STEEL, -1.5F, new Item.Properties().group(ModItemGroups.DRAGON_AGE_TOOLS)));
}
