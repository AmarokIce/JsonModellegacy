package net.dragon.jsonmodel;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockJsonModelBake {
  public static List<BakedQuad> bakeElement(ModelJson.Element e, Map<String, IIcon> iconMap) {
    List<BakedQuad> out = new ArrayList<>();
    double x0 = e.from[0] / 16.0, y0 = e.from[1] / 16.0, z0 = e.from[2] / 16.0;
    double x1 = e.to[0] / 16.0, y1 = e.to[1] / 16.0, z1 = e.to[2] / 16.0;
    double[][] C = new double[8][3];
    C[idx(0, 0, 0)] = new double[]{x0, y0, z0};
    C[idx(1, 0, 0)] = new double[]{x1, y0, z0};
    C[idx(0, 1, 0)] = new double[]{x0, y1, z0};
    C[idx(1, 1, 0)] = new double[]{x1, y1, z0};
    C[idx(0, 0, 1)] = new double[]{x0, y0, z1};
    C[idx(1, 0, 1)] = new double[]{x1, y0, z1};
    C[idx(0, 1, 1)] = new double[]{x0, y1, z1};
    C[idx(1, 1, 1)] = new double[]{x1, y1, z1};
    if (e.rotation != null && e.rotation.angle != 0) {
      char ax = Character.toLowerCase(e.rotation.axis.charAt(0));
      float ang = e.rotation.angle;
      float[] o = e.rotation.origin;
      boolean originIsZero = (o.length >= 3 && o[0] == 0f && o[1] == 0f && o[2] == 0f);
      final float oxPx = originIsZero ? 8f : o[0];
      final float oyPx = originIsZero ? ((e.from[1] + e.to[1]) * 0.5f) : o[1];
      final float ozPx = originIsZero ? 8f : o[2];
      for (int i = 0; i < 8; i++) {
        double[] r = rotateAxisExact(C[i][0], C[i][1], C[i][2], oxPx, oyPx, ozPx, ax, ang);
        C[i][0] = r[0];
        C[i][1] = r[1];
        C[i][2] = r[2];
      }
    }
    emitFaceFromCorners(out, e.faces.north, iconMap, C, new int[]{idx(1, 1, 0), idx(1, 0, 0), idx(0, 0, 0), idx(0, 1, 0)}, EnumFacing.NORTH);
    emitFaceFromCorners(out, e.faces.south, iconMap, C, new int[]{idx(0, 1, 1), idx(0, 0, 1), idx(1, 0, 1), idx(1, 1, 1)}, EnumFacing.SOUTH);
    emitFaceFromCorners(out, e.faces.west, iconMap, C, new int[]{idx(0, 1, 0), idx(0, 0, 0), idx(0, 0, 1), idx(0, 1, 1)}, EnumFacing.WEST);
    emitFaceFromCorners(out, e.faces.east, iconMap, C, new int[]{idx(1, 1, 1), idx(1, 0, 1), idx(1, 0, 0), idx(1, 1, 0)}, EnumFacing.EAST);
    emitFaceFromCorners(out, e.faces.down, iconMap, C, new int[]{idx(0, 0, 1), idx(0, 0, 0), idx(1, 0, 0), idx(1, 0, 1)}, EnumFacing.DOWN);
    emitFaceFromCorners(out, e.faces.up, iconMap, C, new int[]{idx(0, 1, 0), idx(0, 1, 1), idx(1, 1, 1), idx(1, 1, 0)}, EnumFacing.UP);
    return out;
  }

  private static void emitFaceFromCorners(List<BakedQuad> out, ModelJson.Face f, Map<String, IIcon> iconMap, double[][] C, int[] id, EnumFacing facing) {
    if (f == null) {
      return;
    }
    IIcon icon = iconMap.get(f.texture.substring(1));
    if (icon == null) {
      return;
    }
    double[] vx = new double[4], vy = new double[4], vz = new double[4];
    for (int i = 0; i < 4; i++) {
      vx[i] = C[id[i]][0];
      vy[i] = C[id[i]][1];
      vz[i] = C[id[i]][2];
    }
    float[] up = new float[4], vp = new float[4];
    assignUVForFace(f, facing, id, up, vp);
    BakedQuad q = new BakedQuad();
    for (int i = 0; i < 4; i++) {
      q.vx[i] = vx[i];
      q.vy[i] = vy[i];
      q.vz[i] = vz[i];
      q.up[i] = up[i];
      q.vp[i] = vp[i];
    }
    q.icon = icon;
    q.face = facing;
    q.faceNormal = normal(q.vx, q.vy, q.vz);
    out.add(q);
  }

  private static void assignUVForFace(ModelJson.Face f, EnumFacing face, int[] ids, float[] outU, float[] outV) {
    final float u0 = f.uv[0], v0 = f.uv[1], u1 = f.uv[2], v1 = f.uv[3];
    final float du = u1 - u0, dv = v1 - v0;
    int steps = (f.rotation == null) ? 0 : ((Integer.parseInt(f.rotation) / 90) & 3);
    if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
      switch (steps) {
        case 1:
          steps = 3;
          break;
        case 3:
          steps = 1;
          break;
      }
    }
    for (int i = 0; i < 4; i++) {
      int idx = ids[i];
      int ix = idx & 1;
      int iz = (idx >> 1) & 1;
      int iy = (idx >> 2) & 1;
      float s = 0, t = 0;
      switch (face) {

        case SOUTH:
          s = ix;
          t = 1 - iy;
          break;

        case NORTH:
          s = 1 - ix;
          t = 1 - iy;
          break;

        case EAST:
          s = 1 - iz;
          t = 1 - iy;
          break;

        case WEST:
          s = iz;
          t = 1 - iy;
          break;

        case DOWN:
          s = ix;
          t = 1 - iz;
          break;
        case UP:
          s = ix;
          t = iz;
          break;
      }
      float ss = s, tt = t;
      for (int r = 0; r < steps; r++) {
        float ns = 1f - tt;
        float nt = ss;
        ss = ns;
        tt = nt;
      }
      outU[i] = u0 + du * ss;
      outV[i] = v0 + dv * tt;
    }
  }

  private static int idx(int ix, int iy, int iz) {
    return (iy << 2) | (iz << 1) | ix;
  }

  static double[] rotateAxisExact(double x, double y, double z, float oxPx, float oyPx, float ozPx, char axis, float angleDeg) {
    double ox = oxPx / 16.0, oy = oyPx / 16.0, oz = ozPx / 16.0;
    double px = x - ox, py = y - oy, pz = z - oz;
    double a = Math.toRadians(angleDeg), c = Math.cos(a), s = Math.sin(a);
    double rx = px, ry = py, rz = pz;
    switch (Character.toLowerCase(axis)) {
      case 'x':
        ry = py * c - pz * s;
        rz = py * s + pz * c;
        break;
      case 'y':
        rx = px * c - pz * s;
        rz = px * s + pz * c;
        break;
      case 'z':
        rx = px * c - py * s;
        ry = px * s + py * c;
        break;
      default:
        return new double[]{x, y, z};
    }
    return new double[]{rx + ox, ry + oy, rz + oz};
  }

  public static double[] normal(double[] vx, double[] vy, double[] vz) {
    double ax = vx[1] - vx[0], ay = vy[1] - vy[0], az = vz[1] - vz[0];
    double bx = vx[2] - vx[0], by = vy[2] - vy[0], bz = vz[2] - vz[0];
    double nx = ay * bz - az * by, ny = az * bx - ax * bz, nz = ax * by - ay * bz;
    double len = Math.sqrt(nx * nx + ny * ny + nz * nz);
    return len == 0 ? new double[]{0, 1, 0} : new double[]{nx / len, ny / len, nz / len};
  }

  public static class BakedQuad {
    public final double[] vx = new double[4];
    public final double[] vy = new double[4];
    public final double[] vz = new double[4];
    public final float[] up = new float[4];
    public final float[] vp = new float[4];
    public double[] faceNormal = new double[3];
    public IIcon icon;
    public EnumFacing face;
  }
}
