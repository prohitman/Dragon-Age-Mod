package com.prohitman.dragonage.containers;

import java.util.Optional;

import com.prohitman.dragonage.init.ModBlocks;
import com.prohitman.dragonage.init.ModContainerTypes;
import com.prohitman.dragonage.recipes.ForgingTableRecipes;
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
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;

public class ForgingTableContainer extends RecipeBookContainer<CraftingInventory> {
	private final CraftResultInventory outputSlot = new CraftResultInventory();
	private final CraftingInventory inputSlots = new CraftingInventory(this, 2, 1);
	private final IWorldPosCallable canInteractwithCallable;
	private final PlayerEntity player;
	private long lastOnTake;

	public ForgingTableContainer(final int windowid, final PlayerInventory playerinv, final PacketBuffer data) {
		this(windowid, playerinv, IWorldPosCallable.DUMMY);
	}

	public ForgingTableContainer(final int windowid, final PlayerInventory playerInventory, IWorldPosCallable pos) {
		super(ModContainerTypes.FORGING_TABLE_CONTAINER.get(), windowid);
		this.canInteractwithCallable = pos;
		this.player = playerInventory.player;

		// FT's inventory
		this.addSlot(new CraftingResultSlot(playerInventory.player, this.inputSlots, this.outputSlot, 0, 124, 59) {
			@Override
			public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
				this.onCrafting(stack);
				ForgeHooks.setCraftingPlayer(thePlayer);
				NonNullList<ItemStack> nonnulllist = thePlayer.world.getRecipeManager().getRecipeNonNull(
						IModRecipeTypes.FORGING, ForgingTableContainer.this.inputSlots, thePlayer.world);
				ForgeHooks.setCraftingPlayer(null);
				for (int i = 0; i < nonnulllist.size(); ++i) {
					ItemStack itemstack = ForgingTableContainer.this.inputSlots.getStackInSlot(i);
					ItemStack itemstack1 = nonnulllist.get(i);
					if (!itemstack.isEmpty()) {
						ForgingTableContainer.this.inputSlots.decrStackSize(i, 1);
						itemstack = ForgingTableContainer.this.inputSlots.getStackInSlot(i);
					}

					if (!itemstack1.isEmpty()) {
						if (itemstack.isEmpty()) {
							ForgingTableContainer.this.inputSlots.setInventorySlotContents(i, itemstack1);
						} else if (ItemStack.areItemsEqual(itemstack, itemstack1)
								&& ItemStack.areItemStackTagsEqual(itemstack, itemstack1)) {
							itemstack1.grow(itemstack.getCount());
							ForgingTableContainer.this.inputSlots.setInventorySlotContents(i, itemstack1);
						} else if (!ForgingTableContainer.this.player.inventory.addItemStackToInventory(itemstack1)) {
							ForgingTableContainer.this.player.dropItem(itemstack1, false);
						}
					}
				}

				stack.getItem().onCreated(stack, thePlayer.world, thePlayer);
				ForgingTableContainer.this.canInteractwithCallable.consume((world, blockpos) -> {
					long l = world.getGameTime();
					if (ForgingTableContainer.this.lastOnTake != l) {
						world.playSound((PlayerEntity) null, blockpos, SoundEvents.BLOCK_ANVIL_USE,
								SoundCategory.BLOCKS, 1.0F, 1.0F);
						ForgingTableContainer.this.lastOnTake = l;
					}

				});

				return stack;
			}
		});
		this.addSlot(new Slot(this.inputSlots, 0, 25, 59));
		this.addSlot(new Slot(this.inputSlots, 1, 66, 59));

		// Main Inventory
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 108 + i * 18));
			}
		}

		// HotBar
		for (int k = 0; k < 9; ++k) {
			this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 166));
		}

	}

	protected static void updatecraftmatrix(int id, World world, PlayerEntity player, CraftingInventory inventory,
			CraftResultInventory result) {
		if (!world.isRemote) {
			ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) player;
			ItemStack itemstack = ItemStack.EMPTY;
			Optional<ForgingTableRecipes> optional = world.getServer().getRecipeManager()
					.getRecipe(IModRecipeTypes.FORGING, inventory, world);
			if (optional.isPresent()) {
				ForgingTableRecipes icraftingrecipe = optional.get();
				if (result.canUseRecipe(world, serverplayerentity, icraftingrecipe)) {
					itemstack = icraftingrecipe.getCraftingResult(inventory);
				}
			}

			result.setInventorySlotContents(0, itemstack);
			serverplayerentity.connection.sendPacket(new SSetSlotPacket(id, 0, itemstack));
		}
	}

	public void onCraftMatrixChanged(IInventory inventoryIn) {
		this.canInteractwithCallable.consume((world, pos) -> {
			updatecraftmatrix(this.windowId, world, this.player, this.inputSlots, this.outputSlot);
		});

	}

	public boolean canInteractWith(PlayerEntity playerIn) {
		return isWithinUsableDistance(canInteractwithCallable, playerIn, ModBlocks.FORGING_TABLE.get());
	}

	public void fillStackedContents(RecipeItemHelper itemHelperIn) {
		this.inputSlots.fillStackedContents(itemHelperIn);
	}

	public void clear() {
		this.inputSlots.clear();
		this.outputSlot.clear();
	}

	public boolean matches(IRecipe<? super CraftingInventory> recipeIn) {
		return recipeIn.matches(this.inputSlots, this.player.world);
	}

	public int getOutputSlot() {
		return 0;// this.outputSlot.getSizeInventory();
	}

	public int getWidth() {
		return this.inputSlots.getWidth();
	}

	public int getHeight() {
		return this.inputSlots.getHeight();
	}

	@OnlyIn(Dist.CLIENT)
	public int getSize() {
		return 3;
	}

	/*
	 * Called to determine if the current slot is valid for the stack merging
	 * (double-click) code. The stack passed in is null for the initial slot that
	 * was double-clicked.
	 */
	public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
		return slotIn.inventory != this.outputSlot && super.canMergeSlot(stack, slotIn);
	}

	/*
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this
	 * moves the stack between the player inventory and the other inventory(s).
	 */
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (index == 0) {
				this.canInteractwithCallable.consume((p_217067_2_, p_217067_3_) -> {
					itemstack1.getItem().onCreated(itemstack1, p_217067_2_, playerIn);
				});
				if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.onSlotChange(itemstack1, itemstack);
			} else if (index >= 3 && index < 39) {
				if (!this.mergeItemStack(itemstack1, 1, 10, false)) {
					if (index < 37) {
						if (!this.mergeItemStack(itemstack1, 35, 39, false)) {
							return ItemStack.EMPTY;
						}
					} else if (!this.mergeItemStack(itemstack1, 3, 35, false)) {
						return ItemStack.EMPTY;
					}
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

			ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);
			if (index == 0) {
				playerIn.dropItem(itemstack2, false);
			}
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
