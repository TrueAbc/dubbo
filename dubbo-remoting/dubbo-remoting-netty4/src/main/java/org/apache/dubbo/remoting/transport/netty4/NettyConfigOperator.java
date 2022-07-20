package org.apache.dubbo.remoting.transport.netty4;


import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.api.pu.ChannelHandlerPretender;
import org.apache.dubbo.remoting.api.pu.ChannelOperator;

import java.util.List;

public class NettyConfigOperator implements ChannelOperator {

    public NettyConfigOperator(NettyChannel channel) {
        this.channel = channel;
    }

    @Override
    public void configChannelHandler(List<ChannelHandler> handlerList) {
        if(channel instanceof NettyChannel) {
            for (ChannelHandler handler: handlerList) {
                if (handler instanceof ChannelHandlerPretender) {
                    ((NettyChannel) channel).getNioChannel().pipeline().addLast(
                        (io.netty.channel.ChannelHandler) handler
                    );
                }
            }
        }
    }

    private Channel channel;
}
