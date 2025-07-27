package com.badgerson.fable.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;

public record RockFeatureConfig(BlockState state) implements FeatureConfig {
  public static final Codec<RockFeatureConfig> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(BlockState.CODEC.fieldOf("state").forGetter(RockFeatureConfig::state))
                  .apply(instance, RockFeatureConfig::new));
}
