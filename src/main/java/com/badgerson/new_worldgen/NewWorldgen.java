package com.badgerson.new_worldgen;

import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badgerson.new_worldgen.df_types.*;

public class NewWorldgen implements ModInitializer {
	public static final String MOD_ID = "new-worldgen";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        Registry.register(
            Registries.DENSITY_FUNCTION_TYPE,
            Identifier.of("extra", "x"), XCoord.CODEC.codec()
        );
        Registry.register(
            Registries.DENSITY_FUNCTION_TYPE,
            Identifier.of("extra", "z"), ZCoord.CODEC.codec()
        );
        Registry.register(
            Registries.DENSITY_FUNCTION_TYPE,
            Identifier.of("extra", "tri"), Tri.CODEC.codec()
        );
	}
}
