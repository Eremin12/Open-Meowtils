/*    */ package wtf.tatp.meowtils.event;
/*    */ 
/*    */ import net.minecraft.client.multiplayer.WorldClient;
/*    */ import wtf.tatp.meowtils.event.api.Event;
/*    */ 
/*    */ public class WorldEvent
/*    */   extends Event {
/*    */   private final WorldClient world;
/*    */   private final Type type;
/*    */   
/*    */   public enum Type {
/* 12 */     LOAD,
/* 13 */     UNLOAD;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WorldEvent(WorldClient world, Type type) {
/* 20 */     this.world = world;
/* 21 */     this.type = type;
/*    */   }
/*    */   
/*    */   public WorldClient getWorld() {
/* 25 */     return this.world;
/*    */   }
/*    */   
/*    */   public Type getType() {
/* 29 */     return this.type;
/*    */   }
/*    */ }


/* Location:              C:\Users\adzc\Downloads\Meowtils-2.0.0.jar!\wtf\tatp\meowtils\event\WorldEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */