package com.badgerson.fable.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record BranchBendingConfig(FloatBounds upwards, FloatBounds sideways) {
  public static final Codec<BranchBendingConfig> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      FloatBounds.CODEC.fieldOf("upwards").forGetter(BranchBendingConfig::upwards),
                      FloatBounds.CODEC
                          .fieldOf("sideways")
                          .forGetter(BranchBendingConfig::sideways))
                  .apply(instance, BranchBendingConfig::new));
}
