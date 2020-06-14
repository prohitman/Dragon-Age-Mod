package com.prohitman.dragonage.blocks;

import java.util.Random;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
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

public class CandleBlock extends Block {
	public static final IntegerProperty CANDLES = IntegerProperty.create("candles", 1, 3);

	protected static final VoxelShape ONE_CANDLE = Stream
			.of(Block.makeCuboidShape(7.58668, 5.00314, 7.90375, 8.58668, 7.00314, 7.90375),
					Block.makeCuboidShape(7.59654, 5.00314, 7.90329, 8.59654, 7.00314, 7.90329),
					Block.makeCuboidShape(6.5, 0, 6.5, 9.5, 5, 9.5))
			.reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();

	protected static final VoxelShape TWO_CANDLES = Stream.of(Block.makeCuboidShape(3.5, 0, 4.5, 6.5, 5, 7.5),
			Block.makeCuboidShape(6.87957, 5.00314, 4.36822, 7.87957, 7.00314, 4.36822),
			Block.makeCuboidShape(5.68773, 7, 9.96, 6.68773, 9, 9.96),
			Block.makeCuboidShape(4.06101, 5.00314, 8.6104, 5.06101, 7.00314, 8.6104),
			Block.makeCuboidShape(9.83397, 7, 9.17772, 10.83397, 9, 9.17772), Block.makeCuboidShape(6, 0, 9, 9, 7, 12))
			.reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();

	protected static final VoxelShape THREE_CANDLES = Stream.of(Block.makeCuboidShape(3.5, 0, 4.5, 6.5, 5, 7.5),
			Block.makeCuboidShape(
					6.8795732188134515, 5.00314, 4.368216094067261, 7.8795732188134515, 7.00314, 4.368216094067261),
			Block.makeCuboidShape(5.687733230091756, 7, 9.96, 6.687733230091756, 9, 9.96),
			Block.makeCuboidShape(4.061006094067261, 5.00314, 8.610396781186548, 5.06100609406726, 7.00314,
					8.610396781186548),
			Block.makeCuboidShape(9.83396895112973, 7, 9.1777200066867, 10.833968951129728, 9, 9.1777200066867),
			Block.makeCuboidShape(6, 0, 9, 9, 7, 12), Block.makeCuboidShape(9.5, 0, 5.5, 12.5, 6, 8.5),
			Block.makeCuboidShape(10.055456351736995, 6, 5.6767766952966365, 11.055456351736995, 8, 5.6767766952966365),
			Block.makeCuboidShape(10.896446609406727, 6, 6.353553390593274, 10.896446609406727, 8, 7.353553390593274))
			.reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();

	public CandleBlock(Block.Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(CANDLES, Integer.valueOf(1)));
	}

	@SuppressWarnings("deprecation")
	public int getLightValue(BlockState state) {
		return super.getLightValue(state) + state.get(CANDLES);
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(CANDLES);
	}

	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState blockstate = context.getWorld().getBlockState(context.getPos());
		if (blockstate.getBlock() == this) {
			return blockstate.with(CANDLES, Integer.valueOf(Math.min(3, blockstate.get(CANDLES) + 1)));
		}
		return super.getStateForPlacement(context);
	}

	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch (state.get(CANDLES)) {
		case 1:
		default:
			return ONE_CANDLE;
		case 2:
			return TWO_CANDLES;
		case 3:
			return THREE_CANDLES;
		}
	}

	@SuppressWarnings("deprecation")
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
			BlockPos currentPos, BlockPos facingPos) {
		return func_220277_j(stateIn).getOpposite() == facing && !stateIn.isValidPosition(worldIn, currentPos)
				? Blocks.AIR.getDefaultState()
				: super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		Direction direction = func_220277_j(state).getOpposite();
		return Block.hasEnoughSolidSide(worldIn, pos.offset(direction), direction.getOpposite());
	}

	protected static Direction func_220277_j(BlockState p_220277_0_) {
		return Direction.UP;
	}

	public PushReaction getPushReaction(BlockState state) {
		return PushReaction.DESTROY;
	}

	@SuppressWarnings("deprecation")
	public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
		return useContext.getItem().getItem() == this.asItem() && state.get(CANDLES) < 3 ? true
				: super.isReplaceable(state, useContext);
	}

	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (stateIn.get(CANDLES) == 1) {
			double d0 = (double) pos.getX() + 0.5D;
			double d1 = (double) pos.getY() + 0.55D;
			double d2 = (double) pos.getZ() + 0.5D;
			worldIn.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
			worldIn.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}
		
		else if(stateIn.get(CANDLES) > 1)
		{
			double d0 = (double) pos.getX() + 0.4633D;
			double d1 = (double) pos.getY() + 0.65D;
			double d2 = (double) pos.getZ() + 0.65D;
			worldIn.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
			worldIn.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
			
			double d3 = (double) pos.getX() + 0.3225D;
			double d4 = (double) pos.getY() + 0.55D;
			double d5 = (double) pos.getZ() + 0.375D;
			worldIn.addParticle(ParticleTypes.SMOKE, d3, d4, d5, 0.0D, 0.0D, 0.0D);
			worldIn.addParticle(ParticleTypes.FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D);
			
			if (stateIn.get(CANDLES) == 3)
			{
				double d6 = (double) pos.getX() + 0.68D;
				double d7 = (double) pos.getY() + 0.575D;
				double d8 = (double) pos.getZ() + 0.425D;
				worldIn.addParticle(ParticleTypes.SMOKE, d6, d7, d8, 0.0D, 0.0D, 0.0D);
				worldIn.addParticle(ParticleTypes.FLAME, d6, d7, d8, 0.0D, 0.0D, 0.0D);
			}
		}
		

	}
}
