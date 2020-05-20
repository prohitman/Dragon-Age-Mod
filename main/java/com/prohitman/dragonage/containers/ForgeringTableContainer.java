package com.prohitman.dragonage.containers;

import java.util.Optional;

import com.prohitman.dragonage.init.ModBlocks;
import com.prohitman.dragonage.init.ModContainerTypes;
import com.prohitman.dragonage.recipes.ForgeringTableRecipes;
import com.prohitman.dragonage.recipes.IModRecipeTypes;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ForgeringTableContainer extends RecipeBookContainer<CraftingInventory> {
	private final CraftResultInventory outputSlot = new CraftResultInventory();
	private final CraftingInventory inputSlots = new CraftingInventory(this, 2, 1);
	private final IWorldPosCallable canInteractwithCallable;
	private final PlayerEntity player;

	public ForgeringTableContainer(final int windowid, final PlayerInventory playerInventory, IWorldPosCallable pos) {
		super(ModContainerTypes.FORGERING_TABLE_CONTAINER.get(), windowid);
		this.canInteractwithCallable = pos;
		this.player = playerInventory.player;

		// FT's inventory
		this.addSlot(new Slot(this.inputSlots, 0, 27, 47));
		this.addSlot(new Slot(this.inputSlots, 1, 76, 47));
		this.addSlot(new CraftingResultSlot(playerInventory.player, this.inputSlots, this.outputSlot, 2, 134, 47));

		// Main Inventory
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		// HotBar
		for (int k = 0; k < 9; ++k) {
			this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
		}

	}

	protected static void updatecraftmatrix(int id, World world, PlayerEntity player, CraftingInventory inventory,
			CraftResultInventory result) {
		if (!world.isRemote) {
			ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) player;
			ItemStack itemstack = ItemStack.EMPTY;
			Optional<ForgeringTableRecipes> optional = world.getServer().getRecipeManager()
					.getRecipe(IModRecipeTypes.FORGERING, inventory, world);
			if (optional.isPresent()) {
				ForgeringTableRecipes icraftingrecipe = optional.get();
				if (result.canUseRecipe(world, serverplayerentity, icraftingrecipe)) {
					itemstack = icraftingrecipe.getCraftingResult(inventory);
				}
			}

			result.setInventorySlotContents(2, itemstack);
			serverplayerentity.connection.sendPacket(new SSetSlotPacket(id, 2, itemstack));
		}
	}

	public ForgeringTableContainer(final int windowid, final PlayerInventory playerinv, PacketBuffer data) {
		this(windowid, playerinv, IWorldPosCallable.DUMMY);
	}

	public void onCraftMatrixChanged(IInventory inventoryIn) {
		this.canInteractwithCallable.consume((world, pos) -> {
			updatecraftmatrix(this.windowId, world, this.player, (CraftingInventory) this.inputSlots, this.outputSlot);
		});
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return isWithinUsableDistance(canInteractwithCallable, playerIn, ModBlocks.FORGERING_TABLE.get());
	}

//	private static ForgeringTableTileEntity getTileEntity(final PlayerInventory playerInventory,
//			final PacketBuffer data) {
//		Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
//		Objects.requireNonNull(data, "data cannot be null");
//		final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
//		if (tileAtPos instanceof ForgeringTableTileEntity) {
//			return (ForgeringTableTileEntity) tileAtPos;
//		}
//
//		throw new IllegalStateException("Tile Entity is not correct" + tileAtPos);
//	}

	@Override
	public void fillStackedContents(RecipeItemHelper itemHelperIn) {
		this.inputSlots.fillStackedContents(itemHelperIn);
	}

	@Override
	public void clear() {
		this.inputSlots.clear();
		this.outputSlot.clear();
	}

	@Override
	public boolean matches(IRecipe<? super CraftingInventory> recipeIn) {
		return recipeIn.matches(this.inputSlots, this.player.world);
	}

	@Override
	public int getOutputSlot() {
		return 2;// this.outputSlot.getSizeInventory();
	}

	@Override
	public int getWidth() {
		return this.inputSlots.getWidth();
	}

	@Override
	public int getHeight() {
		return this.inputSlots.getHeight();
	}

	@OnlyIn(Dist.CLIENT)
	public int getSize() {
		return 3;
	}

	/**
	 * Called to determine if the current slot is valid for the stack merging
	 * (double-click) code. The stack passed in is null for the initial slot that
	 * was double-clicked.
	 */
	public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
		return slotIn.inventory != this.outputSlot && super.canMergeSlot(stack, slotIn);
	}

	/**
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this
	 * moves the stack between the player inventory and the other inventory(s).
	 */
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (index == 2) {
				if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.onSlotChange(itemstack1, itemstack);
			} else if (index != 0 && index != 1) {
				if (index >= 3 && index < 39 && !this.mergeItemStack(itemstack1, 0, 2, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, itemstack1);
		}

		return itemstack;
	}

	/**
	 * Called when the container is closed.
	 */
	public void onContainerClosed(PlayerEntity playerIn) {
		super.onContainerClosed(playerIn);
		this.canInteractwithCallable.consume((world, blockpos) -> {
			this.clearContainer(playerIn, world, this.inputSlots);
		});
	}
}
