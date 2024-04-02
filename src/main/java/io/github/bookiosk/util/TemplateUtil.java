package io.github.bookiosk.util;

import io.github.bookiosk.callback.ICallback;
import io.github.bookiosk.exception.AsyncException;
import io.github.bookiosk.worker.IWorker;

/**
 * @author bookiosk
 */
public class TemplateUtil {




    /**
     *
     * @param clazz 全限定类名
     * @return 实例对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T newIWorkerInstance(String clazz) throws AsyncException {
        AssertUtil.notEmpty(clazz);
        try {
            Class<?> instanceClass = Class.forName(clazz);
            if (!IWorker.class.isAssignableFrom(instanceClass)) {
                throw new AsyncException("this argument is not is assigned from IWorker");
            }
            return (T) Class.forName(clazz).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new AsyncException(e, "Instance class [{}] error!", clazz);
        }
    }

    /**
     *
     * @param clazz 全限定类名
     * @return 实例对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T newICallBackInstance(String clazz) throws AsyncException {
        AssertUtil.notEmpty(clazz);
        try {
            Class<?> instanceClass = Class.forName(clazz);
            if (!ICallback.class.isAssignableFrom(instanceClass)) {
                throw new AsyncException("this argument is not is assigned from ICallBack");
            }
            return (T) Class.forName(clazz).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new AsyncException(e, "Instance class [{}] error!", clazz);
        }
    }
}
