package com.badgerson.fable.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.random.Random;

public record FloatBounds(float min, float max) {
  public static final Codec<FloatBounds> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Codec.FLOAT.fieldOf("min").forGetter(FloatBounds::min),
                      Codec.FLOAT.fieldOf("max").forGetter(FloatBounds::max))
                  .apply(instance, FloatBounds::new));

  public float generate(Random random) {
    return min() + random.nextFloat() * (max() - min());
  }
}
