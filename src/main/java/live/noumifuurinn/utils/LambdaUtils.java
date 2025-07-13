package live.noumifuurinn.utils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

public class LambdaUtils {

    public static <T, R> String getPropertyName(SerializableFunction<T, R> function) {
        try {
            Method method = function.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(function);

            String methodName = serializedLambda.getImplMethodName();

            // 处理 getter 方法名
            if (methodName.startsWith("get")) {
                return toCamelCase(methodName.substring(3));
            } else if (methodName.startsWith("is")) {
                return toCamelCase(methodName.substring(2));
            }

            return methodName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get property name", e);
        }
    }

    // 转换为驼峰命名
    public static String toCamelCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }
}