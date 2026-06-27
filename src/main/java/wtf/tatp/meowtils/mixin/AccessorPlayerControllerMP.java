package wtf.tatp.meowtils.mixin;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerControllerMP.class)
public interface AccessorPlayerControllerMP {

  @Accessor("curBlockDamageMP")
  float getCurBlockDamageMP();

  @Accessor("blockHitDelay")
  void setBlockHitDelay(int blockHitDelay);

  @Accessor("blockHitDelay")
  int getBlockHitDelay();
}