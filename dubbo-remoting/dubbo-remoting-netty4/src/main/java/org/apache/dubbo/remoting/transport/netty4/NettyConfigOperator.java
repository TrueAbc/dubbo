package org.apache.dubbo.remoting.transport.netty4;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.Codec2;
import org.apache.dubbo.remoting.api.pu.NioChannel;
import org.apache.dubbo.rpc.model.ScopeModelUtil;


public class NettyConfigOperator {
    public static  void configServerPipeline(URL url, Channel channel, ChannelHandler handler) {
        if (channel instanceof NettyChannel && url.getProtocol().startsWith("dubbo")) {
            io.netty.channel.Channel ch = ((NettyChannel) channel).getNioChannel();
            System.out.println("abc netty config server pipeline");
            NettyServerHandler sh = new NettyServerHandler(url, handler);
            Codec2 codec2 = ExtensionLoader.getExtensionLoader(Codec2.class).getExtension(url.getProtocol());

            NettyCodecAdapter codec = new NettyCodecAdapter(codec2, url, handler);
            ch.pipeline().addLast("decoder", codec.getDecoder())
                .addLast("encoder", codec.getEncoder())
                .addLast("handler", sh);
            System.out.println("abc finished config server pipeline");
        }

    }
}
