package com.prohitman.dragonage.recipes;

import com.google.gson.JsonObject;
import com.prohitman.dragonage.init.ModRecipes;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ForgeringTableRecipes implements IRecipe<CraftingInventory> {
	protected final ResourceLocation id;
	protected final String group;
	protected final Ingredient firstIngredient;
	protected final Ingredient secondIngredient;
	protected final IRecipeType<?> type;
	protected final IRecipeSerializer<?> serializer;
	protected final ItemStack result;

	public ForgeringTableRecipes(ResourceLocation idIn, String groupIn, Ingredient firstIngredientIn,
			Ingredient secondIngredientIn, ItemStack resultIn) {
		this.id = idIn;
		this.group = groupIn;
		this.firstIngredient = firstIngredientIn;
		this.secondIngredient = secondIngredientIn;
		this.result = resultIn;
		this.type = this.getType();
		this.serializer = this.getSerializer();
	}
	
	@Override
	public boolean isDynamic()
	{
		return true;
	}
	
	/**
	 * Used to determine if this recipe can fit in a grid of the given width/height
	 */
	public boolean canFit(int width, int height) {
		return true;
	}

	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(this.firstIngredient);
		nonnulllist.add(this.secondIngredient);
		return nonnulllist;
	}

	/**
	 * Get the result of this recipe, usually for display purposes (e.g. recipe
	 * book). If your recipe has more than one possible result (e.g. it's dynamic
	 * and depends on its inputs), then return an empty stack.
	 */
	public ItemStack getRecipeOutput() {
		return this.result;
	}

	/**
	 * Recipes with equal group are combined into one button in the recipe book
	 */
	public String getGroup() {
		return this.group;
	}

	public ResourceLocation getId() {
		return this.id;
	}

	public IRecipeType<?> getType() {
		return IModRecipeTypes.FORGERING;
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		return this.firstIngredient.test(inv.getStackInSlot(0)) && this.secondIngredient.test(inv.getStackInSlot(1));
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		return this.result.copy();
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipes.FORGERING_TABLE_RECIPE_SERIALIZER.get();
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ForgeringTableRecipes> {
		private final IRecipeFactory<ForgeringTableRecipes> factory;

		public Serializer(IRecipeFactory<ForgeringTableRecipes> factory) {
			this.factory = factory;
		}

		public ForgeringTableRecipes read(ResourceLocation recipeId, JsonObject json) {
			String s = JSONUtils.getString(json, "group", "");
			Ingredient firstIngredient;
			Ingredient secondIngredient;
			if (JSONUtils.isJsonArray(json, "first_ingredient")) {
				firstIngredient = Ingredient.deserialize(JSONUtils.getJsonArray(json, "first_ingredient"));
			} else {
				firstIngredient = Ingredient.deserialize(JSONUtils.getJsonObject(json, "first_ingredient"));
			}

			if (JSONUtils.isJsonArray(json, "second_ingredient")) {
				secondIngredient = Ingredient.deserialize(JSONUtils.getJsonArray(json, "second_ingredient"));
			} else {
				secondIngredient = Ingredient.deserialize(JSONUtils.getJsonObject(json, "second_ingredient"));
			}

			String s1 = JSONUtils.getString(json, "result");
			int i = JSONUtils.getInt(json, "count");
			@SuppressWarnings("deprecation")
			ItemStack itemstack = new ItemStack(Registry.ITEM.getOrDefault(new ResourceLocation(s1)), i);
			return this.factory.create(recipeId, s, firstIngredient, secondIngredient, itemstack);
		}

		public ForgeringTableRecipes read(ResourceLocation recipeId, PacketBuffer buffer) {
			String s = buffer.readString(32767);
			Ingredient firstIngredient = Ingredient.read(buffer);
			Ingredient secondIngredient = Ingredient.read(buffer);
			ItemStack itemstack = buffer.readItemStack();
			return this.factory.create(recipeId, s, firstIngredient, secondIngredient, itemstack);
		}

		public void write(PacketBuffer buffer, ForgeringTableRecipes recipe) {
			buffer.writeString(recipe.group);
			recipe.firstIngredient.write(buffer);
			recipe.secondIngredient.write(buffer);
			buffer.writeItemStack(recipe.result);
		}

		public interface IRecipeFactory<T extends ForgeringTableRecipes> {
			T create(ResourceLocation id, String p_create_2_, Ingredient firstIngredient, Ingredient secondIngredient,
					ItemStack result);
		}
	}
}
