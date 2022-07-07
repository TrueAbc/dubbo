package org.apache.dubbo.rpc.protocol.dubbo.newportunification;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.api.newportunification.ChannelWithHandler;
import org.apache.dubbo.remoting.api.newportunification.NewProtocolDetector;
import org.apache.dubbo.remoting.api.newportunification.NewWireProtocol;

public class DubboNewWireProtocol implements NewWireProtocol {
    private final NewProtocolDetector detector = new DubboNewDetector();
    @Override
    public NewProtocolDetector detector() {
        return detector;
    }

    @Override
    public void configServerPipeline(URL url, ChannelWithHandler ch) {
        // 理论上来说只需要将channel with handler的最外层内容剥离
        ch.setUrl(url);
        // 返回的应该是装饰器内层的handler,
        ch.setHandler(ch.getChannelHandler());
    }

    @Override
    public void configClientPipeline(URL url, ChannelWithHandler ch) {

    }

    @Override
    public void close() {

    }
}
