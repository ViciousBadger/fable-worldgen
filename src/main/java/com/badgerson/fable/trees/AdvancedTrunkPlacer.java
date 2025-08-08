package com.badgerson.fable.trees;

import com.badgerson.fable.Fable;
import com.badgerson.fable.trees.config.AdvancedTrunkConfig;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacer.TreeNode;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import org.joml.Vector3f;

public class AdvancedTrunkPlacer extends TrunkPlacer {

  private AdvancedTrunkConfig config;

  public static final MapCodec<AdvancedTrunkPlacer> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      AdvancedTrunkConfig.CODEC
                          .fieldOf("config")
                          .forGetter(
                              (placer) -> {
                                return placer.config;
                              }))
                  .apply(instance, AdvancedTrunkPlacer::new));

  public AdvancedTrunkPlacer(AdvancedTrunkConfig config) {
    super((int) config.trunk().length().min(), 0, 0);
    this.config = config;
  }

  @Override
  protected TrunkPlacerType<?> getType() {
    return Fable.TRUNK_PLACER;
  }

  @Override
  public List<TreeNode> generate(
      TestableWorld world,
      BiConsumer<BlockPos, BlockState> replacer,
      Random random,
      int height,
      BlockPos startPos,
      TreeFeatureConfig config) {
    for (BlockPos trunkDirtPos :
        new TrunkSegment(startPos.down(), this.config.trunk().thickness().orElse(1))) {
      setToDirt(world, replacer, random, trunkDirtPos, config);
    }

    Vector3f initialPosition = Vec3d.of(startPos.down()).add(0.5, 0.5, 0.5).toVector3f();

    List<FoliagePlacer.TreeNode> treeNodes = new ArrayList<FoliagePlacer.TreeNode>();

    List<BranchProducer> thisLayer = new ArrayList<>();
    for (BranchProducer trunkBranch :
        BranchProducer.evenlySpread(
            this.config.trunk(),
            this.config.bending(),
            initialPosition,
            new Vector3f(0f, 1f, 0f),
            random)) {
      thisLayer.add(trunkBranch);
    }

    this.config
        .roots()
        .ifPresent(
            (rootsConfig) -> {
              for (BranchProducer rootBranch :
                  BranchProducer.evenlySpread(
                      rootsConfig,
                      this.config.bending(),
                      initialPosition,
                      new Vector3f(0f, -1f, 0f),
                      random)) {
                thisLayer.add(rootBranch);
              }
            });

    while (thisLayer.size() > 0) {
      // // Layer swap
      // List<BranchProducer> thisLayer = thisLayer;
      // thisLayer = new ArrayList<>();
      List<BranchProducer> nextLayer = new ArrayList<>();

      // Build this layer
      producerLoop:
      for (BranchProducer producer : thisLayer) {
        BranchProduct product = producer.produce(random);
        for (BranchProduct.TrunkBlock block : product.trunkBlocks()) {
          if (!this.canReplaceOrIsLog(world, block.pos())) {
            // In case of unplaceable block, easiest solution is to simply stop
            // and ignore any foliage or sub-branches of this branch.
            continue producerLoop;
          }
          this.getAndSetState(
              world,
              replacer,
              random,
              block.pos(),
              config,
              state -> state.withIfExists(PillarBlock.AXIS, block.axis()));
        }
        for (BranchProduct.FoliageNode foliage : product.foliageNodes()) {
          treeNodes.add(
              new FoliagePlacer.TreeNode(
                  foliage.pos(), foliage.radius(), foliage.isOnGiantTrunk()));
        }
        for (BranchProducer subBranch : product.subBranches()) {
          nextLayer.add(subBranch);
        }
      }

      thisLayer.clear();
      thisLayer.addAll(nextLayer);
    }

    return treeNodes;
  }
}
