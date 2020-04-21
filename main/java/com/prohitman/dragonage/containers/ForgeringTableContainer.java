package com.prohitman.dragonage.containers;

import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.prohitman.dragonage.init.ModBlocks;
import com.prohitman.dragonage.init.ModContainerTypes;
import com.prohitman.dragonage.recipes.ForgeringTableRecipes;
import com.prohitman.dragonage.recipes.IModRecipeTypes;
import com.prohitman.dragonage.tileentities.ForgeringTableTileEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ForgeringTableContainer extends RecipeBookContainer<IInventory>
{
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();
	private final IInventory outputSlot = new CraftResultInventory();
	private final IInventory inputSlots = new Inventory(2);
	private final IWorldPosCallable canInteractwithCallable;
	@SuppressWarnings("unused")
	private final ForgeringTableTileEntity tileEntity;
	private final PlayerEntity player;

	public ForgeringTableContainer(final int windowid, final PlayerInventory playerInventory, final ForgeringTableTileEntity tileEntity)
	{
		super(ModContainerTypes.FORGERING_TABLE_CONTAINER.get(), windowid);
		this.tileEntity = tileEntity;
		this.canInteractwithCallable = IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos());
		this.player = playerInventory.player;
		
		//FT's inventory
		this.addSlot(new Slot(this.inputSlots, 0, 27, 47));
		this.addSlot(new Slot(this.inputSlots, 1, 76, 47));
		this.addSlot(new Slot(this.outputSlot, 2, 134, 47) {
			
	         public boolean isItemValid(ItemStack stack) {
	            return false;
	         }

	         /**
	          * Return whether this slot's stack can be taken from this slot.
	          */
	         public boolean canTakeStack(PlayerEntity playerIn) {
	            return true;
	         }
	         
		});
		
		//Main Inventory
		for(int i = 0; i < 3; ++i) {
	         for(int j = 0; j < 9; ++j) 
	         {
	            this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
	         }
	      }
		
		//HotBar
	      for(int k = 0; k < 9; ++k) 
	      {
	         this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
	      }
		
	}
	
	protected static void func_217066_a(int p_217066_0_, World world, PlayerEntity player, IInventory inventory, CraftResultInventory result) {
		if (!world.isRemote) {
			ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)player;
			ItemStack itemstack = ItemStack.EMPTY;
			Optional<ForgeringTableRecipes> optional = world.getServer().getRecipeManager().getRecipe(IModRecipeTypes.FORGERING, inventory, world);
			if (optional.isPresent()) {
			ForgeringTableRecipes icraftingrecipe = optional.get();
        	 if (result.canUseRecipe(world, serverplayerentity, icraftingrecipe)) {
        		 itemstack = icraftingrecipe.getCraftingResult(inventory);
        	 }
			}

			result.setInventorySlotContents(0, itemstack);
			serverplayerentity.connection.sendPacket(new SSetSlotPacket(p_217066_0_, 0, itemstack));
		}
	}
	
	public ForgeringTableContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) 
	{
		this(windowId, playerInventory, getTileEntity(playerInventory, data));
	}
	
	public void onCraftMatrixChanged(IInventory inventoryIn) {
	      this.canInteractwithCallable.consume((p_217069_1_, p_217069_2_) -> {
	         func_217066_a(this.windowId, p_217069_1_, this.player, this.inputSlots, (CraftResultInventory)this.outputSlot);
	      });
	   }
	
	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return isWithinUsableDistance(canInteractwithCallable, playerIn, ModBlocks.FORGERING_TABLE.get());
	}
	
	private static ForgeringTableTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data)
	{
		Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
		Objects.requireNonNull(data, "data cannot be null");
		final TileEntity tileAtPos = playerInventory.player.world
				.getTileEntity(data.readBlockPos());
		if(tileAtPos instanceof ForgeringTableTileEntity)
		{
			return (ForgeringTableTileEntity)tileAtPos;
		}
		
		throw new IllegalStateException("Tile Entity is not correct" + tileAtPos);
	}

	@Override
	public void fillStackedContents(RecipeItemHelper itemHelperIn) {
		if (this.inputSlots instanceof IRecipeHelperPopulator) {
	         ((IRecipeHelperPopulator)this.inputSlots).fillStackedContents(itemHelperIn);
	      }
	}

	@Override
	public void clear() {
		this.inputSlots.clear();
		this.outputSlot.clear();
	}

	@Override
	public boolean matches(IRecipe<? super IInventory> recipeIn) {
		return recipeIn.matches(this.inputSlots, this.player.world);
	}

	@Override
	public int getOutputSlot() {
		return 1;
	}

	@Override
	public int getWidth() {
		return 1;
	}

	@Override
	public int getHeight() {
		return 1;
	}

	@OnlyIn(Dist.CLIENT)
	public int getSize() 
	{
		return 3;
	}
	
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
	            if (!this.mergeItemStack(itemstack1, 10, 46, true)) {
	               return ItemStack.EMPTY;
	            }

	            slot.onSlotChange(itemstack1, itemstack);
	         } else if (index >= 10 && index < 46) {
	            if (!this.mergeItemStack(itemstack1, 1, 10, false)) {
	               if (index < 37) {
	                  if (!this.mergeItemStack(itemstack1, 37, 46, false)) {
	                     return ItemStack.EMPTY;
	                  }
	               } else if (!this.mergeItemStack(itemstack1, 10, 37, false)) {
	                  return ItemStack.EMPTY;
	               }
	            }
	         } else if (!this.mergeItemStack(itemstack1, 10, 46, false)) {
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
}
