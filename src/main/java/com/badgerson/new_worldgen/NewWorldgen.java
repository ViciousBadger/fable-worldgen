package com.badgerson.new_worldgen;

import com.badgerson.new_worldgen.df_types.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewWorldgen implements ModInitializer {
  public static final String MOD_ID = "new-worldgen";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  @Override
  public void onInitialize() {
    Registry.register(
        Registries.DENSITY_FUNCTION_TYPE, Identifier.of("fable", "x"), XCoord.CODEC.codec());
    Registry.register(
        Registries.DENSITY_FUNCTION_TYPE, Identifier.of("fable", "z"), ZCoord.CODEC.codec());
    Registry.register(
        Registries.DENSITY_FUNCTION_TYPE, Identifier.of("fable", "tri"), Tri.CODEC.codec());
    Registry.register(
        Registries.DENSITY_FUNCTION_TYPE, Identifier.of("fable", "quart"), Quart.CODEC.codec());
    Registry.register(
        Registries.DENSITY_FUNCTION_TYPE,
        Identifier.of("fable", "blend_steps"),
        BlendSteps.CODEC.codec());
    Registry.register(
        Registries.DENSITY_FUNCTION_TYPE,
        Identifier.of("fable", "shifted_density"),
        ShiftedDensity.CODEC.codec());
  }
}
