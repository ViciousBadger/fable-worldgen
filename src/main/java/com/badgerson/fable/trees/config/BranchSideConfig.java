package com.badgerson.fable.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;

public record BranchSideConfig(
    Optional<Float> startPadding, Optional<Float> endPadding, BranchLayer branches) {
  public static final Codec<BranchSideConfig> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Codec.FLOAT
                          .optionalFieldOf("start_padding")
                          .forGetter(BranchSideConfig::startPadding),
                      Codec.FLOAT
                          .optionalFieldOf("end_padding")
                          .forGetter(BranchSideConfig::endPadding),
                      BranchLayer.CODEC.fieldOf("branches").forGetter(BranchSideConfig::branches))
                  .apply(instance, BranchSideConfig::new));
}
