package cn.gl.analysis.load;

/**
 * 加载文件名，然后分配给读取进程读取文件内容
 */
public interface Loader {

    /**
     * 加载所有文件名到内存中
     * @return true, 加载成功， false 加载失败
     */
    boolean load();

}
