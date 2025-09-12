package club.starry.example;

import club.someoneice.pineapplepsychic.api.IPineappleConfig;
import club.someoneice.pineapplepsychic.config.ConfigBeanV2;

public class Config extends ConfigBeanV2 implements IPineappleConfig {
    public static boolean EXAMPLE_CONFIG_BOOLEAN = false;

    public Config() {
        super("example_config");
        init();
    }

    @Override
    public void init() {
        EXAMPLE_CONFIG_BOOLEAN = this.getBoolean("example_boolean", EXAMPLE_CONFIG_BOOLEAN);
    }
}
