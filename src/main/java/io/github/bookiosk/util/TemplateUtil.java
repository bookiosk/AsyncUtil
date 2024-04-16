package io.github.bookiosk.util;

import io.github.bookiosk.callback.ICallback;
import io.github.bookiosk.worker.IWorker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bookiosk
 */
public abstract class TemplateUtil {


    public void parseFileTemplate (String filePath) {
        changeFileContentToObject(filePath);
        acquireIWorkerInstance();
        acquireICallbackInstance();
        acquireWorkerWrapperInstance();
    }

    public abstract void changeFileContentToObject(String filePath);

    public abstract void acquireIWorkerInstance();
    public abstract void acquireICallbackInstance();
    public abstract void acquireWorkerWrapperInstance();
}
