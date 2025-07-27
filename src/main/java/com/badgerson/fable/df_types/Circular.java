package com.badgerson.fable.df_types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;

/** Circular easing function. Input is clamped to [0,inf] to prevent imaginary numbers. */
public record Circular(DensityFunction df) implements DensityFunctionTypes.Unary {

  private static final MapCodec<Circular> MAP_CODEC =
      RecordCodecBuilder.mapCodec(
          (instance) ->
              instance
                  .group(DensityFunction.FUNCTION_CODEC.fieldOf("argument").forGetter(Circular::df))
                  .apply(instance, (Circular::new)));
  public static final CodecHolder<Circular> CODEC = DensityFunctionTypes.holderOf(MAP_CODEC);

  @Override
  public DensityFunction input() {
    return this.df;
  }

  @Override
  public double apply(double density) {
    // return 1.0 - Math.pow(1.0 - Math.abs(density), 4.0);
    return Math.sqrt(1.0 - Math.pow(Math.max(0.0, density) - 1.0, 2.0));
  }

  @Override
  public DensityFunction apply(DensityFunctionVisitor visitor) {
    return new Circular(this.df.apply(visitor));
  }

  @Override
  public double minValue() {
    return 0.0;
  }

  @Override
  public double maxValue() {
    return df.maxValue();
  }

  @Override
  public CodecHolder<? extends DensityFunction> getCodecHolder() {
    return CODEC;
  }
}
