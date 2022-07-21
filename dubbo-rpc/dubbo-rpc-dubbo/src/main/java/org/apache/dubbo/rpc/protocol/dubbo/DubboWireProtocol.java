package org.apache.dubbo.rpc.protocol.dubbo;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.Codec2;
import org.apache.dubbo.remoting.api.AbstractWireProtocol;
import org.apache.dubbo.remoting.api.pu.ChannelHandlerPretender;
import org.apache.dubbo.remoting.api.pu.ChannelOperator;

import java.util.ArrayList;
import java.util.List;

@Activate
public class DubboWireProtocol extends AbstractWireProtocol {
    public DubboWireProtocol() {
        super(new DubboDetector());
    }

    @Override
    public void configServerPipeline(URL url, ChannelOperator channelOperator) {
        List<ChannelHandler> handlerList = new ArrayList<>();
        channelOperator.configChannelHandler(handlerList);
    }
}
