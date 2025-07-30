package com.badgerson.fable.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.math.random.Random;

public record BranchLayer(
    List<Integer> counts, FloatBounds length, FloatBounds angle, Optional<Integer> thickness) {
  public int generateBranchCount(Random random) {
    return this.counts().get(random.nextInt(this.counts().size()));
  }

  public static final Codec<BranchLayer> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Codec.list(Codec.INT).fieldOf("counts").forGetter(BranchLayer::counts),
                      FloatBounds.CODEC.fieldOf("length").forGetter(BranchLayer::length),
                      FloatBounds.CODEC.fieldOf("angle").forGetter(BranchLayer::angle),
                      Codec.INT.optionalFieldOf("thickness").forGetter(BranchLayer::thickness))
                  .apply(instance, BranchLayer::new));
}
