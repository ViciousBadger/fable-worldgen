package com.badgerson.new_worldgen.df_types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;

/** Triangular wave function. */
public record Tri(DensityFunction df) implements DensityFunctionTypes.Unary {

  private static final MapCodec<Tri> MAP_CODEC =
      RecordCodecBuilder.mapCodec(
          (instance) ->
              instance
                  .group(DensityFunction.FUNCTION_CODEC.fieldOf("argument").forGetter(Tri::df))
                  .apply(instance, (Tri::new)));
  public static final CodecHolder<Tri> CODEC = DensityFunctionTypes.holderOf(MAP_CODEC);

  @Override
  public DensityFunction input() {
    return this.df;
  }

  @Override
  public double apply(double density) {
    return 4.0 * Math.abs(density - Math.floor(density + 3.0 / 4.0) + 1.0 / 4.0) - 1.0;
  }

  @Override
  public DensityFunction apply(DensityFunctionVisitor visitor) {
    return new Tri(this.df.apply(visitor));
  }

  @Override
  public double minValue() {
    return -1;
  }

  @Override
  public double maxValue() {
    return 1;
  }

  @Override
  public CodecHolder<? extends DensityFunction> getCodecHolder() {
    return CODEC;
  }
}
