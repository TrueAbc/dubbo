package org.apache.dubbo.remoting.api.pu;

import org.apache.dubbo.remoting.transport.ChannelHandlerAdapter;

public class ChannelHandlerPretender extends ChannelHandlerAdapter {
    private final Object realHandler;

    public ChannelHandlerPretender(Object realHandler) {
        this.realHandler = realHandler;
    }

    public Object getRealHandler() {
        return realHandler;
    }

}
