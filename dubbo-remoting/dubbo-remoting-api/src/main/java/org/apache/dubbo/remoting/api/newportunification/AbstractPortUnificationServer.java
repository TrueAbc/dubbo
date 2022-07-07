package org.apache.dubbo.remoting.api.newportunification;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.transport.AbstractServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// 继承了abstract server作为连接管理handler的能力
// 自身管理wire protocol 和protocol
public abstract class AbstractPortUnificationServer extends AbstractServer {
    private static final Logger logger = LoggerFactory.getLogger(AbstractPortUnificationServer.class);

    public List<NewWireProtocol> getProtocols() {
        return protocols;
    }

    public List<URL> getUrls() {
        return urls;
    }

    public ConcurrentMap<NewWireProtocol, URL> getWireProtocolURLConcurrentMap() {
        return wireProtocolURLConcurrentMap;
    }

    private final List<NewWireProtocol> protocols;

    private final List<URL> urls;
    private final ConcurrentMap<NewWireProtocol, URL> wireProtocolURLConcurrentMap = new ConcurrentHashMap<>();

    public AbstractPortUnificationServer(URL url, ChannelHandler handler) throws RemotingException {
        super(url, handler);
        this.protocols = new ArrayList<>();
        this.urls = new ArrayList<>();

        this.urls.add(url);
        final NewWireProtocol wp = ExtensionLoader.getExtensionLoader(NewWireProtocol.class).getExtension(url.getProtocol());
        this.protocols.add(wp);
        this.wireProtocolURLConcurrentMap.put(wp, url);
    }


    public void AddNewUrl(URL url) {
        System.out.println("true abc add url to server :" + url);
        this.urls.add(url);
        final NewWireProtocol wp = ExtensionLoader.getExtensionLoader(NewWireProtocol.class).getExtension(url.getProtocol());

        this.wireProtocolURLConcurrentMap.put(wp, url);
        this.protocols.add(wp);
    }
    // url 包含连接建立和断开的事件重写, 并且会将事件交给上层
}
