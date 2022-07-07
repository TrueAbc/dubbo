package org.apache.dubbo.remoting.api.newportunification;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionScope;
import org.apache.dubbo.common.extension.SPI;

// 导入进来之后, 通过url获取到指定的protocol, 随后修改Channel持有的handler对象
@SPI(scope = ExtensionScope.FRAMEWORK)
public interface NewWireProtocol {
    NewProtocolDetector detector();

    // 设置channel的对应事件
    void configServerPipeline(URL url, ChannelWithHandler ch);

    void configClientPipeline(URL url, ChannelWithHandler ch);

    void close();
}
