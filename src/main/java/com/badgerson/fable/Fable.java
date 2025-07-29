package com.badgerson.fable;

import com.badgerson.fable.df_types.*;
import com.badgerson.fable.features.RockFeature;
import com.badgerson.fable.features.RockFeatureConfig;
import com.badgerson.fable.trees.AdvancedTrunkPlacer;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Fable implements ModInitializer {
  public static final String MOD_ID = "fable";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  public static final TrunkPlacerType<AdvancedTrunkPlacer> TRUNK_PLACER =
      Registry.register(
          Registries.TRUNK_PLACER_TYPE,
          Identifier.of(MOD_ID, "advanced_trunk_placer"),
          new TrunkPlacerType<AdvancedTrunkPlacer>(AdvancedTrunkPlacer.CODEC));

  public static final RockFeature FEATURE_ROCK =
      Registry.register(
          Registries.FEATURE,
          Identifier.of(MOD_ID, "rock"),
          new RockFeature(RockFeatureConfig.CODEC));

  public static final MapCodec<XCoord> X_COORD = dfType("x", XCoord.CODEC.codec());
  public static final MapCodec<ZCoord> Z_COORD = dfType("z", ZCoord.CODEC.codec());
  public static final MapCodec<Tri> TRI = dfType("tri", Tri.CODEC.codec());
  public static final MapCodec<Sine> SINE = dfType("sine", Sine.CODEC.codec());
  public static final MapCodec<Div> DIV = dfType("div", Div.CODEC.codec());
  public static final MapCodec<Circular> CIRCULAR = dfType("circular", Circular.CODEC.codec());
  public static final MapCodec<BlendSteps> BLEND_STEPS =
      dfType("blend_steps", BlendSteps.CODEC.codec());
  public static final MapCodec<ShiftedDensity> SHIFTED_DENSITY =
      dfType("shifted_density", ShiftedDensity.CODEC.codec());

  @Override
  public void onInitialize() {}

  private static <T extends DensityFunction> MapCodec<T> dfType(String name, MapCodec<T> entry) {
    return Registry.register(Registries.DENSITY_FUNCTION_TYPE, Identifier.of(MOD_ID, name), entry);
  }
}
