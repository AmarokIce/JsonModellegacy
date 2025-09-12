package net.dragon.jsonmodel;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.dragon.jsonmodel.BlockJsonModelBake.BakedQuad;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IIcon;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class JsonBlock {
  public static final Map<String, IIcon> iconMap = new HashMap<>();
  public static final Map<String, String> iconNameMap = new HashMap<>();
  public static final Map<IBlockJsonModel, Integer> IDMap = new HashMap<>();
  public static final Gson gson = new Gson();
  public static int jsonID = 90000;
  public static List<RenderRequest> renderRequest = new ArrayList<>();

  @SideOnly(Side.CLIENT)
  public static void register(final IBlockJsonModel block, boolean randomRotation, boolean renderItem,
                              boolean autoRotationY, int rotation, String... name) {
    if (FMLLaunchHandler.side().isServer()) {
      throw new IllegalStateException("You can only register blocks model in client.");
    }

    Objects.requireNonNull(block);

    String modId = Loader.instance().activeModContainer().getModId();
    if (Objects.isNull(modId) || modId.isEmpty()) {
      modId = "minecraft";
    }


    int size = name.length;
    ModelJson[] models = new ModelJson[size];
    for (int s = 0; s < size; s++) {
      ModMain.LOGGER.info("register json block model : {}", name[s]);
      final Path path = Paths.get("assets", modId, "textures", "json", "block",
          name[s] + ".json");

      try (final InputStream stream = JsonBlock.class.getResourceAsStream(path.toString())) {
        if (Objects.isNull(stream)) {
          continue;
        }
        final InputStreamReader reader = new InputStreamReader(stream);
        models[s] = gson.fromJson(reader, ModelJson.class);
        if (Objects.isNull(models[s]) || Objects.isNull(models[s].textures)) {
          continue;
        }

        String finalModId = modId;
        models[s].textures.forEach((textureID, textureName) -> {
          final String texName = textureName.replace(finalModId + ":blocks/", "");
          Minecraft.getMinecraft().getTextureMapBlocks().registerIcon(texName);
          iconNameMap.put(textureID, texName);
          ModMain.LOGGER.info("Register texture pool ID : {} name : {}", textureID, textureName);
        });
      } catch (JsonSyntaxException | JsonIOException | IOException e) {
        ModMain.LOGGER.error(e);
      }
    }
    renderRequest.add(new RenderRequest(jsonID, name, models, autoRotationY, false, rotation, randomRotation, renderItem));
    IDMap.put(block, jsonID);
    jsonID++;
  }

  protected static void registerJsonBlock(RenderRequest RR) {
    int size = RR.models.length;

    Map<String, IIcon> iconMapTemp = new HashMap<String, IIcon>();
    ModelJson MJ;
    for (int s = 0; s < size; s++) {
      MJ = RR.models[s];
      if (Objects.isNull(MJ.textures)) {
        continue;
      }

      MJ.textures.forEach((textureID, textureName) -> {
        if (iconMap.containsKey(textureID)) {
          iconMapTemp.put(textureID, iconMap.get(textureID));
        } else {
          ModMain.LOGGER.info("can't find texture from pool ID : {} name : {}", textureID, textureName);
        }
      });
    }

    List<List<BakedQuad>> quads = Lists.newArrayList();
    for (int s = 0; s < size; s++) {
      List<BakedQuad> bakedQuadsTemp = new ArrayList<>();
      for (ModelJson.Element e : RR.models[s].elements) {
        bakedQuadsTemp.addAll(BlockJsonModelBake.bakeElement(e, iconMapTemp));
      }
      quads.add(s, bakedQuadsTemp);
    }
    RenderingRegistry.registerBlockHandler(RR.ID, new RenderJsonBlockModel(quads, RR.ID,
        RR.autoRotationY, RR.autoOverlay, RR.rotation, RR.randomRotation, RR.renderItem));
  }

  public static void event() {
    for (RenderRequest request : renderRequest) {
      registerJsonBlock(request);
    }
  }
}
