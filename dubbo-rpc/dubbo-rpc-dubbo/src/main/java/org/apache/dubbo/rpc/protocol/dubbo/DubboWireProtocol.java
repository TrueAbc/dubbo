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
        if (channel instanceof NioChannel) {
            io.netty.channel.Channel ch = (io.netty.channel.Channel) ((NioChannel<?>) channel).getNioChannel();
            NettyServerHandler sh = new NettyServerHandler(url, channelHandler);
            Codec2 codec2 = ExtensionLoader.getExtensionLoader(Codec2.class).getExtension(url.getProtocol());

            NettyCodecAdapter codec = new NettyCodecAdapter(codec2, url, channelHandler);
            ch.pipeline().addLast("decoder", codec.getDecoder())
                .addLast("encoder", codec.getEncoder())
                .addLast("handler", sh);
        }
    }
}
