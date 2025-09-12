package net.dragon.jsonmodel;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.dragon.jsonmodel.BlockJsonModelBake.BakedQuad;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class RenderJsonBlockModel implements ISimpleBlockRenderingHandler {
  public final ImmutableList<List<BakedQuad>> quads;
  public final int ID;
  public int rotation;
  public boolean autoRotationY;
  public boolean autoOverlay;
  public boolean randomRotation;
  public boolean renderItem;

  public RenderJsonBlockModel(List<List<BakedQuad>> quads, int ID, boolean autoRotationY,
                              boolean autoOverlay, int rotation, boolean randomRotation,
                              boolean renderItem) {
    this.quads = ImmutableList.copyOf(quads);
    this.ID = ID;
    this.autoRotationY = autoRotationY;
    this.autoOverlay = autoOverlay;
    this.rotation = rotation;
    this.randomRotation = randomRotation;
    this.renderItem = renderItem;
  }

  private static int sampleBrightnessByNormal(IBlockAccess w, int x, int y, int z, double[] n, Block b) {
    double ax = Math.abs(n[0]), ay = Math.abs(n[1]), az = Math.abs(n[2]);
    if (ay >= ax && ay >= az) {
      return w.getLightBrightnessForSkyBlocks(x, y + n[1] > 0 ? 1 : -1, z, 0);
    } else if (ax >= ay && ax >= az) {
      return w.getLightBrightnessForSkyBlocks(x + n[0] > 0 ? 1 : -1, y, z, 0);
    }

    return w.getLightBrightnessForSkyBlocks(x, y, z + n[2] > 0 ? 1 : -1, 0);
  }

  private static float shadeByNormal(double[] n) {
    double ax = Math.abs(n[0]), ay = Math.abs(n[1]), az = Math.abs(n[2]);
    if (ay >= ax && ay >= az) {
      return n[1] > 0 ? 1.0f : 0.5f;
    }
    if (ax >= ay && ax >= az) {
      return 0.6f;
    }
    return 0.8f;
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {
    return renderItem;
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    int renderCount = this.autoOverlay ? this.quads.size() : 1;
    int metadata = world.getBlockMetadata(x, y, z);
    if (metadata < renderCount) {
      Tessellator t = Tessellator.instance;
      int BQ;
      float shade;
      IIcon icon;
      double U;
      double V;
      BakedQuad q;
      final double a = Math.toRadians(randomRotation ? (Math.abs(x + y + z) % 4) * 90 : (this.autoRotationY ? (90 * (metadata % 4)) : this.rotation));
      final double c = Math.cos(a), s = Math.sin(a);
      for (int w = 0; w < this.quads.get(metadata).size(); w++) {
        q = this.quads.get(metadata).get(w);
        int br = sampleBrightnessByNormal(world, x, y, z, q.faceNormal, block);
        BQ = this.getFaceBrightness(world, x, y, z, block, q.face);
        t.setBrightness(BQ);
        shade = shadeByNormal(q.faceNormal);
        t.setColorOpaque_F(shade, shade, shade);
        icon = q.icon;
        for (int i = 0; i < 4; i++) {
          double wx = x + q.vx[i], wy = y + q.vy[i], wz = z + q.vz[i];
          final double cx = x + 0.5, cz = z + 0.5;
          double px = (x + q.vx[i]) - cx;
          double pz = (z + q.vz[i]) - cz;
          double rx = px * c - pz * s;
          double rz = px * s + pz * c;
          U = icon.getInterpolatedU(q.up[i]);
          V = icon.getInterpolatedV(q.vp[i]);
          t.addVertexWithUV(cx + rx, y + q.vy[i], cz + rz, U, V);
        }
      }
    }
    return true;
  }

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    if (metadata < this.quads.size()) {
      Tessellator t = Tessellator.instance;
      GL11.glPushMatrix();
      Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      t.startDrawingQuads();
      t.setColorOpaque_F(1, 1, 1);
      t.setBrightness(255);
      BakedQuad q;
      IIcon icon;
      double U;
      double V;
      for (int s = 0; s < this.quads.get(metadata).size(); s++) {
        q = this.quads.get(metadata).get(s);
        icon = q.icon;
        for (int i = 0; i < 4; i++) {
          U = icon.getInterpolatedU(q.up[i]);
          V = icon.getInterpolatedV(q.vp[i]);
          t.addVertexWithUV(q.vx[i], q.vy[i], q.vz[i], U, V);
        }
      }
      t.draw();
      GL11.glPopMatrix();
    }
  }

  private int getFaceBrightness(IBlockAccess w, int x, int y, int z, Block b, EnumFacing face) {
    switch (face) {
      case UP:
        return w.getLightBrightnessForSkyBlocks(x, y + 1, z, 0);
      case DOWN:
        return w.getLightBrightnessForSkyBlocks(x, y - 1, z, 0);
      case NORTH:
        return w.getLightBrightnessForSkyBlocks(x, y, z - 1, 0);
      case SOUTH:
        return w.getLightBrightnessForSkyBlocks(x, y, z + 1, 0);
      case WEST:
        return w.getLightBrightnessForSkyBlocks(x - 1, y, z, 0);
      case EAST:
        return w.getLightBrightnessForSkyBlocks(x + 1, y, z, 0);
      default:
        return b.getMixedBrightnessForBlock(w, x, y, z);
    }
  }

  private float getFaceShade(EnumFacing face) {
    switch (face) {
      case DOWN:
        return 0.5f;
      case NORTH:
      case SOUTH:
        return 0.8f;
      case WEST:
      case EAST:
        return 0.6f;
      case UP:
      default:
        return 1.0f;
    }
  }

  @Override
  public int getRenderId() {
    return this.ID;
  }
}
