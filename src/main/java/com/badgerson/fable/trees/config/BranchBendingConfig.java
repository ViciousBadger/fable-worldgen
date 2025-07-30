package com.badgerson.fable.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record BranchBendingConfig(
    float bendChance, float minBendAmount, float maxBendAmount, float straightenAmount) {
  public static final Codec<BranchBendingConfig> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Codec.FLOAT.fieldOf("bend_chance").forGetter(BranchBendingConfig::bendChance),
                      Codec.FLOAT
                          .fieldOf("min_bend_amount")
                          .forGetter(BranchBendingConfig::minBendAmount),
                      Codec.FLOAT
                          .fieldOf("max_bend_amount")
                          .forGetter(BranchBendingConfig::maxBendAmount),
                      Codec.FLOAT
                          .fieldOf("straighten_amount")
                          .forGetter(BranchBendingConfig::straightenAmount))
                  .apply(instance, BranchBendingConfig::new));
}
