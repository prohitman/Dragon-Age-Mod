//package com.prohitman.dragonage.tileentities;
//
//import java.util.Map;
//import java.util.Map.Entry;
//
//import javax.annotation.Nonnull;
//import javax.annotation.Nullable;
//
//import com.google.common.collect.Maps;
//import com.prohitman.dragonage.containers.ForgeringTableContainer;
//
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.entity.player.PlayerInventory;
//import net.minecraft.entity.player.ServerPlayerEntity;
//import net.minecraft.inventory.IInventory;
//import net.minecraft.inventory.IRecipeHelperPopulator;
//import net.minecraft.inventory.IRecipeHolder;
//import net.minecraft.inventory.ItemStackHelper;
//import net.minecraft.inventory.container.Container;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.crafting.IRecipe;
//import net.minecraft.item.crafting.RecipeItemHelper;
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.tileentity.LockableTileEntity;
//import net.minecraft.tileentity.TileEntityType;
//import net.minecraft.util.Direction;
//import net.minecraft.util.NonNullList;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.SoundCategory;
//import net.minecraft.util.SoundEvent;
//import net.minecraft.util.text.ITextComponent;
//import net.minecraft.util.text.TranslationTextComponent;
//import net.minecraft.world.GameRules;
//import net.minecraft.world.World;
//import net.minecraftforge.common.capabilities.Capability;
//import net.minecraftforge.common.util.LazyOptional;
//import net.minecraftforge.items.CapabilityItemHandler;
//import net.minecraftforge.items.IItemHandlerModifiable;
//import net.minecraftforge.items.wrapper.InvWrapper;
//
//public class ForgeringTableTileEntity extends LockableTileEntity implements IInventory, IRecipeHolder, IRecipeHelperPopulator {
//
//	protected NonNullList<ItemStack> contents = NonNullList.withSize(3, ItemStack.EMPTY);
//	private IItemHandlerModifiable items = createHandler();
//	private LazyOptional<IItemHandlerModifiable> itemHandler = LazyOptional.of(() -> items);
//	private final Map<ResourceLocation, Integer> recipesList = Maps.newHashMap();
//
//	public ForgeringTableTileEntity(TileEntityType<?> tileEntityTypeIn) {
//		super(tileEntityTypeIn);
//	}
//
////	public ForgeringTableTileEntity() {
////		this(ModTileEntityTypes.FORGERING_TABLE_TILE_ENTITY.get());
////	}
//
//	@Override
//	public int getSizeInventory() {
//		return this.contents.size();
//	}
//
//	@Override
//	public boolean isEmpty() {
//		for (ItemStack itemstack : this.contents) {
//			if (!itemstack.isEmpty()) {
//				return false;
//			}
//		}
//		return true;
//	}
//
//	@Override
//	public ItemStack getStackInSlot(int index) {
//		return this.contents.get(index);
//	}
//
//	@Override
//	public ItemStack decrStackSize(int index, int count) {
//		return ItemStackHelper.getAndSplit(this.contents, index, count);
//	}
//
//	@Override
//	public ItemStack removeStackFromSlot(int index) {
//		return ItemStackHelper.getAndRemove(this.contents, index);
//	}
//
//	@Override
//	public void setInventorySlotContents(int index, ItemStack stack) {
//		ItemStack itemstack = this.contents.get(index);
//		boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack)
//				&& ItemStack.areItemStackTagsEqual(stack, itemstack);
//		this.contents.set(index, stack);
//		if (stack.getCount() > this.getInventoryStackLimit()) {
//			stack.setCount(this.getInventoryStackLimit());
//		}
//
//		if (index == 0 && !flag) {
//			this.markDirty();
//		}
//	}
//
//	@Override
//	public boolean isUsableByPlayer(PlayerEntity player) {
//		if (this.world.getTileEntity(this.pos) != this) {
//			return false;
//		} else {
//			return player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D,
//					(double) this.pos.getZ() + 0.5D) <= 64.0D;
//		}
//	}
//
//	@Override
//	public void clear() {
//		this.contents.clear();
//	}
//
//	public boolean isItemValidForSlot(int index, ItemStack stack) {
//		if (index == 2) {
//			return false;
//		}
//		return true;
//	}
//
//	@Override
//	protected ITextComponent getDefaultName() {
//		return new TranslationTextComponent("container.forgering_table");
//	}
//
////	@Override
////	protected Container createMenu(int id, PlayerInventory player) {
////		return new ForgeringTableContainer(id, player, this);
////	}
//
//	@Override
//	public CompoundNBT write(CompoundNBT compound) {
//		super.write(compound);
//		ItemStackHelper.saveAllItems(compound, this.contents);
//		compound.putShort("RecipesUsedSize", (short) this.recipesList.size());
//		int i = 0;
//
//		for (Entry<ResourceLocation, Integer> entry : this.recipesList.entrySet()) {
//			compound.putString("RecipeLocation" + i, entry.getKey().toString());
//			compound.putInt("RecipeAmount" + i, entry.getValue());
//			++i;
//		}
//		return compound;
//	}
//
//	@Override
//	public void read(CompoundNBT compound) {
//		super.read(compound);
//		this.contents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
//		ItemStackHelper.loadAllItems(compound, this.contents);
//		int i = compound.getShort("RecipesUsedSize");
//
//		for (int j = 0; j < i; ++j) {
//			ResourceLocation resourcelocation = new ResourceLocation(compound.getString("RecipeLocation" + j));
//			int k = compound.getInt("RecipeAmount" + j);
//			this.recipesList.put(resourcelocation, k);
//		}
//	}
//
//	@SuppressWarnings("unused")
//	private void playsound(SoundEvent sound) {
//		double dx = (double) this.pos.getX() + 0.5D;
//		double dy = (double) this.pos.getY() + 0.5D;
//		double dz = (double) this.pos.getZ() + 0.5D;
//		this.world.playSound((PlayerEntity) null, dx, dy, dz, sound, SoundCategory.BLOCKS, 0.5F,
//				this.world.rand.nextFloat() * 0.1F + 0.9F);
//	}
//
//	@Override
//	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nonnull Direction side) {
//		if (!this.removed && side != null && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
//			return itemHandler.cast();
//		}
//
//		return super.getCapability(cap, side);
//	}
//
//	public IItemHandlerModifiable createHandler() {
//		return new InvWrapper(this);
//	}
//
//	@Override
//	public void remove() {
//		if (itemHandler != null) {
//			itemHandler.invalidate();
//		}
//		super.remove();
//	}
//
//	@Override
//	public void setRecipeUsed(@Nullable IRecipe<?> recipe) {
//		if (recipe != null) {
//			this.recipesList.compute(recipe.getId(), (resourceLocation, integer) -> {
//				return 1 + (integer == null ? 0 : integer);
//			});
//		}
//
//	}
//
//	@Override
//	public IRecipe<?> getRecipeUsed() {
//		return null;
//	}
//
//	@Override
//	public boolean canUseRecipe(World worldIn, ServerPlayerEntity player, IRecipe<?> recipe) {
//		if (!recipe.isDynamic() && worldIn.getGameRules().getBoolean(GameRules.DO_LIMITED_CRAFTING)) {
//			return false;
//		} else {
//			this.setRecipeUsed(recipe);
//			return true;
//		}
//	}
//
//	@Override
//	public void fillStackedContents(RecipeItemHelper helper) {
//		for (ItemStack itemstack : this.contents) {
//			helper.accountStack(itemstack);
//		}
//	}
//}
