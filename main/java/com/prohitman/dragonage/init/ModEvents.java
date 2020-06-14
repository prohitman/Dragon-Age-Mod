package com.prohitman.dragonage.init;

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
import net.minecraft.entity.projectile.ProjectileHelper;
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
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = DragonAge.MOD_ID, bus = Bus.FORGE)
public class ModEvents {
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public static void onEvent(InputEvent event) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player != null) {
			if (player.getHeldItemMainhand().getItem() instanceof IExtendedReach) {
				if (((IExtendedReach) player.getHeldItemMainhand().getItem()).getReach() > 5.0f) {
					GameSettings GS = Minecraft.getInstance().gameSettings;
					if (GS.keyBindAttack.isPressed()) {
						extendAttackReach(Minecraft.getInstance().player);
					}
				}
			}
		}
	}

	private static void extendAttackReach(ClientPlayerEntity thePlayer) {
		if (!thePlayer.isRowingBoat()) {
			ItemStack itemstack = thePlayer.getHeldItemMainhand();
			if (itemstack.getItem() instanceof IExtendedReach) {
				IExtendedReach ieri = (IExtendedReach) itemstack.getItem();
				float reach = ieri.getReach();
				getMouseOverExtended2(reach);
				InputEvent.ClickInputEvent inputEvent = ForgeHooksClient.onClickInput(0, Minecraft.getInstance().gameSettings.keyBindAttack, Hand.MAIN_HAND);
				if (!inputEvent.isCanceled()) {
					switch (Minecraft.getInstance().objectMouseOver.getType()) {
					case ENTITY: {
						DragonAgePacketHandler.HANDLER.sendToServer(new MessageExtendedReachAttack(
								((EntityRayTraceResult) Minecraft.getInstance().objectMouseOver).getEntity()
										.getEntityId()));
						if (!thePlayer.isSpectator()) {
							Minecraft.getInstance().playerController.attackEntity(thePlayer,
									((EntityRayTraceResult) Minecraft.getInstance().objectMouseOver).getEntity());
							thePlayer.resetCooldown();
						}
						break;
					}
					case BLOCK:
						// Only extends attack reach, not breaking reach
						BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult) Minecraft
								.getInstance().objectMouseOver;
						BlockPos blockpos = blockraytraceresult.getPos();
						if (!Minecraft.getInstance().world
								.isAirBlock(blockpos) /* && thePlayer.pos.getDistanceSq(blockpos) <= 25 */) {
							Minecraft.getInstance().playerController.clickBlock(blockpos,
									blockraytraceresult.getFace());
							break;
						}

					case MISS:

						if (Minecraft.getInstance().playerController.isNotCreative()) {
							// this.leftClickCounter = 10;
						}

						thePlayer.resetCooldown();
						ForgeHooks.onEmptyLeftClick(thePlayer);
					}
					if (inputEvent.shouldSwingHand())
						thePlayer.swingArm(Hand.MAIN_HAND);

				}
			}
		}
	}

	public static void getMouseOverExtended2(float dist) {
		Entity entity = Minecraft.getInstance().getRenderViewEntity();

		if (entity != null) {
			if (Minecraft.getInstance().world != null) {
				Minecraft.getInstance().getProfiler().startSection("pick");
				double d0 = dist;
				Minecraft.getInstance().objectMouseOver = entity.pick(d0, 1.0F, false);
				Vec3d vec3d = entity.getEyePosition(1.0F);
				boolean flag = false;
				double d1 = d0;

				
					if (d0 > 3.0D) {
						flag = true;
					}
				

				if (Minecraft.getInstance().objectMouseOver != null) {
					d1 = Minecraft.getInstance().objectMouseOver.getHitVec().squareDistanceTo(vec3d);
				}

				d1 = d1 * d1;

				Vec3d vec3d1 = entity.getLook(1.0F);
				Vec3d vec3d2 = vec3d.add(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
				AxisAlignedBB axisalignedbb = entity.getBoundingBox().expand(vec3d1.scale(d0)).grow(1.0D, 1.0D, 1.0D);
				EntityRayTraceResult entityraytraceresult = ProjectileHelper.rayTraceEntities(entity, vec3d, vec3d2,
						axisalignedbb, (Entity) -> {
							return !Entity.isSpectator() && Entity.canBeCollidedWith();
						}, d1);
				if (entityraytraceresult != null) {
					Entity pointedEntity = entityraytraceresult.getEntity();
					Vec3d vec3d3 = entityraytraceresult.getHitVec();
					double d2 = vec3d.squareDistanceTo(vec3d3);

					if (pointedEntity != null && flag && d2 > d1) {
						pointedEntity = null;
						Minecraft.getInstance().objectMouseOver = BlockRayTraceResult.createMiss(vec3d3,
								Direction.getFacingFromVector(vec3d1.x, vec3d1.y, vec3d1.z), new BlockPos(vec3d3));
					}

					else if (pointedEntity != null && (d2 < d1 || Minecraft.getInstance().objectMouseOver == null)) {
						Minecraft.getInstance().objectMouseOver = entityraytraceresult;

						if (pointedEntity instanceof LivingEntity || pointedEntity instanceof ItemFrameEntity) {
							Minecraft.getInstance().pointedEntity = pointedEntity;
						}
					}
				}

				Minecraft.getInstance().getProfiler().endSection();
			}
		}
	}
}
