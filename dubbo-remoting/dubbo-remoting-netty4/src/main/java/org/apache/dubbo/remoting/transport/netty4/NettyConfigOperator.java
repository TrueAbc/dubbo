package org.apache.dubbo.remoting.transport.netty4;


import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.Codec2;
import org.apache.dubbo.remoting.api.pu.ChannelHandlerPretender;
import org.apache.dubbo.remoting.api.pu.ChannelOperator;
import org.apache.dubbo.remoting.api.pu.DefaultCodec;
import org.apache.dubbo.remoting.api.pu.DefaultPuHandler;

import java.util.List;

public class NettyConfigOperator implements ChannelOperator {

    public NettyConfigOperator(NettyChannel channel, ChannelHandler handler) {
        this.channel = channel;
        this.handler = handler;
    }

    @Override
    public void configChannelHandler(List<ChannelHandler> handlerList) {
        if(channel instanceof NettyChannel) {
            URL url = channel.getUrl();
            Codec2 codec2 = url.getOrDefaultFrameworkModel().getExtensionLoader(Codec2.class).
                getExtension(url.getProtocol());
            if (!(codec2 instanceof DefaultCodec)){
                NettyCodecAdapter codec = new NettyCodecAdapter(codec2, channel.getUrl(), handler);
                ((NettyChannel) channel).getNioChannel().pipeline().addLast(
                    codec.getDecoder()
                ).addLast(
                    codec.getEncoder()
                );
            }

            for (ChannelHandler handler: handlerList) {
                if (handler instanceof ChannelHandlerPretender) {
                    Object realHandler = ((ChannelHandlerPretender) handler).getRealHandler();
                    if(realHandler instanceof io.netty.channel.ChannelHandler) {
                        ((NettyChannel) channel).getNioChannel().pipeline().addLast(
                            (io.netty.channel.ChannelHandler) realHandler
                        );
                    }
                }
            }

            // triple的codec和channel handler都是default的，不进行任何操作
            if (! (handler instanceof DefaultPuHandler)){
                // handler有用处, 对于dubbo协议的场景, handler已经传递下来了
                // 构建netty的pipeline需要使用, 而triple协议则不需要使用, 传递的都是伪装的handler
                NettyServerHandler sh = new NettyServerHandler(channel.getUrl(), handler);
                ((NettyChannel) channel).getNioChannel().pipeline().addLast(
                    sh
                );
            }
        }
    }

    private final Channel channel;
    private ChannelHandler handler;
}
