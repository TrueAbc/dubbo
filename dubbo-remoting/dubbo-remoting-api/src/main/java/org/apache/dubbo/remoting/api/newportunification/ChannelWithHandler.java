package org.apache.dubbo.remoting.api.newportunification;


import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.Constants;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.transport.ChannelHandlerDelegate;
import org.apache.dubbo.remoting.utils.PayloadDropper;

// channel和handler之间的相互绑定
public abstract class ChannelWithHandler implements Channel, ChannelHandler {
    // url 也不能是绑定的, 需要在config server pipeline的时候确定
    // 基本是对abstract peer的方法的移动, 主要是改变handler为非final并添加set方法
    // 让这个handler再包装一下
    private ChannelHandler handler;


    // 检测当前channel的状态
    private volatile boolean closing;
    private volatile boolean closed;

    private volatile URL url;

    @Override
    public void close() {
        closed = true;
    }

    @Override
    public void close(int timeout) {
        close();
    }

    @Override
    public void startClose() {
        if (isClosed()) {
            return;
        }
        closing = true;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("url == null");
        }
        this.url = url;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    public boolean isClosing() {
        return closing && !closed;
    }

    // 添加的新方法
    public void setHandler(ChannelHandler handler) {
        this.handler = handler;
    }

    // 返回delegate内部装饰的handler
    @Override
    public ChannelHandler getChannelHandler() {
        if (handler instanceof ChannelHandlerDelegate) {
            return ((ChannelHandlerDelegate) handler).getHandler();
        } else {
            return handler;
        }
    }


    public ChannelWithHandler(ChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    public void send(Object message, boolean sent) throws RemotingException {
        if (isClosed()) {
            throw new RemotingException(this, "Failed to send message "
                + (message == null ? "" : message.getClass().getName()) + ":" + PayloadDropper.getRequestWithoutData(message)
                + ", cause: Channel closed. channel: " + getLocalAddress() + " -> " + getRemoteAddress());
        }
    }

    @Override
    public void send(Object message) throws RemotingException {
        if (url != null) {
            send(message, url.getParameter(Constants.SENT_KEY, false));
        }else {
            send(message, false);
        }
    }

    @Override
    public String toString() {
        return getLocalAddress() + " -> " + getRemoteAddress();
    }


    // 现在channel自身拥有了处理五种事件的能力
    @Override
    public void connected(Channel ch) throws RemotingException {
        if (closed) {
            return;
        }
        handler.connected(ch);
    }

    @Override
    public void disconnected(Channel ch) throws RemotingException {
        handler.disconnected(ch);
    }

    @Override
    public void sent(Channel ch, Object msg) throws RemotingException {
        if (closed) {
            return;
        }
        handler.sent(ch, msg);
    }

    @Override
    public void received(Channel ch, Object msg) throws RemotingException {
        if (closed) {
            return;
        }
        handler.received(ch, msg);
    }

    @Override
    public void caught(Channel ch, Throwable ex) throws RemotingException {
        handler.caught(ch, ex);
    }
}
