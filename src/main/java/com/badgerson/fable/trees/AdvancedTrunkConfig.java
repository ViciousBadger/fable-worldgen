package com.badgerson.fable.trees;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;

public record AdvancedTrunkConfig(
    int trunkThickness,
    float bendChance,
    float minBendAmount,
    float maxBendAmount,
    float straightenAmount,
    int sideBranchLength,
    float sideBranchChance,
    float sideBranchMinAngle,
    float sideBranchMaxAngle,
    int upBranchDepth,
    int minUpBranches,
    int maxUpBranches,
    float upBranchLengthMin,
    float upBranchLengthMax,
    float upBranchMinAngle,
    float upBranchMaxAngle)
    implements FeatureConfig {
  public static final Codec<AdvancedTrunkConfig> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Codec.INT
                          .fieldOf("trunk_thickness")
                          .forGetter(AdvancedTrunkConfig::trunkThickness),
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
                          .fieldOf("up_branch_depth")
                          .forGetter(AdvancedTrunkConfig::upBranchDepth),
                      Codec.INT
                          .fieldOf("min_up_branches")
                          .forGetter(AdvancedTrunkConfig::minUpBranches),
                      Codec.INT
                          .fieldOf("max_up_branches")
                          .forGetter(AdvancedTrunkConfig::maxUpBranches),
                      Codec.FLOAT
                          .fieldOf("up_branch_length_min")
                          .forGetter(AdvancedTrunkConfig::upBranchLengthMin),
                      Codec.FLOAT
                          .fieldOf("up_branch_length_max")
                          .forGetter(AdvancedTrunkConfig::upBranchLengthMax),
                      Codec.FLOAT
                          .fieldOf("up_branch_min_angle")
                          .forGetter(AdvancedTrunkConfig::upBranchMinAngle),
                      Codec.FLOAT
                          .fieldOf("up_branch_max_angle")
                          .forGetter(AdvancedTrunkConfig::upBranchMaxAngle))
                  .apply(instance, AdvancedTrunkConfig::new));
}
