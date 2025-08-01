package com.badgerson.fable.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;

public record BranchSideConfig(
    Optional<Integer> startPadding,
    Optional<Integer> endPadding,
    Optional<BranchLayer> branches,
    Optional<FoliageLayer> foliage) {
  public static final Codec<BranchSideConfig> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Codec.INT
                          .optionalFieldOf("start_padding")
                          .forGetter(BranchSideConfig::startPadding),
                      Codec.INT
                          .optionalFieldOf("end_padding")
                          .forGetter(BranchSideConfig::endPadding),
                      BranchLayer.CODEC
                          .optionalFieldOf("branches")
                          .forGetter(BranchSideConfig::branches),
                      FoliageLayer.CODEC
                          .optionalFieldOf("foliage")
                          .forGetter(BranchSideConfig::foliage))
                  .apply(instance, BranchSideConfig::new));
}
