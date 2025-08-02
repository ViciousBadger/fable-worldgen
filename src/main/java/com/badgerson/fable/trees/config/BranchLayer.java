package com.badgerson.fable.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.math.random.Random;

public record BranchLayer(
    FloatBounds length,
    Optional<List<Integer>> countChoices,
    Optional<FloatBounds> angle,
    Optional<Integer> thickness,
    Optional<BranchSideConfig> side,
    Optional<BranchTipConfig> tip) {
  public int generateBranchCount(Random random) {
    return this.countChoices()
        .map((choices) -> choices.get(random.nextInt(choices.size())))
        .orElse(1);
  }

  public static final Codec<BranchLayer> CODEC =
      Codec.recursive(
          BranchLayer.class.getSimpleName(),
          selfCodec ->
              RecordCodecBuilder.create(
                  instance ->
                      instance
                          .group(
                              FloatBounds.CODEC.fieldOf("length").forGetter(BranchLayer::length),
                              Codec.list(Codec.INT)
                                  .optionalFieldOf("count_choices")
                                  .forGetter(BranchLayer::countChoices),
                              FloatBounds.CODEC
                                  .optionalFieldOf("angle")
                                  .forGetter(BranchLayer::angle),
                              Codec.INT
                                  .optionalFieldOf("thickness")
                                  .forGetter(BranchLayer::thickness),
                              BranchSideConfig.CODEC
                                  .optionalFieldOf("side")
                                  .forGetter(BranchLayer::side),
                              BranchTipConfig.CODEC
                                  .optionalFieldOf("tip")
                                  .forGetter(BranchLayer::tip))
                          .apply(instance, BranchLayer::new)));
}
