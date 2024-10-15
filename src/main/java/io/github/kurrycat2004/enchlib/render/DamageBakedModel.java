package io.github.kurrycat2004.enchlib.render;

import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
@NonnullByDefault
public class DamageBakedModel extends SimpleBakedModel {
    public DamageBakedModel(List<BakedQuad> generalQuadsIn, Map<EnumFacing, List<BakedQuad>> faceQuadsIn, boolean ambientOcclusionIn, boolean gui3dIn, TextureAtlasSprite textureIn, ItemCameraTransforms cameraTransformsIn, ItemOverrideList itemOverrideListIn) {
        super(generalQuadsIn, faceQuadsIn, ambientOcclusionIn, gui3dIn, textureIn, cameraTransformsIn, itemOverrideListIn);
    }

    public static class Builder extends SimpleBakedModel.Builder {
        public Builder(ModelBlock model, ItemOverrideList overrides) {
            super(model, overrides);
        }

        public Builder(IBlockState state, IBakedModel model, TextureAtlasSprite texture, BlockPos pos) {
            super(state, model, texture, pos);
        }

        @Override
        protected void addFaceQuads(IBlockState state, IBakedModel model, TextureAtlasSprite texture, EnumFacing facing, long posRand) {
            for (BakedQuad bakedquad : model.getQuads(state, facing, posRand)) {
                this.addFaceQuad(facing, new BakedDamageQuad(bakedquad, texture));
            }
        }

        @Override
        protected void addGeneralQuads(IBlockState state, IBakedModel model, TextureAtlasSprite texture, long posRand) {
            for (BakedQuad bakedquad : model.getQuads(state, null, posRand)) {
                this.addGeneralQuad(new BakedDamageQuad(bakedquad, texture));
            }
        }
    }
}
