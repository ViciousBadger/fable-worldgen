package com.badgerson.fable.trees;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;

public record FableTrunkConfig(
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
  public static final Codec<FableTrunkConfig> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Codec.FLOAT.fieldOf("bend_chance").forGetter(FableTrunkConfig::bendChance),
                      Codec.FLOAT
                          .fieldOf("min_bend_amount")
                          .forGetter(FableTrunkConfig::minBendAmount),
                      Codec.FLOAT
                          .fieldOf("max_bend_amount")
                          .forGetter(FableTrunkConfig::maxBendAmount),
                      Codec.FLOAT
                          .fieldOf("straighten_amount")
                          .forGetter(FableTrunkConfig::straightenAmount),
                      Codec.INT
                          .fieldOf("side_branch_length")
                          .forGetter(FableTrunkConfig::sideBranchLength),
                      Codec.FLOAT
                          .fieldOf("side_branch_chance")
                          .forGetter(FableTrunkConfig::sideBranchChance),
                      Codec.FLOAT
                          .fieldOf("side_branch_min_angle")
                          .forGetter(FableTrunkConfig::sideBranchMinAngle),
                      Codec.FLOAT
                          .fieldOf("side_branch_max_angle")
                          .forGetter(FableTrunkConfig::sideBranchMaxAngle),
                      Codec.INT
                          .fieldOf("min_up_branches")
                          .forGetter(FableTrunkConfig::minUpBranches),
                      Codec.INT
                          .fieldOf("max_up_branches")
                          .forGetter(FableTrunkConfig::maxUpBranches),
                      Codec.FLOAT
                          .fieldOf("up_branch_length_factor")
                          .forGetter(FableTrunkConfig::upBranchLengthFactor),
                      Codec.FLOAT
                          .fieldOf("up_branch_min_angle")
                          .forGetter(FableTrunkConfig::upBranchMinAngle),
                      Codec.FLOAT
                          .fieldOf("up_branch_max_angle")
                          .forGetter(FableTrunkConfig::upBranchMaxAngle))
                  .apply(instance, FableTrunkConfig::new));
}
