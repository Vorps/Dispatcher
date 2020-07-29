package net.vorps.dispatcher;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Stream;

public class Dispatcher {

    private static final String baseDirectory = ".."+File.separator;

    public static void init() throws CreateServerException{
        try{
            for (ServerType serverType : ServerType.serversType.values()) {
                if(Server.getServersByServerType(serverType.getName()).isEmpty())
                    Dispatcher.createServer(serverType, 0, serverType.getPort_start(), serverType.getNb_player_max());
            }
        } catch (IOException e){
            throw new CreateServerException(e);
        }
    }


    public static Server connectServer(ServerType serverType, boolean play){
        for(Server server : Server.getServersByServerType(serverType.getName())){
            if(server.canPlay == play && server.nb_player < serverType.getNb_player_max()){
                server.nb_player++;
                return server;
            }
        }
        return null;
    }

    public static Server connectServer(Server server, boolean play){
        if(server.canPlay == play && server.nb_player < ServerType.getServerTypeFromName(Server.getTypeServer(server.name)).getNb_player_max()){
            return server;
        }
        return null;
    }

    private static void createServer(final ServerType serverType, final Integer number, final Integer port, final Integer max_player) throws CreateServerException, IOException{
        serverType.addPort(port);
        String nameServer = serverType.getName().toUpperCase()+"_"+String.format("%02d" , number);
        String directoryServerType = Dispatcher.baseDirectory+serverType.getName().toUpperCase()+File.separator;
        String directoryServer = directoryServerType+nameServer+File.separator;
        File directoryServerTypeFile = new File(directoryServerType).getCanonicalFile();
        File directoryServerFile = new File(directoryServer).getCanonicalFile();
        if(!directoryServerTypeFile.exists()) if(!directoryServerTypeFile.mkdir()) throw new CreateServerException("Create directory : "+directoryServerTypeFile+" failed !");
        if(!directoryServerFile.exists()) if(!directoryServerFile.mkdir()) throw new CreateServerException("Create directory : "+directoryServerFile+" failed !");
        String directoryResource = Dispatcher.baseDirectory+File.separator+"Resources"+File.separator;
        // Spigot copy
        if(!new File(directoryResource+"spigot.jar").exists()) throw new CreateServerException("spigot.jar dont exist !");
        if(!new File(directoryServer+"spigot.jar").exists()) Files.copy(Paths.get(directoryResource+"spigot.jar"), Paths.get(directoryServer+"spigot.jar"));
        // eula copy
        if(!new File(directoryResource+"eula.txt").exists()) throw new CreateServerException("eula.txt dont exist !");
        if(!new File(directoryServer+"eula.txt").exists()) Files.copy(Paths.get(directoryResource+"eula.txt"), Paths.get(directoryServer+"eula.txt"));
        // server.properties copy
        if(!new File(directoryResource+"server.properties").exists()) throw new CreateServerException("server.properties dont exist !");
        if(!new File(directoryServer+"server.properties").exists()) Files.copy(Paths.get(directoryResource+"server.properties"), Paths.get(directoryServer+"server.properties"));

        // Plugin copy
        if(!new File(directoryResource+serverType.getName().toUpperCase()+File.separator+"plugins").exists()) throw new CreateServerException("plugins dont exist !");
        Dispatcher.copyFolder(directoryResource+serverType.getName().toUpperCase()+File.separator+"plugins", directoryServer+"/plugins");

        // World copy
        if(!new File(directoryResource+serverType.getName().toUpperCase()+File.separator+"world").exists()) throw new CreateServerException("world dont exist !");
        Dispatcher.copyFolder(directoryResource+serverType.getName().toUpperCase()+File.separator+"world", directoryServer+File.separator+"world");

        ProcessBuilder processBuilder = new ProcessBuilder("screen","-dmS", nameServer,"java", "-Xms1G", "-Xmx1G", "-DIReallyKnowWhatIAmDoingISwear", "-jar", "spigot.jar", "--nogui", "-p", port.toString(), "-s", max_player.toString(), "-o", "false");
        processBuilder.inheritIO();
        processBuilder.directory(directoryServerFile);
        processBuilder.start();
    }

    protected static void removeServer(final Server server) throws IOException {
        ServerType serverType =  ServerType.getServerTypeFromName(Server.getTypeServer(server.name));
        serverType.removePort(server.port);
        Files.walk(Paths.get(Dispatcher.baseDirectory+server.name.split("_")[0]+File.separator+server.name))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    private static void copyFolder(String src, String dest) throws IOException {
        if(!new File(dest).exists()){
            try (Stream<Path> stream = Files.walk(Paths.get(src))) {
                stream.forEach(source -> {
                    try {
                        Files.copy(source, Paths.get(dest).resolve(Paths.get(src).relativize(source)), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}
