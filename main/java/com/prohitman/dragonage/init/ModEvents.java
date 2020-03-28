package com.prohitman.dragonage.init;

import java.util.List;
import java.util.Optional;

import com.prohitman.dragonage.DragonAge;
import com.prohitman.dragonage.network.DragonAgePacketHandler;
import com.prohitman.dragonage.network.MessageExtendedReachAttack;
import com.prohitman.dragonage.util.IExtendedReach;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = DragonAge.MOD_ID, bus = Bus.FORGE)
public class ModEvents 
{
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public static void onEvent(InputEvent event)
	{
		
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player != null)
		{
			if (player.getHeldItemMainhand().getItem() instanceof IExtendedReach)
			{
				if (((IExtendedReach)player.getHeldItemMainhand().getItem()).getReach() > 5.0f)
				{
					GameSettings GS = Minecraft.getInstance().gameSettings;
					if (GS.keyBindAttack.isPressed())
					{
						extendAttackReach(Minecraft.getInstance().player);
					}
				}
			}
		}
	}
	
	private static void extendAttackReach(ClientPlayerEntity thePlayer)
	{
        if (!thePlayer.isRowingBoat())
        {
        	ItemStack itemstack = thePlayer.getHeldItemMainhand();           
            if (itemstack.getItem() instanceof IExtendedReach)
            {
            	IExtendedReach ieri = (IExtendedReach) itemstack.getItem();
                float reach = ieri.getReach();
                getMouseOverExtended2(reach);
                if (Minecraft.getInstance().objectMouseOver != null)
                {
                    switch (Minecraft.getInstance().objectMouseOver.getType())
                    {
                    	case ENTITY:
	                    {
	                    	DragonAgePacketHandler.HANDLER.sendToServer(new MessageExtendedReachAttack(((EntityRayTraceResult)Minecraft.getInstance().objectMouseOver).getEntity().getEntityId()));
	                    	Entity entityraytraceresult = ((EntityRayTraceResult)Minecraft.getInstance().objectMouseOver).getEntity();
	                    	if (!thePlayer.isSpectator())
	                    	{
	                    		thePlayer.attackTargetEntityWithCurrentItem(entityraytraceresult);
	                    		thePlayer.resetCooldown();
	                    	}
	                    	break;
	                    }
                    	case BLOCK:
                            //Only extends attack reach, not breaking reach
                    		 BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)Minecraft.getInstance().objectMouseOver;
                             BlockPos blockpos = blockraytraceresult.getPos();
						if (!Minecraft.getInstance().world.isAirBlock(blockpos) /* && thePlayer.pos.getDistanceSq(blockpos) <= 25 */)
                            {
                            	Minecraft.getInstance().playerController.clickBlock(blockpos, blockraytraceresult.getFace());
                                break;
                            }

                        case MISS:

                            if (Minecraft.getInstance().playerController.isNotCreative())
                            {
                                //this.leftClickCounter = 10;
                            }

                            thePlayer.resetCooldown();
                            net.minecraftforge.common.ForgeHooks.onEmptyLeftClick(thePlayer);
                    }

                    thePlayer.swingArm(Hand.MAIN_HAND);
                }
            }
        }
    }
	
	public static void getMouseOverExtended2(float dist)
	{
		Entity entity = Minecraft.getInstance().getRenderViewEntity();
		
        if (entity != null)
        {
            if (Minecraft.getInstance().world != null)
            {
                Minecraft.getInstance().getProfiler().startSection("pick");
                double d0 = dist;
                Minecraft.getInstance().objectMouseOver = entity.pick(d0, 1.0F, false);
                Vec3d vec3d = entity.getEyePosition(1.0F);
                boolean flag = false;
                double d1 = d0;
                
                {
                    if (d0 > 3.0D)
                    {
                        flag = true;
                    }
                }

                if (Minecraft.getInstance().objectMouseOver != null)
                {
                    d1 = Minecraft.getInstance().objectMouseOver.getHitVec().squareDistanceTo(vec3d);
                }

                Vec3d vec3d1 = entity.getLook(1.0F);
                Vec3d vec3d2 = vec3d.add(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
                Entity pointedEntity = null;
                Vec3d vec3d3 = null;
                List<Entity> list = Minecraft.getInstance().world.getEntitiesInAABBexcluding(entity, entity.getBoundingBox().expand(vec3d1.scale(d0)).grow(1.0D, 1.0D, 1.0D), (p_213489_0_) -> 
            	{
          	      return !p_213489_0_.isSpectator() && p_213489_0_.canBeCollidedWith();
            	});
                
                double d2 = d1;

                for (int j = 0; j < list.size(); ++j)
                {
                    Entity entity1 = list.get(j);
                    AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow((double)entity1.getCollisionBorderSize());
                    Optional<Vec3d> optional = axisalignedbb.rayTrace(vec3d, vec3d2);
                    Vec3d hitvec = new EntityRayTraceResult(pointedEntity, vec3d).getHitVec();
                    		/*ProjectileHelper.rayTraceEntities(entity1, vec3d, vec3d2, axisalignedbb, (p_215312_0_) -> {
 		               return !p_215312_0_.isSpectator() && p_215312_0_.canBeCollidedWith();
 		            }, d1);*/

                    if (axisalignedbb.contains(vec3d))
                    {
                        if (d2 >= 0.0D)
                        {
                            pointedEntity = entity1;
                            vec3d3 = optional == null ? vec3d : hitvec;
                            d2 = 0.0D;
                        }
                    }
                    else if (optional.isPresent())
                    {
                        double d3 = vec3d.squareDistanceTo(hitvec);//distanceTo(hitvec);

                        if (d3 < d2 || d2 == 0.0D)
                        {
                            if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity() && !entity1.canRiderInteract())
                            {
                                if (d2 == 0.0D)
                                {
                                    pointedEntity = entity1;
                                    vec3d3 = hitvec;
                                }
                            }
                            else
                            {
                                pointedEntity = entity1;
                                vec3d3 = hitvec;
                                d2 = d3;
                            }
                        }
                    }
                }

                if (pointedEntity != null && flag && vec3d.squareDistanceTo(vec3d3)/*distanceTo(vec3d3)*/ > dist)
                {
                    pointedEntity = null;
                    Minecraft.getInstance().objectMouseOver = BlockRayTraceResult.createMiss(vec3d3, Direction.getFacingFromVector(vec3d1.x, vec3d1.y, vec3d1.z), new BlockPos(vec3d3));
                }      
                
                if (pointedEntity != null && (d2 < d1 || Minecraft.getInstance().objectMouseOver == null))
                {
                	Minecraft.getInstance().objectMouseOver = new EntityRayTraceResult(pointedEntity, vec3d3);

                    if (pointedEntity instanceof LivingEntity || pointedEntity instanceof ItemFrameEntity)
                    {
                        Minecraft.getInstance().pointedEntity = pointedEntity;
                    }
                }

                Minecraft.getInstance().getProfiler().endSection();
            }
        }
	}
}
