package net.dragon.jsonmodel;

public class RenderRequest {
  public int ID;
  public String[] name;
  public ModelJson[] models;
  public int rotation;
  public boolean autoRotationY;
  public boolean autoOverlay;
  public boolean randomRotation;
  public boolean renderItem;

  public RenderRequest(int ID, String[] name, ModelJson[] models, boolean autoRotationY, boolean autoOverlay, int rotation, boolean randomRotation, boolean renderItem) {
    this.ID = ID;
    this.name = name;
    this.models = models;
    this.autoRotationY = autoRotationY;
    this.autoOverlay = autoOverlay;
    this.rotation = rotation;
    this.randomRotation = randomRotation;
    this.renderItem = renderItem;
  }
}
