package com.badgerson.fable.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SideBranchConfig(
    float chancePerSegment, float minLength, float maxLength, float minAngle, float maxAngle) {
  public static final Codec<SideBranchConfig> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Codec.FLOAT
                          .fieldOf("chance_per_segment")
                          .forGetter(SideBranchConfig::chancePerSegment),
                      Codec.FLOAT.fieldOf("min_length").forGetter(SideBranchConfig::minLength),
                      Codec.FLOAT.fieldOf("max_length").forGetter(SideBranchConfig::maxLength),
                      Codec.FLOAT.fieldOf("min_angle").forGetter(SideBranchConfig::minAngle),
                      Codec.FLOAT.fieldOf("max_angle").forGetter(SideBranchConfig::maxAngle))
                  .apply(instance, SideBranchConfig::new));
}
