package com.badgerson.fable.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.world.gen.feature.FeatureConfig;

public record AdvancedTrunkConfig(
    int segmentLength,
    int trunkThickness,
    Optional<BranchBendingConfig> bendingConfig,
    Optional<SideBranchConfig> sideBranchConfig,
    Optional<UpBranchConfig> upBranchConfig)
    implements FeatureConfig {

  public int toSegmentCount(float realLength) {
    return Math.max((int) realLength / segmentLength(), 1);
  }

  public static final Codec<AdvancedTrunkConfig> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Codec.INT
                          .fieldOf("segment_length")
                          .forGetter(AdvancedTrunkConfig::segmentLength),
                      Codec.INT
                          .fieldOf("trunk_thickness")
                          .forGetter(AdvancedTrunkConfig::trunkThickness),
                      BranchBendingConfig.CODEC
                          .optionalFieldOf("bending")
                          .forGetter(AdvancedTrunkConfig::bendingConfig),
                      SideBranchConfig.CODEC
                          .optionalFieldOf("side_branches")
                          .forGetter(AdvancedTrunkConfig::sideBranchConfig),
                      UpBranchConfig.CODEC
                          .optionalFieldOf("up_branches")
                          .forGetter(AdvancedTrunkConfig::upBranchConfig))
                  .apply(instance, AdvancedTrunkConfig::new));
}
