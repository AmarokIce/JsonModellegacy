package net.dragon.jsonmodel;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "jsonmodel")
public class ModMain {
  public static final Logger LOGGER = LogManager.getLogger("JsonModel");

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void onTextureStitchPost(TextureStitchEvent event) {
    if (event.map.getTextureType() == 0) {
      JsonBlock.event();
    }
  }
}
