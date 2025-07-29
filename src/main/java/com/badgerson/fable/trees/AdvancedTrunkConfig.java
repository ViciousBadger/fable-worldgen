package com.badgerson.fable.trees;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;

public record AdvancedTrunkConfig(
    int segmentLength,
    int trunkThickness,
    float bendChance,
    float minBendAmount,
    float maxBendAmount,
    float straightenAmount,
    float sideBranchChance,
    BranchTypeConfig sideBranchConfig,
    int upBranchDepth,
    int minUpBranches,
    int maxUpBranches,
    BranchTypeConfig upBranchConfig)
    implements FeatureConfig {

  public record BranchTypeConfig(float lengthMin, float lengthMax, float angleMin, float angleMax) {
    public static final Codec<BranchTypeConfig> CODEC =
        RecordCodecBuilder.create(
            instance ->
                instance
                    .group(
                        Codec.FLOAT.fieldOf("length_min").forGetter(BranchTypeConfig::lengthMin),
                        Codec.FLOAT.fieldOf("length_max").forGetter(BranchTypeConfig::lengthMax),
                        Codec.FLOAT.fieldOf("angle_min").forGetter(BranchTypeConfig::angleMin),
                        Codec.FLOAT.fieldOf("angle_max").forGetter(BranchTypeConfig::angleMax))
                    .apply(instance, BranchTypeConfig::new));
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
                      // Codec.INT
                      //     .fieldOf("side_branch_length")
                      //     .forGetter(AdvancedTrunkConfig::sideBranchLength),
                      Codec.FLOAT
                          .fieldOf("side_branch_chance")
                          .forGetter(AdvancedTrunkConfig::sideBranchChance),
                      BranchTypeConfig.CODEC
                          .fieldOf("side_branch_config")
                          .forGetter(AdvancedTrunkConfig::sideBranchConfig),
                      // Codec.FLOAT
                      //     .fieldOf("side_branch_min_angle")
                      //     .forGetter(AdvancedTrunkConfig::sideBranchMinAngle),
                      // Codec.FLOAT
                      //     .fieldOf("side_branch_max_angle")
                      //     .forGetter(AdvancedTrunkConfig::sideBranchMaxAngle),
                      Codec.INT
                          .fieldOf("up_branch_depth")
                          .forGetter(AdvancedTrunkConfig::upBranchDepth),
                      Codec.INT
                          .fieldOf("min_up_branches")
                          .forGetter(AdvancedTrunkConfig::minUpBranches),
                      Codec.INT
                          .fieldOf("max_up_branches")
                          .forGetter(AdvancedTrunkConfig::maxUpBranches),
                      BranchTypeConfig.CODEC
                          .fieldOf("up_branch_config")
                          .forGetter(AdvancedTrunkConfig::upBranchConfig))
                  // Codec.FLOAT
                  //     .fieldOf("up_branch_length_min")
                  //     .forGetter(AdvancedTrunkConfig::upBranchLengthMin),
                  // Codec.FLOAT
                  //     .fieldOf("up_branch_length_max")
                  //     .forGetter(AdvancedTrunkConfig::upBranchLengthMax),
                  // Codec.FLOAT
                  //     .fieldOf("up_branch_min_angle")
                  //     .forGetter(AdvancedTrunkConfig::upBranchMinAngle),
                  // Codec.FLOAT
                  //     .fieldOf("up_branch_max_angle")
                  //     .forGetter(AdvancedTrunkConfig::upBranchMaxAngle))
                  .apply(instance, AdvancedTrunkConfig::new));
}
