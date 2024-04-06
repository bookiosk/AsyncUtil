package io.github.bookiosk.util;


import cn.hutool.json.JSONUtil;
import io.github.bookiosk.exception.AsyncException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

/**
 * @author bookiosk
 */
public class ReflectUtil {

    private static final Logger logger = LoggerFactory.getLogger(ReflectUtil.class);

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String clazz) throws AsyncException {
        AssertUtil.notEmpty(clazz);
        try {
            return (T) Class.forName(clazz).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new AsyncException(e, "Instance class [{}] error!", clazz);
        }
    }

}
