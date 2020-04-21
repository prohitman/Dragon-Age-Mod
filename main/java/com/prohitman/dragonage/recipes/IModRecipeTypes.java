package com.prohitman.dragonage.recipes;

import java.util.Optional;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public interface IModRecipeTypes<T extends IRecipe<?>>
{
	IRecipeType<ForgeringTableRecipes> FORGERING = register("forgering");

	static <T extends IRecipe<?>> IRecipeType<T> register(final String key) {
		return Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(key), new IRecipeType<T>() {
			public String toString() {
				return key;
			}
		});
	}

	@SuppressWarnings("unchecked")
	default <C extends IInventory> Optional<T> matches(IRecipe<C> recipe, World worldIn, C inv) {
	   return recipe.matches(inv, worldIn) ? Optional.of((T)recipe) : Optional.empty();
	}
}
