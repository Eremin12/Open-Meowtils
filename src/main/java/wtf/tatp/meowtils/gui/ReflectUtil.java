/*    */ package wtf.tatp.meowtils.gui;
/*    */ 
/*    */ import java.lang.reflect.Field;
/*    */ import wtf.tatp.meowtils.Meowtils;
/*    */ 
/*    */ public class ReflectUtil
/*    */ {
/*    */   public static Field bindField(Object owner, String fieldName, Class<?> expectedType) {
/*  9 */     if (owner == null || fieldName == null || fieldName.isEmpty()) return null;
/*    */     
/*    */     try {
/* 12 */       Field field = owner.getClass().getDeclaredField(fieldName);
/* 13 */       field.setAccessible(true);
/*    */       
/* 15 */       if (expectedType != null && !isTypeCompatible(field.getType(), expectedType)) {
/* 16 */         Meowtils.warn("Field " + fieldName + " type mismatch, expected: " + expectedType.getSimpleName() + " but got " + field.getType().getSimpleName());
/*    */       }
/*    */       
/* 19 */       return field;
/* 20 */     } catch (NoSuchFieldException e) {
/* 21 */       throw new RuntimeException("Invalid config field (" + fieldName + ") in " + owner.getClass().getSimpleName());
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public static <T> T get(Field field, Object instance, T def) {
/* 27 */     if (field == null || instance == null) return def; 
/*    */     try {
/* 29 */       Object value = field.get(instance);
/* 30 */       if (value == null) return def; 
/* 31 */       return (T)value;
/* 32 */     } catch (Exception e) {
/* 33 */       Meowtils.error("Failed to get field value: " + e);
/* 34 */       return def;
/*    */     } 
/*    */   }
/*    */   
/*    */   public static boolean set(Field field, Object instance, Object value) {
/* 39 */     if (field == null || instance == null) return false; 
/*    */     try {
/* 41 */       if (value instanceof Number && Number.class.isAssignableFrom(field.getType())) {
/* 42 */         value = castNumber(((Number)value).doubleValue(), field.getType());
/*    */       }
/* 44 */       field.set(instance, value);
/* 45 */       return true;
/* 46 */     } catch (Exception e) {
/* 47 */       Meowtils.error("Failed to set field " + field.getName() + ": " + e);
/* 48 */       return false;
/*    */     } 
/*    */   }
/*    */   
/*    */   public static Object castNumber(double value, Class<?> type) {
/* 53 */     if (type == int.class || type == Integer.class) return Integer.valueOf((int)value); 
/* 54 */     if (type == float.class || type == Float.class) return Float.valueOf((float)value); 
/* 55 */     if (type == long.class || type == Long.class) return Long.valueOf((long)value); 
/* 56 */     if (type == double.class || type == Double.class) return Double.valueOf(value); 
/* 57 */     throw new IllegalArgumentException("Unsupported numeric type: " + type.getName());
/*    */   }
/*    */ 
/*    */   
/*    */   private static boolean isTypeCompatible(Class<?> fieldType, Class<?> expected) {
/* 62 */     if (fieldType == expected) return true;
/*    */     
/* 64 */     if (fieldType.isPrimitive()) return (wrap(fieldType) == expected); 
/* 65 */     if (expected.isPrimitive()) return (wrap(expected) == fieldType);
/*    */     
/* 67 */     if (Number.class.isAssignableFrom(fieldType) && Number.class.isAssignableFrom(expected)) return true;
/*    */     
/* 69 */     if ((fieldType == Boolean.class || fieldType == boolean.class) && (expected == Boolean.class || expected == boolean.class)) return true;
/*    */     
/* 71 */     return expected.isAssignableFrom(fieldType);
/*    */   }
/*    */   
/*    */   private static Class<?> wrap(Class<?> c) {
/* 75 */     if (c == int.class) return Integer.class; 
/* 76 */     if (c == float.class) return Float.class; 
/* 77 */     if (c == double.class) return Double.class; 
/* 78 */     if (c == long.class) return Long.class; 
/* 79 */     if (c == boolean.class) return Boolean.class; 
/* 80 */     if (c == byte.class) return Byte.class; 
/* 81 */     if (c == short.class) return Short.class; 
/* 82 */     if (c == char.class) return Character.class; 
/* 83 */     return c;
/*    */   }
/*    */ }


/* Location:              C:\Users\adzc\Downloads\Meowtils-2.0.0.jar!\wtf\tatp\meowtils\gui\ReflectUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */