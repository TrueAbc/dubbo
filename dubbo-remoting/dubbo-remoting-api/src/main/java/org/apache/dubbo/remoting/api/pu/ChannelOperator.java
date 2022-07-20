package org.apache.dubbo.remoting.api.pu;

import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.ChannelHandler;

import java.util.List;

public interface ChannelOperator {
    public void configChannelHandler(List<ChannelHandler> handlerList);
}
