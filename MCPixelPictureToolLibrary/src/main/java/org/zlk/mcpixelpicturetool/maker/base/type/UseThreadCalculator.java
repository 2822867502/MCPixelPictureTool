package org.zlk.mcpixelpicturetool.maker.base.type;

public class UseThreadCalculator {
    public static final int SET_PER_THREAD_TASK = 0;
    public static final int SET_USE_THREAD = 1;
    private final int constData;
    private final int mode;
    private int taskCount = 0;
    public UseThreadCalculator(int constData, int mode) {
        this.constData = constData;
        this.mode = mode;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }


    public int getUseThread() {
        return mode == SET_USE_THREAD ?
                constData :
                (taskCount % constData == 0 ? taskCount / constData : taskCount / constData + 1);
    }
    public int getPerThreadTask() {
        return mode == SET_PER_THREAD_TASK ?
                constData :
                (taskCount % constData == 0? taskCount / constData : taskCount / constData + 1);
    }
    public int getStartIndex(int i) {
        return i * getPerThreadTask();
    }

    public int getEndIndex(int i) {
        return (i + 1) * getPerThreadTask();
    }
}
