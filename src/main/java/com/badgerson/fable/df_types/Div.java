package com.badgerson.fable.df_types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;

public record Div(DensityFunction argument1, DensityFunction argument2) implements DensityFunction {

  private static final MapCodec<Div> MAP_CODEC =
      RecordCodecBuilder.mapCodec(
          (instance) ->
              instance
                  .group(
                      DensityFunction.FUNCTION_CODEC.fieldOf("argument1").forGetter(Div::argument1),
                      DensityFunction.FUNCTION_CODEC.fieldOf("argument2").forGetter(Div::argument2))
                  .apply(instance, (Div::new)));
  public static final CodecHolder<Div> CODEC = DensityFunctionTypes.holderOf(MAP_CODEC);

  @Override
  public double sample(NoisePos pos) {
    double dividendValue = this.argument1.sample(pos);
    double divisorValue = this.argument2.sample(pos);

    if (divisorValue == 0) {
      return 0.0;
    }

    double result = dividendValue / divisorValue;

    return result;
  }

  @Override
  public void fill(double[] densities, EachApplier applier) {
    applier.fill(densities, this);
  }

  @Override
  public DensityFunction apply(DensityFunctionVisitor visitor) {
    return visitor.apply(new Div(this.argument1.apply(visitor), this.argument2.apply(visitor)));
  }

  @Override
  public DensityFunction argument1() {
    return argument1;
  }

  @Override
  public DensityFunction argument2() {
    return argument2;
  }

  @Override
  public double minValue() {
    if (this.argument2.minValue() == 0) return 0;
    return this.argument1.minValue() / this.argument2.minValue();
  }

  @Override
  public double maxValue() {
    if (this.argument2.maxValue() == 0) return 0;
    return this.argument1.maxValue() / this.argument2.maxValue();
  }

  @Override
  public CodecHolder<? extends DensityFunction> getCodecHolder() {
    return CODEC;
  }
}
