package com.badgerson.fable.mixin;

import java.util.function.UnaryOperator;
import net.minecraft.world.chunk.ChunkGenerationStep;
import net.minecraft.world.chunk.ChunkGenerationSteps;
import net.minecraft.world.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ChunkGenerationSteps.Builder.class)
public class ChunkGenerationStepsBuilderMixin {
  @ModifyVariable(argsOnly = true, method = "then", at = @At(value = "HEAD"))
  private void thenMixin(Args args) {
    ChunkStatus status = args.get(0);

    if (status == ChunkStatus.FEATURES) {
      UnaryOperator<ChunkGenerationStep.Builder> og = args.get(1);
      UnaryOperator<ChunkGenerationStep.Builder> modify =
          (builder) -> {
            return builder.blockStateWriteRadius(2);
          };

      UnaryOperator<ChunkGenerationStep.Builder> composite =
          (builder) -> {
            var composed = og.andThen(modify);
            return composed.apply(builder);
          };
      args.set(1, composite);
    }
  }
}
