/*    */ package wtf.tatp.meowtils.handler.command;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import net.minecraft.command.CommandException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class ClientCommand
/*    */ {
/*    */   public abstract String getName();
/*    */   
/*    */   public abstract void process(String[] paramArrayOfString) throws CommandException;
/*    */   
/*    */   public List<String> getAliases() {
/* 24 */     return Collections.emptyList();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean tabCompleteNames() {
/* 31 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\adzc\Downloads\Meowtils-2.0.0.jar!\wtf\tatp\meowtils\handler\command\ClientCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */