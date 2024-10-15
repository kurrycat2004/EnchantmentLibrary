package io.github.kurrycat2004.enchlib.render;

import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * This TESR is used to render the breaking animation of any model correctly, even if the texture is not 16x16 <br>
 * It will simply render the breaking texture on top of each quad of the model, using its position in the block as UVs. <br>
 * <br>
 * Add <code>boolean canRenderBreaking() { return true; }</code> to your TileEntity, add<br>
 * <code>boolean hasCustomBreakingProgress(IBlockState state) { return true; }</code> <br>
 * to your Block and bind this TESR to your TileEntity using <br>
 * <code>ClientRegistry.bindTileEntitySpecialRenderer(YourTileEntity.class, new ModelDamageTESR());</code> <br>
 * to use this. <br>
 *
 * THIS ONLY RENDERS THE BREAKING ANIMATION, NOT THE BLOCK ITSELF! <br>
 * */
@SideOnly(Side.CLIENT)
@NonnullByDefault
public class ModelDamageTESR extends TileEntitySpecialRenderer<TileEntity> {
    @Override
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        // this TESR is only for rendering the breaking animation
        if (destroyStage < 0) return;

        IBlockState state = te.getWorld().getBlockState(te.getPos());
        if (state.getRenderType() != EnumBlockRenderType.MODEL) return;
        Minecraft mc = Minecraft.getMinecraft();

        BlockRendererDispatcher blockRendererDispatcher = mc.getBlockRendererDispatcher();

        GlStateManager.enableBlend();
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        RenderHelper.disableStandardItemLighting();

        IBakedModel model = blockRendererDispatcher.getModelForState(state);
        state = state.getActualState(te.getWorld(), te.getPos());
        state = state.getBlock().getExtendedState(state, te.getWorld(), te.getPos());
        TextureAtlasSprite texture = mc.renderGlobal.destroyBlockIcons[destroyStage];

        // use custom damage model, instead of forge's
        IBakedModel damageModel = getDamageModel(model, texture, state, te.getWorld(), te.getPos());

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        bufferBuilder.setTranslation(
                -TileEntityRendererDispatcher.staticPlayerX,
                -TileEntityRendererDispatcher.staticPlayerY,
                -TileEntityRendererDispatcher.staticPlayerZ);
        bufferBuilder.noColor();

        blockRendererDispatcher.getBlockModelRenderer().renderModel(
                te.getWorld(), damageModel, state, te.getPos(),
                Tessellator.getInstance().getBuffer(), true);

        Tessellator.getInstance().draw();
        bufferBuilder.setTranslation(0, 0, 0);

        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        GlStateManager.disableBlend();
    }

    /// This is meant to replace {@link net.minecraftforge.client.ForgeHooksClient#getDamageModel}
    private static IBakedModel getDamageModel(IBakedModel ibakedmodel, TextureAtlasSprite texture, IBlockState state, IBlockAccess world, BlockPos pos) {
        // we already did this
        //state = state.getBlock().getExtendedState(state, world, pos);
        return (new DamageBakedModel.Builder(state, ibakedmodel, texture, pos)).makeBakedModel();
    }
}
