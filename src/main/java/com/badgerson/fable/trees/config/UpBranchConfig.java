package com.badgerson.fable.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public record UpBranchConfig(List<BranchLayer> layers) {

  public static final Codec<UpBranchConfig> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Codec.list(BranchLayer.CODEC)
                          .fieldOf("layers")
                          .forGetter(UpBranchConfig::layers))
                  .apply(instance, UpBranchConfig::new));
}
