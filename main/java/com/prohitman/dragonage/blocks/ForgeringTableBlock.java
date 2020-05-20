package com.prohitman.dragonage.blocks;

import java.util.stream.Stream;

import com.prohitman.dragonage.containers.ForgeringTableContainer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class ForgeringTableBlock extends Block {
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	private static final ITextComponent text_component = new TranslationTextComponent("container.forgering_table");

	public static final VoxelShape FACING_SOUTH = Stream.of(Block.makeCuboidShape(12, 0, 5, 14, 7, 7),
			Block.makeCuboidShape(2, 0, 5, 4, 7, 7), Block.makeCuboidShape(2, 0, 12, 4, 7, 14),
			Block.makeCuboidShape(12, 0, 12, 14, 7, 14), Block.makeCuboidShape(1, 7, 4, 15, 10, 15),
			Block.makeCuboidShape(11, 0, 1, 15, 16, 2), Block.makeCuboidShape(1, 0, 1, 5, 16, 2),
			Block.makeCuboidShape(0, 13, 2, 16, 15, 3), Block.makeCuboidShape(0, 8, 3.25, 16, 19, 3.25))
			.reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();

	public static final VoxelShape FACING_EAST = Stream.of(Block.makeCuboidShape(5, 0, 2, 7, 7, 4),
			Block.makeCuboidShape(5, 0, 12, 7, 7, 14), Block.makeCuboidShape(12, 0, 12, 14, 7, 14),
			Block.makeCuboidShape(12, 0, 2, 14, 7, 4), Block.makeCuboidShape(4, 7, 1, 15, 10, 15),
			Block.makeCuboidShape(1, 0, 1, 2, 16, 5), Block.makeCuboidShape(1, 0, 11, 2, 16, 15),
			Block.makeCuboidShape(2, 13, 0, 3, 15, 16), Block.makeCuboidShape(3.25, 8, 0, 3.25, 19, 16))
			.reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();

	public static final VoxelShape FACING_NORTH = Stream.of(Block.makeCuboidShape(2, 0, 9, 4, 7, 11),
			Block.makeCuboidShape(12, 0, 9, 14, 7, 11), Block.makeCuboidShape(12, 0, 2, 14, 7, 4),
			Block.makeCuboidShape(2, 0, 2, 4, 7, 4), Block.makeCuboidShape(1, 7, 1, 15, 10, 12),
			Block.makeCuboidShape(1, 0, 14, 5, 16, 15), Block.makeCuboidShape(11, 0, 14, 15, 16, 15),
			Block.makeCuboidShape(0, 13, 13, 16, 15, 14), Block.makeCuboidShape(0, 8, 12.75, 16, 19, 12.75))
			.reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();

	public static final VoxelShape FACING_WEST = Stream.of(Block.makeCuboidShape(9, 0, 12, 11, 7, 14),
			Block.makeCuboidShape(9, 0, 2, 11, 7, 4), Block.makeCuboidShape(2, 0, 2, 4, 7, 4),
			Block.makeCuboidShape(2, 0, 12, 4, 7, 14), Block.makeCuboidShape(1, 7, 1, 12, 10, 15),
			Block.makeCuboidShape(14, 0, 11, 15, 16, 15), Block.makeCuboidShape(14, 0, 1, 15, 16, 5),
			Block.makeCuboidShape(13, 13, 0, 14, 15, 16), Block.makeCuboidShape(12.75, 8, 0, 12.75, 19, 16))
			.reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();

	public ForgeringTableBlock(Block.Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}

	public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type) {
		return false;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch (state.get(FACING)) {
		case NORTH:
			return FACING_NORTH;
		case EAST:
			return FACING_EAST;
		case WEST:
			return FACING_WEST;
		case SOUTH:
			return FACING_SOUTH;
		default:
			return FACING_NORTH;
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

//	@Override
//	public boolean hasTileEntity(BlockState state) {
//		return true;
//	}
//
//	@Override
//	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
//		return ModTileEntityTypes.FORGERING_TABLE_TILE_ENTITY.get().create();
//	}

	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult p_225533_6_) {
		if (worldIn.isRemote) {
			return ActionResultType.SUCCESS;
		} else {
			player.openContainer(state.getContainer(worldIn, pos));
			return ActionResultType.SUCCESS;
		}
	}

	public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
		return new SimpleNamedContainerProvider((windowid, playerinv, blockpos) -> {
			return new ForgeringTableContainer(windowid, playerinv, IWorldPosCallable.of(worldIn, pos));
		}, text_component);
	}

//	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
//		if (stack.hasDisplayName()) {
//			TileEntity tileentity = worldIn.getTileEntity(pos);
//			if (tileentity instanceof ForgeringTableTileEntity) {
//				((ForgeringTableTileEntity) tileentity).setCustomName(stack.getDisplayName());
//			}
//		}
//
//	}
//
//	@Override
//	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
//		if (state.getBlock() != newState.getBlock()) {
//			TileEntity te = worldIn.getTileEntity(pos);
//			if (te instanceof ForgeringTableTileEntity) {
//				InventoryHelper.dropInventoryItems(worldIn, pos, ((ForgeringTableTileEntity) te));
//			}
//		}
//	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
		// TODO Auto-generated method stub
		return 1.0f;
	}
}
