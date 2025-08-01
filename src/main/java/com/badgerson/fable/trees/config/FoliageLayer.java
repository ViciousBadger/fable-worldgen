package com.badgerson.fable.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record FoliageLayer(int radius) {
  public static final Codec<FoliageLayer> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(Codec.INT.fieldOf("radius").forGetter(FoliageLayer::radius))
                  .apply(instance, FoliageLayer::new));
}
