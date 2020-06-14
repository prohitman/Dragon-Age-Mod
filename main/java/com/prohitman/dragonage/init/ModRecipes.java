package com.prohitman.dragonage.init;

import com.prohitman.dragonage.DragonAge;
import com.prohitman.dragonage.recipes.ForgingTableRecipes;
import com.prohitman.dragonage.recipes.SteelShieldRecipes;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRecipes {
	public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = new DeferredRegister<>(
			ForgeRegistries.RECIPE_SERIALIZERS, DragonAge.MOD_ID);

	public static final RegistryObject<IRecipeSerializer<?>> CRAFTING_SPECIAL_STEEL_SHIELD = RECIPE_SERIALIZERS
			.register("crafting_special_steelshielddecoration",
					() -> new SpecialRecipeSerializer<>(SteelShieldRecipes::new));
	
	public static final RegistryObject<IRecipeSerializer<ForgingTableRecipes>> FORGING_TABLE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS
			.register("forging_table_recipe_serializer",
					() -> new ForgingTableRecipes.Serializer(ForgingTableRecipes::new));
}
