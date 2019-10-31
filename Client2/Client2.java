package Client2;

import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class Client2 {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";

    Socket requestSocket;           //socket connect to the server
    boolean connectEstablished = false;      //connection established
    ObjectOutputStream out;         //stream write to the socket
    ObjectInputStream in;          //stream read from the socket
    String command;                //message send to the server

    public static void main(String args[])
    {
        Client2 client = new Client2();
        client.run();
    }

    void run()
    {
        try{
            //get Input from standard input
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Enter IP and port (correct usage: ftpclient <IP Port>):");
            command = bufferedReader.readLine();

            while(!verifyFtpCommand(command))   {

                System.err.println(ANSI_RED + "Invalid Command!" + ANSI_RESET);
                System.out.println("Enter IP and port (correct usage: ftpclient <IP Port>):");
                command = bufferedReader.readLine();
            }

            while(!connectEstablished)  {
                //create a socket to connect to the server
                try{
                    if(!command.split(" ")[1].equalsIgnoreCase("localhost") && !command.split(" ")[1].equalsIgnoreCase("127.0.0.1"))
                        throw new UnknownHostException();
                    requestSocket = new Socket(command.split(" ")[1], Integer.parseInt(command.split(" ")[2]));
                    connectEstablished = true;
                } catch (ConnectException e)   {
                    System.err.println(ANSI_RED + "Connection Unsuccessful! Server not started." + ANSI_RESET);
                    System.out.println("Enter IP and port (correct usage: ftpclient <IP Port>):");
                    command = bufferedReader.readLine();
                } catch(UnknownHostException unknownHost){
                    System.err.println(ANSI_RED + "You are trying to connect to an unknown host!" + ANSI_RESET);
                    System.out.println("Enter IP and port (correct usage: ftpclient <IP Port>):");
                    command = bufferedReader.readLine();
                } catch(Exception e) {
                    System.err.println(ANSI_RED + e.toString() + ANSI_RESET);
                    System.out.println("Enter IP and port (correct usage: ftpclient <IP Port>):");
                    command = bufferedReader.readLine();
                }
            }

            System.out.println(ANSI_GREEN + "Connected to "+ command.split(" ")[1] +" in port " + command.split(" ")[2] + ANSI_RESET);
            //initialize inputStream and outputStream
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(requestSocket.getInputStream());

            while (true) {
                System.out.print(ANSI_BLUE + "(cmd) Client$ " + ANSI_RESET);
                //read a sentence from the standard input
                command = bufferedReader.readLine();
                //Send the sentence to the server

                if (command.equalsIgnoreCase("ls")) {
                    File f = new File("Client2");
                    File[] files = f.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        System.out.println(files[i].getName());
                    }
                } else if (command.equalsIgnoreCase("dir")) {
                    out.writeObject(command);
                    out.flush();
                    File[] files = (File[]) in.readObject();
                    for (int i = 0; i < files.length; i++) {
                        System.out.println(files[i].getName());
                    }
                } else {
                    String[] command_split = command.split(" ");
                    if (command_split[0].equalsIgnoreCase("upload")) {
                        out.writeObject(command);
                        out.flush();

                        try {
                            byte[] content = Files.readAllBytes(Path.of("Client2", command_split[1]));
                            out.writeObject(true);
                            out.writeObject(content);
                            out.flush();
                            System.out.println(ANSI_GREEN + "File sent to server!" + ANSI_RESET);
                        } catch (NoSuchFileException e) {
                            out.writeObject(false);
                            out.flush();
                            System.err.println(ANSI_RED + "File not found" + ANSI_RESET);
                        }
                    } else if (command_split[0].equalsIgnoreCase("get")) {
                        out.writeObject(command);
                        out.flush();

                        if (!(boolean) in.readObject()) {
                            System.err.println(ANSI_RED + "File not present at Server" + ANSI_RESET);
                            continue;
                        }
                        String fileName = command_split[1];
                        byte[] content = (byte[]) in.readObject();
                        Files.write(Path.of("Client2", fileName), content);
                        System.out.println(ANSI_GREEN + "File Downloaded!" + ANSI_RESET);
                    } else {
                        System.err.println(ANSI_RED + "Invalid Command" + ANSI_RESET);
                    }
                }
            }
        } catch (ClassNotFoundException e ) {
            System.err.println(ANSI_RED + "Class not found");
        } catch(IOException ioException){
            ioException.printStackTrace();
        } finally{
            //Close connections
            try{
                in.close();
                out.close();
                requestSocket.close();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }
    }

    private boolean verifyFtpCommand(String command) {
        String[] command_split = command.split(" ");

        if(command_split.length != 3)   return false;

        if(!command_split[0].equalsIgnoreCase("ftpclient")) return false;

        try{
            Integer.parseInt(command_split[2]);
        } catch (NumberFormatException e)   {
            return false;
        }

        return true;
    }
}
