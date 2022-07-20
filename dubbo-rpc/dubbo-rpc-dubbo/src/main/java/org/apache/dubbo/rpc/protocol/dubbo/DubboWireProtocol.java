package org.apache.dubbo.rpc.protocol.dubbo;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.Codec2;
import org.apache.dubbo.remoting.api.AbstractWireProtocol;
import org.apache.dubbo.remoting.api.pu.NioChannel;
import org.apache.dubbo.remoting.transport.netty4.NettyCodecAdapter;
import org.apache.dubbo.remoting.transport.netty4.NettyServerHandler;

@Activate
public class DubboWireProtocol extends AbstractWireProtocol {
    public DubboWireProtocol() {
        super(new DubboDetector());
    }

    @Override
    public void configServerPipeline(URL url, Channel channel, ChannelHandler channelHandler) {

    }
}
