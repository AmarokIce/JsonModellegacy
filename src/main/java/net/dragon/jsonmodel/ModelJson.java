package net.dragon.jsonmodel;

import java.util.List;
import java.util.Map;

public class ModelJson {
  public Map<String, String> textures;
  public List<Element> elements;

  public static class Element {
    public float[] from;
    public float[] to;
    public Rotation rotation;
    public Faces faces;
  }

  public static class Rotation {
    public float angle;
    public String axis;
    public float[] origin;
  }

  public static class Faces {
    public Face north, east, south, west, up, down;
  }

  public static class Face {
    public float[] uv;
    public String texture;
    public String rotation;
  }
}