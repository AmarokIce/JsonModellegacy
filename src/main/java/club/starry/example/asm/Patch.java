package club.starry.example.asm;

import net.tclproject.mysteriumlib.asm.common.CustomLoadingPlugin;
import net.tclproject.mysteriumlib.asm.common.FirstClassTransformer;

public class Patch extends CustomLoadingPlugin {
    public Patch() {}

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{FirstClassTransformer.class.getName()};
    }

    @Override
    public void registerFixes() {
        registerClassWithFixes(ExampleModASM.class.getName());
    }
}