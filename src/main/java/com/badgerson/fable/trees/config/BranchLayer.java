package com.badgerson.fable.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public record BranchLayer(List<Integer> counts, FloatBounds length, FloatBounds angle) {
  public static final Codec<BranchLayer> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Codec.list(Codec.INT).fieldOf("counts").forGetter(BranchLayer::counts),
                      FloatBounds.CODEC.fieldOf("length").forGetter(BranchLayer::length),
                      FloatBounds.CODEC.fieldOf("angle").forGetter(BranchLayer::angle))
                  .apply(instance, BranchLayer::new));
}
