package com.badgerson.new_worldgen.df_types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;

/** Blend between raw input and input snapped to a step interval. */
public record BlendSteps(DensityFunction input, DensityFunction blend, DensityFunction stepSize)
    implements DensityFunction {

  private static final MapCodec<BlendSteps> MAP_CODEC =
      RecordCodecBuilder.mapCodec(
          (instance) ->
              instance
                  .group(
                      DensityFunction.FUNCTION_CODEC.fieldOf("input").forGetter(BlendSteps::input),
                      DensityFunction.FUNCTION_CODEC.fieldOf("blend").forGetter(BlendSteps::blend),
                      DensityFunction.FUNCTION_CODEC
                          .fieldOf("step_size")
                          .forGetter(BlendSteps::stepSize))
                  .apply(instance, (BlendSteps::new)));
  public static final CodecHolder<BlendSteps> CODEC = DensityFunctionTypes.holderOf(MAP_CODEC);

  @Override
  public double sample(NoisePos pos) {
    double raw = this.input.sample(pos);
    double step = this.stepSize.sample(pos);
    double snap = Math.round(raw / step) * step;

    double blend = this.blend.sample(pos);

    return raw + (snap - raw) * blend;
  }

  @Override
  public void fill(double[] densities, EachApplier applier) {
    applier.fill(densities, this);
  }

  @Override
  public DensityFunction apply(DensityFunctionVisitor visitor) {
    return visitor.apply(
        new BlendSteps(
            this.input.apply(visitor), this.blend.apply(visitor), this.stepSize.apply(visitor)));
  }

  @Override
  public double minValue() {
    return this.input.minValue();
  }

  @Override
  public double maxValue() {
    return this.input.maxValue();
  }

  @Override
  public CodecHolder<? extends DensityFunction> getCodecHolder() {
    return CODEC;
  }
}
