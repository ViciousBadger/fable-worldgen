package com.badgerson.fable.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.world.gen.feature.FeatureConfig;

public record AdvancedTrunkConfig(
    Optional<BranchBendingConfig> bending, BranchLayer trunk, Optional<BranchLayer> roots)
    implements FeatureConfig {

  public static final Codec<AdvancedTrunkConfig> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      BranchBendingConfig.CODEC
                          .optionalFieldOf("bending")
                          .forGetter(AdvancedTrunkConfig::bending),
                      BranchLayer.CODEC.fieldOf("trunk").forGetter(AdvancedTrunkConfig::trunk),
                      BranchLayer.CODEC
                          .optionalFieldOf("roots")
                          .forGetter(AdvancedTrunkConfig::roots))
                  .apply(instance, AdvancedTrunkConfig::new));
}
