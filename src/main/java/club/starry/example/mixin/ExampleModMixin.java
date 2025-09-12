package club.starry.example.mixin;

import club.starry.example.ExampleMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class ExampleModMixin {

    @Inject(method = "onBlockActivated", at = @At("HEAD"), remap = false)
    public void blockActivated(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_, CallbackInfoReturnable<Boolean> cir) {
        Block thiz = (Block) (Object) this;
        if (thiz instanceof BlockGrass) {
            p_149727_5_.addChatMessage(new ChatComponentText("Hello from BlockGrass with Mixin!"));
        }
    }
}
