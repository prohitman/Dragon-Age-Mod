package com.prohitman.dragonage.items;

import com.google.common.collect.Multimap;
import com.prohitman.dragonage.util.IExtendedReach;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HalberdItem extends TieredItem implements IExtendedReach
{
	private final float attackDamage;
	private final float attackSpeed;
	private final float attackReach;
	
	public HalberdItem(IItemTier tierIn, float attackDamageIn, float attackSpeedIn, float attackReachIn, Item.Properties builder) 
	{
		super(tierIn, builder);
		this.attackDamage = (float)attackDamageIn + tierIn.getAttackDamage();
		this.attackSpeed = attackSpeedIn;
		this.attackReach = attackReachIn;
	}
	
	public float getAttackDamage() 
	{
		return this.attackDamage;
	}
	
	@Override
	public float getReach()
	{
		return this.attackReach;
	}
	
	public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
	      return !player.isCreative();
	   }

	   public float getDestroySpeed(ItemStack stack, BlockState state) {
	      @SuppressWarnings("unused")
		Block block = state.getBlock();
	      if (state.isIn(BlockTags.LEAVES)) {
	         return 15.0F;
	      } else {
	         Material material = state.getMaterial();
	         return material != Material.PLANTS && material != Material.TALL_PLANTS && material != Material.CORAL && !state.isIn(BlockTags.LEAVES) && material != Material.GOURD ? 1.0F : 1.5F;
	      }
	   }

	   /**
	    * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
	    * the damage on the stack.
	    */
	   public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
	      stack.damageItem(1, attacker, (p_220045_0_) -> {
	         p_220045_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
	      });
	      return true;
	   }

	   /**
	    * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
	    */
	   public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
	      if (state.getBlockHardness(worldIn, pos) != 0.0F) {
	         stack.damageItem(2, entityLiving, (p_220044_0_) -> {
	            p_220044_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
	         });
	      }

	      return true;
	   }

	   /**
	    * Check whether this Item can harvest the given Block
	    */
	   public boolean canHarvestBlock(BlockState blockIn) {
	      return blockIn.getBlock() == Blocks.COBWEB;
	   }

	   /**
	    * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
	    */
	   @Override
	   public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
	      @SuppressWarnings("deprecation")
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot);
	      if (equipmentSlot == EquipmentSlotType.MAINHAND) {
	         multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
	         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double)this.attackSpeed, AttributeModifier.Operation.ADDITION));
	      }

	      return multimap;
	   }
}
