package com.leon.bizclass.client;

import com.leon.bizclass.BizClassMod;
import com.leon.bizclass.registry.ModEntities;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = BizClassMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SEAT_ENTITY.get(), NoopRenderer::new);
    }

    /** The seat entity is invisible; it just needs *a* renderer registered to satisfy the game. */
    private static class NoopRenderer extends EntityRenderer<com.leon.bizclass.entity.SeatEntity> {
        protected NoopRenderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public net.minecraft.resources.ResourceLocation getTextureLocation(com.leon.bizclass.entity.SeatEntity entity) {
            return net.minecraft.resources.ResourceLocation.withDefaultNamespace("missingno");
        }

        @Override
        public boolean shouldRender(com.leon.bizclass.entity.SeatEntity entity, net.minecraft.client.renderer.culling.Frustum frustum, double x, double y, double z) {
            return false;
        }
    }
}
