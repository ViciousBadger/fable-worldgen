package com.badgerson.new_worldgen.df_types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;

/** Like shifted_noise, but samples another density function instead of raw noise. */
public record ShiftedDensity(DensityFunction input, DensityFunction shiftX, DensityFunction shiftZ)
    implements DensityFunction {

  private static final MapCodec<ShiftedDensity> MAP_CODEC =
      RecordCodecBuilder.mapCodec(
          (instance) ->
              instance
                  .group(
                      DensityFunction.FUNCTION_CODEC
                          .fieldOf("input")
                          .forGetter(ShiftedDensity::input),
                      DensityFunction.FUNCTION_CODEC
                          .fieldOf("shift_x")
                          .forGetter(ShiftedDensity::shiftX),
                      DensityFunction.FUNCTION_CODEC
                          .fieldOf("shift_z")
                          .forGetter(ShiftedDensity::shiftZ))
                  .apply(instance, (ShiftedDensity::new)));
  public static final CodecHolder<ShiftedDensity> CODEC = DensityFunctionTypes.holderOf(MAP_CODEC);

  @Override
  public double sample(NoisePos pos) {
    return input.sample(
        new UnblendedNoisePos(
            pos.blockX() + (int) this.shiftX.sample(pos),
            pos.blockY(),
            pos.blockZ() + (int) this.shiftZ.sample(pos)));
  }

  @Override
  public void fill(double[] densities, EachApplier applier) {
    applier.fill(densities, this);
  }

  @Override
  public DensityFunction apply(DensityFunctionVisitor visitor) {
    return visitor.apply(
        new ShiftedDensity(
            this.input.apply(visitor), this.shiftX.apply(visitor), this.shiftZ.apply(visitor)));
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
