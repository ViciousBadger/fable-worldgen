package com.badgerson.fable.mixin;

import java.util.function.UnaryOperator;
import net.minecraft.world.chunk.ChunkGenerationStep;
import net.minecraft.world.chunk.ChunkGenerationSteps;
import net.minecraft.world.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkGenerationSteps.Builder.class)
public class ChunkGenerationStepsBuilderMixin {
  @Inject(method = "then", at = @At("HEAD"))
  private void thenMixin(
      ChunkStatus status,
      UnaryOperator<ChunkGenerationStep.Builder> stepFactory,
      CallbackInfoReturnable<ChunkGenerationSteps.Builder> ci) {
    if (status == ChunkStatus.FEATURES) {
      UnaryOperator<ChunkGenerationStep.Builder> og = stepFactory;
      UnaryOperator<ChunkGenerationStep.Builder> modify =
          (builder) -> {
            return builder.blockStateWriteRadius(2);
          };

      UnaryOperator<ChunkGenerationStep.Builder> composite =
          (builder) -> {
            var composed = og.andThen(modify);
            return composed.apply(builder);
          };
      stepFactory = composite;
    }
  }
}
