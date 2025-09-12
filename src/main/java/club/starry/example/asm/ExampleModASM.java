package club.starry.example.asm;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.tclproject.mysteriumlib.asm.annotations.Fix;
import net.tclproject.mysteriumlib.asm.annotations.FixOrder;


public class ExampleModASM {
    @Fix(allThatExtend = true, order = FixOrder.FIRST)
    public static boolean onBlockActivated(Block instance, World world, int x, int y, int z,
                                        EntityPlayer player, int side, float fx, float fy, float fz) {
        if (!(instance instanceof BlockGrass)) return false;
        player.addChatMessage(new ChatComponentText("Hello from BlockGrass with ASM!"));
        return true;
    }
}
