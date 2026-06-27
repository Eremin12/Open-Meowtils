package wtf.tatp.meowtils.module.render;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.RenderWorldLastEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.OpacityValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.manager.session.Duels;
import wtf.tatp.meowtils.manager.session.Server;
import wtf.tatp.meowtils.util.ColorUtil;
import wtf.tatp.meowtils.util.Render;

public class ItemESP extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public float textScale = 0.65F;
    @Config
    public float opacity = 25.0F;
    @Config
    public String mode = "3D";
    @Config
    public String display = "Both";
    @Config
    public String render = "Full";
    @Config
    public String font = "Normal";
    @Config
    public boolean compactText = true;
    @Config
    public boolean autoScale = true;
    @Config
    public boolean stackSize = true;
    @Config
    public boolean showDistance = true;
    @Config
    public boolean dynamicColor = false;
    @Config
    public boolean bedwarsOnly = false;
    @Config
    public boolean iron = true;
    @Config
    public boolean gold = true;
    @Config
    public boolean diamond = true;
    @Config
    public boolean emerald = true;

    private static final int BLACK_COLOR = new Color(0, 0, 0).getRGB();

    public ItemESP() {
        super("ItemESP", Module.Category.Render);
        tag(Module.ModuleTag.SAFE);
        tooltip("Highlights important dropped items.");
        addMode(new ModeValue("Mode", Arrays.asList("3D", "2D"), "mode", this));
        addMode(new ModeValue("Render", Arrays.asList("Full", "Outline"), "render", this));
        addMode(new ModeValue("Display", Arrays.asList("Both", "Box", "Text"), "display", this));
        addMode(new ModeValue("Font", Arrays.asList("Normal", "Smooth"), "font", this));
        addSlider(new SliderValue("Text scale", 0.5D, 1.5D, 0.05D, null, "textScale", this, Float.class));
        addOpacity(new OpacityValue("Box opacity", "opacity", this));
        addToggle(new ToggleValue("Auto-scale", "autoScale", this));
        addToggle(new ToggleValue("Show stack size", "stackSize", this));
        addToggle(new ToggleValue("Show distance", "showDistance", this));
        addToggle(new ToggleValue("Compact text", "compactText", this));
        addToggle(new ToggleValue("Dynamic text color", "dynamicColor", this));
        addToggle(new ToggleValue("Bedwars only", "bedwarsOnly", this));
        addCheck(new CheckValue("Iron §7Items", "iron", this));
        addCheck(new CheckValue("§6Gold §7Items", "gold", this));
        addCheck(new CheckValue("§bDiamond §7Items", "diamond", this));
        addCheck(new CheckValue("§2Emerald §7Items", "emerald", this));
    }

    private boolean isIron(Item item) {
        return (item == Items.iron_ingot || item == Items.iron_sword || item == Items.golden_pickaxe ||
                item == Items.golden_axe || item == Items.golden_shovel || item == Items.iron_helmet ||
                item == Items.iron_chestplate || item == Items.iron_leggings || item == Items.iron_boots);
    }

    private boolean isGold(Item item) {
        return (item == Items.gold_ingot || item == Items.wooden_sword || item == Items.iron_pickaxe ||
                item == Items.iron_axe || item == Items.iron_shovel || item == Items.golden_helmet ||
                item == Items.golden_chestplate || item == Items.golden_leggings || item == Items.golden_boots ||
                item == Items.golden_apple);
    }

    private boolean isDiamond(Item item) {
        return (item == Items.diamond || item == Items.diamond_sword || item == Items.diamond_pickaxe ||
                item == Items.diamond_axe || item == Items.diamond_shovel || item == Items.diamond_helmet ||
                item == Items.diamond_chestplate || item == Items.diamond_leggings || item == Items.diamond_boots);
    }

    private boolean isEmerald(Item item) {
        return (item == Items.emerald || item == Item.getItemFromBlock(Blocks.emerald_block));
    }

    private int getPriority(Item item) {
        if (isEmerald(item)) return 4;
        if (isDiamond(item)) return 3;
        if (isGold(item)) return 2;
        if (isIron(item)) return 1;
        return 0;
    }

    @EventTarget
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (this.mc.theWorld == null || this.mc.thePlayer == null) return;
        if (this.bedwarsOnly && Bedwars.GAME.isNotActive()) return;

        Map<ItemGroup, EntityItem> renderItem = new HashMap<>();
        Map<ItemGroup, Integer> stackCounts = new HashMap<>();

        for (Entity entity : this.mc.theWorld.loadedEntityList) {
            if (!(entity instanceof EntityItem) ||
                    (Server.HYPIXEL.isActive() && (Bedwars.GAME.isActive() || Duels.BEDWARS.isActive()) && entity.ticksExisted < 10))
                continue;

            EntityItem entityItem = (EntityItem) entity;
            ItemStack itemStack = entityItem.getEntityItem();
            if (!shouldHighlight(itemStack)) continue;

            ItemGroup group = new ItemGroup(entityItem);
            EntityItem existing = renderItem.get(group);

            if (existing == null) {
                renderItem.put(group, entityItem);
                stackCounts.put(group, itemStack.stackSize);
            } else {
                Item existingItem = existing.getEntityItem().getItem();
                Item newItem = itemStack.getItem();

                int existingPriority = getPriority(existingItem);
                int newPriority = getPriority(newItem);

                if (newPriority > existingPriority) {
                    renderItem.put(group, entityItem);
                    stackCounts.put(group, itemStack.stackSize);
                } else if (newPriority == existingPriority) {
                    stackCounts.put(group, stackCounts.get(group) + itemStack.stackSize);
                }
            }
        }

        for (ItemGroup key : renderItem.keySet()) {
            EntityItem item = renderItem.get(key);
            ItemStack stack = item.getEntityItem();
            int mergedCount = stackCounts.get(key);
            boolean showBox = (this.display.equals("Box") || this.display.equals("Both"));
            boolean showText = (this.display.equals("Text") || this.display.equals("Both"));

            if (showBox) {
                boolean renderMode = this.render.equals("Full");
                boolean boxMode = this.mode.equals("3D");
                Color color = getColor(stack.getItem());

                double itemX = item.lastTickPosX + (item.posX - item.lastTickPosX) * event.getPartialTicks();
                double itemY = item.lastTickPosY + (item.posY - item.lastTickPosY) * event.getPartialTicks() + 0.4D;
                double itemZ = item.lastTickPosZ + (item.posZ - item.lastTickPosZ) * event.getPartialTicks();
                double distanceX = itemX - this.mc.thePlayer.posX;
                double distanceY = itemY - this.mc.thePlayer.posY;
                double distanceZ = itemZ - this.mc.thePlayer.posZ;
                double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
                float expand = 0.1F;

                if (this.autoScale) {
                    expand = (float) Math.max(0.1D, distance / 100.0D);
                }

                GlStateManager.pushMatrix();
                Render.drawEntityBox(item, boxMode, renderMode, color, !renderMode, color, expand, expand, expand);
                GlStateManager.popMatrix();
            }

            if (showText) {
                renderItemText(item, stack, mergedCount, event.getPartialTicks(), getColor(stack.getItem()));
            }
        }
    }

    private void renderItemText(EntityItem item, ItemStack stack, int mergedStack, float partialTicks, Color color) {
        String text;
        RenderManager rm = this.mc.getRenderManager();

        double itemX = item.lastTickPosX + (item.posX - item.lastTickPosX) * partialTicks;
        double itemY = item.lastTickPosY + (item.posY - item.lastTickPosY) * partialTicks + 0.4D;
        double itemZ = item.lastTickPosZ + (item.posZ - item.lastTickPosZ) * partialTicks;

        double x = itemX - rm.viewerPosX;
        double y = itemY - rm.viewerPosY;
        double z = itemZ - rm.viewerPosZ;

        double distanceX = itemX - this.mc.thePlayer.posX;
        double distanceY = itemY - this.mc.thePlayer.posY;
        double distanceZ = itemZ - this.mc.thePlayer.posZ;
        double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);

        if (this.compactText) {
            text = String.valueOf(mergedStack);
        } else {
            text = stack.getDisplayName();
            if (this.stackSize && mergedStack > 1) text = text + EnumChatFormatting.GRAY + " x" + mergedStack;
            if (this.showDistance) text = text + EnumChatFormatting.GRAY + " [" + (int) distance + "m]";
        }

        float baseScale = this.textScale;
        float autoScaleMultiplier = 1.0F;

        if (this.autoScale && this.compactText) {
            autoScaleMultiplier = (float) Math.max(1.0D, distance / 6.0D);
        } else if (this.autoScale) {
            autoScaleMultiplier = (float) Math.max(1.0D, distance / 10.0D);
        }

        float scale = baseScale * autoScaleMultiplier;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((this.mc.gameSettings.thirdPersonView == 2 ? -1 : 1) * rm.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.025F, -0.025F, -0.025F);
        GlStateManager.disableAlpha();
        GlStateManager.disableLighting();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int textColor = this.dynamicColor ? ColorUtil.rgb(color.getRed(), color.getGreen(), color.getBlue()) : -1;

        if (!this.font.equals("Smooth")) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, scale);
            if (this.compactText) {
                drawOutlinedString(text, -this.mc.fontRendererObj.getStringWidth(text) / 2.0F, -3.0F, textColor);
            } else {
                this.mc.fontRendererObj.drawString(text, (int)(-this.mc.fontRendererObj.getStringWidth(text) / 2.0F), (int)-3.0F, textColor);
            }
            GlStateManager.popMatrix();
        } else {
            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, scale);
            if (this.compactText) {
                drawOutlinedStringSmooth(text, -Meowtils.fontRenderer.getStringWidth(text, 10.0F) / 2.0F, -3.0F, textColor);
            } else {
                Meowtils.fontRenderer.drawScaledStringWithShadow(text, -Meowtils.fontRenderer.getStringWidth(text, 10.0F) / 2.0F, -3.0F, textColor, 10.0F);
            }
            GlStateManager.popMatrix();
        }

        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static void drawOutlinedString(String text, float x, float y, int color) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.fontRendererObj.drawStringWithShadow(text, x + 1.0F, y, 0);
        mc.fontRendererObj.drawStringWithShadow(text, x - 1.0F, y, 0);
        mc.fontRendererObj.drawStringWithShadow(text, x, y + 1.0F, 0);
        mc.fontRendererObj.drawStringWithShadow(text, x, y - 1.0F, 0);
        mc.fontRendererObj.drawStringWithShadow(text, x, y, color);
    }

    private static void drawOutlinedStringSmooth(String text, float x, float y, int color) {
        Meowtils.fontRenderer.drawScaledString(text, x + 0.5F, y, BLACK_COLOR, 10.0F);
        Meowtils.fontRenderer.drawScaledString(text, x - 0.5F, y, BLACK_COLOR, 10.0F);
        Meowtils.fontRenderer.drawScaledString(text, x, y + 0.5F, BLACK_COLOR, 10.0F);
        Meowtils.fontRenderer.drawScaledString(text, x, y - 0.5F, BLACK_COLOR, 10.0F);
        Meowtils.fontRenderer.drawScaledString(text, x, y, color, 10.0F);
    }

    private Color getColor(Item item) {
        int roundedOpacity = Math.round(255.0F * this.opacity / 100.0F);
        if (isEmerald(item)) return new Color(0, 170, 0, roundedOpacity);
        if (isDiamond(item)) return new Color(85, 255, 255, roundedOpacity);
        if (isGold(item)) return new Color(255, 170, 0, roundedOpacity);
        if (isIron(item)) return new Color(255, 255, 255, roundedOpacity);
        return new Color(255, 255, 255, roundedOpacity);
    }

    private boolean shouldHighlight(ItemStack stack) {
        if (stack == null) return false;
        Item item = stack.getItem();
        if (item == null) return false;

        if (this.iron && isIron(item)) return true;
        if (this.gold && isGold(item)) return true;
        if (this.diamond && isDiamond(item)) return true;
        if (this.emerald && isEmerald(item)) return true;
        return false;
    }

    private static final class ItemGroup {
        final int x;
        final int y;
        final int z;

        ItemGroup(EntityItem entity) {
            this.x = (int) entity.posX;
            this.y = (int) entity.posY;
            this.z = (int) entity.posZ;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (!(object instanceof ItemGroup)) return false;
            ItemGroup other = (ItemGroup) object;
            return (this.x == other.x && this.y == other.y && this.z == other.z);
        }

        @Override
        public int hashCode() {
            int h = this.x;
            h = 31 * h + this.y;
            h = 31 * h + this.z;
            return h;
        }
    }
}