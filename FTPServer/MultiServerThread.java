package FTPServer;

import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class MultiServerThread extends Thread {
    private Socket socket = null;

    String inputLine;

    public MultiServerThread(Socket socket) {
        super("MultiServerThread");
        this.socket = socket;
    }

    public void run() {

        System.out.println("Connection received from: " + socket.getInetAddress().getHostName());

        try (
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ) {
            out.flush();

            while ((inputLine = (String) in.readObject()) != null) {
                if (inputLine.equalsIgnoreCase("dir")) {
                    File f = new File("FTPServer");
                    out.writeObject(f.listFiles());
                    out.flush();
                } else {
                    String[] inputLineSplit = inputLine.split(" ");
                    if (inputLineSplit[0].equalsIgnoreCase("upload")) {
                        if(!(boolean)in.readObject())   continue;
                        byte[] content = (byte[]) in.readObject();
                        Files.write(Path.of("FTPServer", inputLineSplit[1]), content);
                    } else if (inputLineSplit[0].equalsIgnoreCase("get")) {
                        try {
                            byte[] content = Files.readAllBytes(Path.of("FTPServer", inputLineSplit[1]));
                            out.writeObject(true);
                            out.writeObject(content);
                            out.flush();
                            System.out.println("File received");
                        } catch (Exception e) {
                            out.writeObject(false);
                            out.flush();
                            System.out.println("File not found");
                        }
                    }
                }
            }
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}