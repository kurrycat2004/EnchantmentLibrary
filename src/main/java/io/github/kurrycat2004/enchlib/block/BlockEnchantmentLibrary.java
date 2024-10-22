package io.github.kurrycat2004.enchlib.block;

import io.github.kurrycat2004.enchlib.EnchLibMod;
import io.github.kurrycat2004.enchlib.Tags;
import io.github.kurrycat2004.enchlib.gui.GuiHandler;
import io.github.kurrycat2004.enchlib.tile.TileEnchantmentLibrary;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NonnullByDefault
public class BlockEnchantmentLibrary extends BlockContainerBase {
    public static final String REGISTRY_NAME = "enchantment_library";
    public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Tags.MODID, REGISTRY_NAME);

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    public BlockEnchantmentLibrary() {
        super(Material.WOOD);
        setSoundType(SoundType.WOOD);
        setHardness(1.5F);

        setTranslationKey(REGISTRY_NAME);
        setRegistryName(REGISTRY_NAME);

        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasCustomBreakingProgress(IBlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        if (face == state.getValue(FACING)) return BlockFaceShape.UNDEFINED;
        return BlockFaceShape.SOLID;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        getTileEntity(world, pos); // ensure tile entity exists

        //TODO: put all books in player inv into lib (drawer behaviour)

        player.openGui(EnchLibMod.INSTANCE, GuiHandler.GuiTypes.ENCHANTMENT_LIBRARY.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());

        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public @NotNull TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEnchantmentLibrary();
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.byIndex(meta);
        if (facing.getAxis() == EnumFacing.Axis.Y) facing = EnumFacing.NORTH;
        return getDefaultState().withProperty(FACING, facing);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return this.withRotation(state, mirror.toRotation(state.getValue(FACING)));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(FACING)).getIndex();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack hand) {
        if (worldIn.isRemote) return;

        ItemStack stack = new ItemStack(this);
        if (stack.isEmpty()) return;
        storeTEInItemStack(stack, te);

        spawnAsEntity(worldIn, pos, stack);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        loadTEFromItemStack(stack, tileEntity);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    public TileEnchantmentLibrary getTileEntity(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEnchantmentLibrary enchLibTile) return enchLibTile;

        tile = createNewTileEntity(world, 0);
        world.setTileEntity(pos, tile);
        return (TileEnchantmentLibrary) tile;
    }
}
