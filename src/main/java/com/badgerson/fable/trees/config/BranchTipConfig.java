package com.badgerson.fable.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;

public record BranchTipConfig(Optional<BranchLayer> branches, Optional<FoliageLayer> foliage) {

  public static final Codec<BranchTipConfig> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      BranchLayer.CODEC
                          .optionalFieldOf("branches")
                          .forGetter(BranchTipConfig::branches),
                      FoliageLayer.CODEC
                          .optionalFieldOf("foliage")
                          .forGetter(BranchTipConfig::foliage))
                  .apply(instance, BranchTipConfig::new));
}
