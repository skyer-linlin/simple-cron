package com.lin.simplecron.utils;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

/**
 * com.lin.simplecron.utils
 *
 * @author quanlinlin
 * @date 2022/9/15 18:17
 * @since
 */
@Slf4j
public class ObjPropsCopyUtil {


    /**
     * 用反射辅助完成对象属性拷贝,主要针对源对象属性是 string 而对应 domain 属性为 integer 的情况
     *
     * @param source
     * @param target
     * @param <S>
     * @param <T>
     */
    public static <S, T> void copyProperties(S source, T target) {
        Class<?> srcClass = source.getClass();
        Class<?> targetClass = target.getClass();

        Field[] srcFields = ReflectUtil.getFields(srcClass);
        Field[] targetFields = ReflectUtil.getFields(targetClass);

        for (Field targetField : targetFields) {
            targetField.setAccessible(true);
            for (Field srcField : srcFields) {
                srcField.setAccessible(true);
                try {
                    if (srcField.getName().equals(targetField.getName())) {
                        // 两边类型都一样, 直接拷贝, 只处理src 属性原来就是基本类型和 string 的属性, src 中对象类型属性不处理
                        if (targetField.getType() == srcField.getType() && (srcField.getType().isPrimitive() || srcField.getType() == String.class)) {
                            targetField.set(target, srcField.get(source));
                        } else if (srcField.getType() == String.class) { // 如果是 string, 可以通过类型转换复制值到新属性
                            String str = (String) srcField.get(source);
                            if (targetField.getType() == Integer.class) { // 如果目标属性是 integer,把 string 转换下就行
                                targetField.set(target, Integer.valueOf(str));
                            } else if (targetField.getType() == LocalDateTime.class) { // 把秒级时间戳转换为对象
                                targetField.set(target, LocalDateTimeUtil.of(Long.parseLong(str) * 1_000));
                            } else {
                                log.error("unknown filed, source filed: {}, type: {}, target field: {}, type: {}",
                                    srcField.getName(), srcField.getType(), targetField.getName(), targetField.getType());
                                throw new RuntimeException("Unknown field");
                            }
                        } else { // 如果是对象类型, 则不能简单复制
                            log.debug("对象类型属性,不用复制,field name: {}", targetField.getName());
                        }
                    }
                } catch (Exception e) {
                    try {
                        log.error("unknown filed, source filed: {}, type: {}, src field value: {}, target field: {}, type: {}",
                            srcField.getName(), srcField.getType(), srcField.get(source), targetField.getName(), targetField.getType());
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        }

    }
}
