package com.badgerson.fable.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record UpBranchConfig(
    int recursionLimit,
    int minBranches,
    int maxBranches,
    float minLength,
    float maxLength,
    float minAngle,
    float maxAngle) {
  public static final Codec<UpBranchConfig> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Codec.INT
                          .fieldOf("recursion_limit")
                          .forGetter(UpBranchConfig::recursionLimit),
                      Codec.INT.fieldOf("min_branches").forGetter(UpBranchConfig::minBranches),
                      Codec.INT.fieldOf("max_branches").forGetter(UpBranchConfig::maxBranches),
                      Codec.FLOAT.fieldOf("min_length").forGetter(UpBranchConfig::minLength),
                      Codec.FLOAT.fieldOf("max_length").forGetter(UpBranchConfig::maxLength),
                      Codec.FLOAT.fieldOf("min_angle").forGetter(UpBranchConfig::minAngle),
                      Codec.FLOAT.fieldOf("max_angle").forGetter(UpBranchConfig::maxAngle))
                  .apply(instance, UpBranchConfig::new));
}
