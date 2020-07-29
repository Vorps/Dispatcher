package net.vorps.dispatcher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;

public class ServerType {

    private @Getter final String name;
    private @Getter final int port_start;
    private @Getter final int nb_server_max;
    private @Getter final int nb_player_max;
    private @Getter final ArrayList<Integer> ports;

    protected static @Getter HashMap<String, ServerType> serversType = new HashMap<>();

    public ServerType(String name, int port_start, int nb_server_max, int nb_player_max){
        this.name = name;
        this.port_start = port_start;
        this.nb_server_max = nb_server_max;
        this.nb_player_max = nb_player_max;
        this.ports = new ArrayList<>();
        ServerType.serversType.put(this.name, this);
    }


    public int get_nb_server(){
        return this.ports.size();
    }

    public void addPort(int port){
        System.out.println(port);
        this.ports.add(port);
    }

    public void removePort(int port){
        this.ports.remove(Integer.valueOf(port));
    }

    public static ServerType getServerTypeFromName(String serverType){
        return ServerType.serversType.get(serverType);
    }

    public int getPort(){
        for(int i = 0; i < this.nb_server_max;i++){
            if(!this.ports.contains(i)) return this.port_start+i;
        }
        return -1;
    }

}
