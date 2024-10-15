package io.github.kurrycat2004.enchlib.render;

import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BakedQuadRetextured;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@NonnullByDefault
public class BakedDamageQuad extends BakedQuadRetextured {
    public BakedDamageQuad(BakedQuad quad, TextureAtlasSprite textureIn) {
        super(quad, textureIn);
    }

    @Override
    protected void remapQuad() {
        for (int vertex = 0; vertex < 4; ++vertex) {
            int vertOffset = format.getIntegerSize() * vertex;
            int uvIndex = format.getUvOffsetById(0) / 4;
            float posX = Float.intBitsToFloat(this.vertexData[vertOffset]);
            float posY = Float.intBitsToFloat(this.vertexData[vertOffset + 1]);
            float posZ = Float.intBitsToFloat(this.vertexData[vertOffset + 2]);

            double u = 0, v = 0;
            // this will make the breaking animation not random, but whatever
            // @formatter:off
            switch (face) {
                case UP ->    { u = 1 - posX; v = 1 - posZ; }
                case DOWN ->  { u =     posX; v = 1 - posZ; }
                case NORTH -> { u = 1 - posX; v = 1 - posY; }
                case SOUTH -> { u =     posX; v = 1 - posY; }
                case EAST ->  { u = 1 - posZ; v = 1 - posY; }
                case WEST ->  { u =     posZ; v = 1 - posY; }
            }
            // @formatter:on

            this.vertexData[vertOffset + uvIndex] = Float.floatToRawIntBits(
                    this.texture.getInterpolatedU(u * 16)
            );
            this.vertexData[vertOffset + uvIndex + 1] = Float.floatToRawIntBits(
                    this.texture.getInterpolatedV(v * 16)
            );
        }
    }
}
