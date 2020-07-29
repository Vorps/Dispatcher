package net.vorps.dispatcher;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Server {

    protected final @Getter String name;
    protected final @Getter int port;
    protected @Setter boolean canPlay;
    protected int nb_player;

    public Server(String name, int port) {
        this.name = name;
        this.port = port;
        this.canPlay = true;
        this.nb_player = 0;
        Server.servers.put(this.name, this);
    }

    protected static HashMap<String, Server> servers;

    public static String getTypeServer(String name) {
        return name.split("_")[0];
    }

    public static Server getServer(String name){
        return Server.servers.get(name);
    }

    public static ArrayList<Server> getServersByServerType(String serverType){
        ArrayList<Server> servers = new ArrayList<>();
        for (Server server : Server.servers.values()) {
            if(Server.getTypeServer(server.name).equals(serverType)) servers.add(server);
        }
        return servers;
    }

    public void removeServer() {
        try{
            Dispatcher.removeServer(this);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
