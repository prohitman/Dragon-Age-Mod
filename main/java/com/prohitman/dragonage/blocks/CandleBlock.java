package com.prohitman.dragonage.blocks;

import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.PushReaction;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CandleBlock extends Block
{
	protected static final VoxelShape SHAPE = Stream.of(
			Block.makeCuboidShape(7.58668, 5.00314, 7.90375, 8.58668, 7.00314, 7.90375),
			Block.makeCuboidShape(7.59654, 5.00314, 7.90329, 8.59654, 7.00314, 7.90329),
			Block.makeCuboidShape(6.5, 0, 6.5, 9.5, 5, 9.5)
			).reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);}).get();

	public CandleBlock(Block.Properties properties) 
	{
		super(properties);
	}
	
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) 
	{
	      return SHAPE;
	}
	
	@SuppressWarnings("deprecation")
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
	      return facing == Direction.DOWN && !this.isValidPosition(stateIn, worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	   }
	
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) 
	{
		return hasEnoughSolidSide(worldIn, pos.down(), Direction.UP);
	}
	
	public PushReaction getPushReaction(BlockState state) 
	{
	      return PushReaction.DESTROY;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) 
	{
      double d0 = (double)pos.getX() + 0.5D;
      double d1 = (double)pos.getY() + 0.55D;
      double d2 = (double)pos.getZ() + 0.5D;
      worldIn.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
      worldIn.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
	}
}
