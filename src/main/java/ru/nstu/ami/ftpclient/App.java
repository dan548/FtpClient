package ru.nstu.ami.ftpclient;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws ParseException {
        Options options = new Options();

        options.addOption("s", "server", true, "server address");
        options.addOption("t", "port", true, "port number");
        options.addOption("u", "user", true, "username");
        options.addOption("p", "password", true, "password");

        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = parser.parse(options, args);

        String server = cmd.getOptionValue("server");
        int port = Integer.parseInt(cmd.getOptionValue("port"));
        String user = cmd.getOptionValue("user");
        String password = cmd.getOptionValue("password");

        FtpClient client = new FtpClient(server, port, user, password);

        boolean run;

        try {
            client.open();
            run = true;
            Scanner sc = new Scanner(System.in);
            while (run) {
                String[] tokens = sc.nextLine().split(" ");
                switch (tokens[0]) {
                    case "stop":
                        run = false;
                        break;
                    case "ls":
                        if (tokens.length == 2) {
                            for (String str : client.listFiles(tokens[1])) {
                                System.out.println(str);
                            }
                        } else {
                            System.out.println("No such command!");
                        }
                        break;
                    case "download":
                        if (tokens.length == 3) {
                            client.downloadFile(tokens[1], tokens[2]);
                        } else {
                            System.out.println("No such command!");
                        }
                        break;
                    case "upload":
                        if (tokens.length == 3) {
                            client.putFileToPath(new File(tokens[1]), tokens[2]);
                        } else {
                            System.out.println("No such command!");
                        }
                        break;
                    case "rename":
                        if (tokens.length == 3) {
                            boolean res = client.renameFile(tokens[1], tokens[2]);
                            if (res) {
                                System.out.println("OK");
                            } else {
                                System.out.println("No renaming!");
                            }
                        } else {
                            System.out.println("No such command!");
                        }
                        break;
                    case "rm":
                        if (tokens.length == 2) {
                            boolean res = client.deleteFile(tokens[1]);
                            if (res) {
                                System.out.println("OK");
                            } else {
                                System.out.println("No deleting!");
                            }
                        } else {
                            System.out.println("No such command!");
                        }
                        break;
                    case "jump":
                        if (tokens.length == 2) {
                            boolean res = client.changeDirectory(tokens[1]);
                            if (res) {
                                System.out.println("OK");
                            } else {
                                System.out.println("No changing workdir!");
                            }
                        } else {
                            System.out.println("No such command!");
                        }
                        break;
                    case "mkdir":
                        if (tokens.length == 2) {
                            boolean res = client.makeDirectory(tokens[1]);
                            if (res) {
                                System.out.println("OK");
                            } else {
                                System.out.println("No making directory!");
                            }
                        } else {
                            System.out.println("No such command!");
                        }
                        break;
                    case "rmdir":
                        if (tokens.length == 2) {
                            boolean res = client.removeDirectory(tokens[1]);
                            if (res) {
                                System.out.println("OK");
                            } else {
                                System.out.println("No removing!");
                            }
                        } else {
                            System.out.println("No such command!");
                        }
                        break;
                    default:
                        System.out.println("No such command!");
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
