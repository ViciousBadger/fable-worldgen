package com.badgerson.fable.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public record SideBranchConfig(int startAt, List<BranchLayer> layers) {
  public static final Codec<SideBranchConfig> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Codec.INT.fieldOf("start_at").forGetter(SideBranchConfig::startAt),
                      Codec.list(BranchLayer.CODEC)
                          .fieldOf("layers")
                          .forGetter(SideBranchConfig::layers))
                  .apply(instance, SideBranchConfig::new));
}
