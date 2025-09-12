package club.starry.example;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ExampleMod.MODID, name = ExampleMod.NAME, useMetadata = true)
public class ExampleMod {
    public static final String MODID = "examplemod";
    public static final String NAME = "Example Mod";
    public static final String VERSION = "0.0.1";

    public static final Logger LOG = LogManager.getLogger(NAME);

    @Mod.Instance(MODID)
    public static ExampleMod INSTANCE;

    public boolean obfuscated;

    public static Config CONFIG_BEAN;

    public static final Item EXAMPLE_ITEM = new Item();
    public static final Block EXAMPLE_BLOCK = new Block(Material.rock) {};

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CONFIG_BEAN = new Config();
        INSTANCE = this;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void commonInit(FMLInitializationEvent event) {
        GameRegistry.registerItem(EXAMPLE_ITEM, "example_item", MODID);
        GameRegistry.registerBlock(EXAMPLE_BLOCK, "example_block");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        this.obfuscated = !(Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment");
    }

    @Mod.EventHandler
    public void onServerStartingEvent(FMLServerStartingEvent event) {
    }
}
