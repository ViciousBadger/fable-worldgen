package com.badgerson.fable.trees;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;

public record AdvancedTrunkConfig(
    float bendChance,
    float minBendAmount,
    float maxBendAmount,
    float straightenAmount,
    int sideBranchLength,
    float sideBranchChance,
    float sideBranchMinAngle,
    float sideBranchMaxAngle,
    int minUpBranches,
    int maxUpBranches,
    float upBranchLengthFactor,
    float upBranchMinAngle,
    float upBranchMaxAngle)
    implements FeatureConfig {
  public static final Codec<AdvancedTrunkConfig> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Codec.FLOAT.fieldOf("bend_chance").forGetter(AdvancedTrunkConfig::bendChance),
                      Codec.FLOAT
                          .fieldOf("min_bend_amount")
                          .forGetter(AdvancedTrunkConfig::minBendAmount),
                      Codec.FLOAT
                          .fieldOf("max_bend_amount")
                          .forGetter(AdvancedTrunkConfig::maxBendAmount),
                      Codec.FLOAT
                          .fieldOf("straighten_amount")
                          .forGetter(AdvancedTrunkConfig::straightenAmount),
                      Codec.INT
                          .fieldOf("side_branch_length")
                          .forGetter(AdvancedTrunkConfig::sideBranchLength),
                      Codec.FLOAT
                          .fieldOf("side_branch_chance")
                          .forGetter(AdvancedTrunkConfig::sideBranchChance),
                      Codec.FLOAT
                          .fieldOf("side_branch_min_angle")
                          .forGetter(AdvancedTrunkConfig::sideBranchMinAngle),
                      Codec.FLOAT
                          .fieldOf("side_branch_max_angle")
                          .forGetter(AdvancedTrunkConfig::sideBranchMaxAngle),
                      Codec.INT
                          .fieldOf("min_up_branches")
                          .forGetter(AdvancedTrunkConfig::minUpBranches),
                      Codec.INT
                          .fieldOf("max_up_branches")
                          .forGetter(AdvancedTrunkConfig::maxUpBranches),
                      Codec.FLOAT
                          .fieldOf("up_branch_length_factor")
                          .forGetter(AdvancedTrunkConfig::upBranchLengthFactor),
                      Codec.FLOAT
                          .fieldOf("up_branch_min_angle")
                          .forGetter(AdvancedTrunkConfig::upBranchMinAngle),
                      Codec.FLOAT
                          .fieldOf("up_branch_max_angle")
                          .forGetter(AdvancedTrunkConfig::upBranchMaxAngle))
                  .apply(instance, AdvancedTrunkConfig::new));
}
