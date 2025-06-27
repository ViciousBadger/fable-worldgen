package com.badgerson.new_worldgen.df_types;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.world.gen.densityfunction.DensityFunction;

/** Outputs the Z coordinate of input. */
public record ZCoord() implements DensityFunction.Base {

  public static final CodecHolder<ZCoord> CODEC = CodecHolder.of(MapCodec.unit(new ZCoord()));

  @Override
  public double sample(DensityFunction.NoisePos pos) {
    return Math.min(Math.max(pos.blockZ(), minValue()), maxValue());
  }

  @Override
  public double minValue() {
    return -30_000_000;
  }

  @Override
  public double maxValue() {
    return 30_000_000;
  }

  public CodecHolder<? extends DensityFunction> getCodecHolder() {
    return CODEC;
  }
}
