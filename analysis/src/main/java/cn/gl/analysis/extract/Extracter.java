package cn.gl.analysis.extract;

import java.util.Map;
import java.util.Set;

/**
 * 提取目标：
 * 锚文本
 * 标题
 * 正文标题
 * 正文
 * 正向链接
 * 作者写作时间
 * ...
 */
public interface Extracter {

    void extract();

}
