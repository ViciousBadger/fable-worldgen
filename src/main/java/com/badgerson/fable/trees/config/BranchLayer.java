package com.badgerson.fable.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.math.random.Random;

public record BranchLayer(
    List<Integer> count_choices,
    FloatBounds length,
    FloatBounds angle,
    Optional<Integer> thickness,
    Optional<BranchSideConfig> side,
    Optional<BranchTipConfig> tip) {
  public int generateBranchCount(Random random) {
    return this.count_choices().get(random.nextInt(this.count_choices().size()));
  }

  public static final Codec<BranchLayer> CODEC =
      Codec.recursive(
          BranchLayer.class.getSimpleName(),
          selfCodec ->
              RecordCodecBuilder.create(
                  instance ->
                      instance
                          .group(
                              Codec.list(Codec.INT)
                                  .fieldOf("count_choices")
                                  .forGetter(BranchLayer::count_choices),
                              FloatBounds.CODEC.fieldOf("length").forGetter(BranchLayer::length),
                              FloatBounds.CODEC.fieldOf("angle").forGetter(BranchLayer::angle),
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

  // public static final Codec<BranchLayer> CODEC =
  //     RecordCodecBuilder.create(
  //         instance ->
  //             instance
  //                 .group(
  //                     Codec.list(Codec.INT)
  //                         .fieldOf("count_choices")
  //                         .forGetter(BranchLayer::count_choices),
  //                     FloatBounds.CODEC.fieldOf("length").forGetter(BranchLayer::length),
  //                     FloatBounds.CODEC.fieldOf("angle").forGetter(BranchLayer::angle),
  //                     Codec.INT.optionalFieldOf("thickness").forGetter(BranchLayer::thickness),
  //
  // BranchSideConfig.CODEC.optionalFieldOf("side").forGetter(BranchLayer::side),
  //                     BranchTipConfig.CODEC.optionalFieldOf("tip").forGetter(BranchLayer::tip))
  //                 .apply(instance, BranchLayer::new));
}
