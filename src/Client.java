import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.crypto.*;

public class Client {

    private String publicKey;
    private String privateKey;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Sender messageSender;
    private String username;
    private BufferedReader reader;
    private ArrayList<String> groups;
    private int clientID;
    private String filePath;
    private String fileReceivePath;
    private KeyPair kp;
    private PublicKey chatKey;

    public static void main(String[] args) {
        new Client().run();
    }

    private void run() {
        //Sets up connection with the socket
        clientID = (int) Math.round(Math.random()*10000);
        connect();

        Receiver messageReceiver = new Receiver(inputStream);
        Thread receiverThread = new Thread(messageReceiver);
        receiverThread.start();

        reader = new BufferedReader(new InputStreamReader(System.in));

        groups = new ArrayList<>();
        messageSender = new Sender(outputStream);

        login();

        messageSender.send("HELO " + username);
        printMenu();
        try {
            receiverThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        try {
            String SERVER_ADDRESS = "localhost";
            int SERVER_PORT = 1337;
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void login() {
        System.out.println("Welcome to the chat server!" +
                "\nType a username to log in." +
                "\nUsername:");

        username = getInput();

    }

    private void printMenu() {
        boolean loggedIn = true;
        while(loggedIn) {
            System.out.println("0. Start chat with file sharing and encryption\n" +
                    "1. Send PM\n" +
                    "2. Create group chat\n" +
                    "3. Join group chat\n" +
                    "4. List of online users\n" +
                    "5. List of group chats\n" +
                    "6. Log off\n" +
                    "Type \"menu\" to show these options again\n" +
                    "Or write something to send to everyone:");

            String choice = getInput();

            // todo menu
            switch (choice) {
                case "0":
                    // todo send request for chat to other person
                    chat(true);
                    break;
                case "1":
                    // todo should not really start an entire chat
                    chat(false);
                    break;
                case "2":
                    createGroupChat();
                    break;
                case "3":
                    joinGroupChat();
                    break;
                case "4":
                    messageSender.list(true);
                    break;
                case "5":
                    messageSender.list(false);
                    break;
                case "6":
                    System.out.println("You logged out.");
                    messageSender.send("QUIT");
                    loggedIn = false;
                    break;
                default:
                    // for backwards compatibility a broadcast can be sent this way
                    messageSender.send("BCST " + choice);
                    System.out.println("Message sent to everyone. Please enter a number between 1 and 6 to use the menu.");
            }
        }
    }

    private String getInput() {

        String input = "";
        try {
            input = reader.readLine();
        } catch(IOException iox) {
            System.out.println(iox);
        }
        return input;
    }

    private void chat(boolean chat) {
        System.out.println("Which user do you want to chat with?");

        String user = getInput();

        messageSender.list(true);


        // todo what if user does not exist
        System.out.println("Chat started with " + user);
        // todo add options for BACK and ENC
        System.out.println("Type just the word FILE (uppercase) to send this user a file.\n" +
                "You both have to use ENC in order to start a secure conversation");

        do {
            String input = getInput();

            if(input.equals("FILE")) {
                try {
                    sendFile();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
            } else if(input.equals("ENC")) {
                // todo key exchange
                KeyPair kp = generateKeys();
                // todo send your own public key
                byte[] byteKey = kp.getPublic().getEncoded();
                System.out.println(byteKey);
                messageSender.send(input + " " + user + " " + byteKey);
                // todo start another method that the other client will also use
            } else if(input.equals("BACK")) {
                chat = false;
            } else {
                // todo between two users you can choose to encrypt the messages
                String message = user + " " + input;
                messageSender.send("PM " + message);
            }
            // todo option to stop messaging the other user
        } while (chat);
    }

    private void encChat() {

    }

    private KeyPair generateKeys() {
        KeyPairGenerator kpg;
        KeyPair kp = null;
        PublicKey pub;
        PrivateKey pvt;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            kp = kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException nsae) {}
//        pub = kp.getPublic();
//        pvt = kp.getPrivate();
        storeKeys(kp);
        return kp;
    }

    private void storeKeys(KeyPair kp) {

        String fileBase = "client" + clientID;

        try {
            FileOutputStream outk = new FileOutputStream(fileBase + ".key");
            outk.write(kp.getPrivate().getEncoded());
//            byte[] bytes = Files.readAllBytes(Paths.get(fileBase + ".key"));
//            X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
//            KeyFactory kf
//            try {
//                kf = KeyFactory.getInstance("RSA");
//            } catch (NoSuchAlgorithmException nsaex) {
//                System.out.println(nsaex.getMessage());
//            }
//            PublicKey pub = kf.generatePublic(ks);
            FileOutputStream outp = new FileOutputStream(fileBase + ".pub");
            outp.write(kp.getPublic().getEncoded());
        } catch (IOException ioex) {
            System.out.println(ioex.getMessage());
        }
    }

    private void sendFile() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        // todo file transfer implementation
        KeyPair kp = generateKeys();
        PublicKey pub = kp.getPublic();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, pub);

        try {
            FileInputStream in = new FileInputStream(new File(filePath));
            String filePathEnc = filePath + "Enc";
            FileOutputStream out = new FileOutputStream(new File(filePathEnc));
            processFile(cipher, in, out);

            // todo send this file
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void decryptFile(File encFile) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        PrivateKey pvt = kp.getPrivate();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, pvt);

        try {
            FileInputStream in = new FileInputStream(encFile);
            String filePathEnc = filePath + "Enc";
            FileOutputStream out = new FileOutputStream(new File(fileReceivePath));
            processFile(cipher, in, out);

            // todo send this file
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static private void processFile(Cipher ci,InputStream in,OutputStream out)
            throws javax.crypto.IllegalBlockSizeException,
            javax.crypto.BadPaddingException,
            java.io.IOException
    {
        byte[] ibuf = new byte[1024];
        int len;
        while ((len = in.read(ibuf)) != -1) {
            byte[] obuf = ci.update(ibuf, 0, len);
            if ( obuf != null ) out.write(obuf);
        }
        byte[] obuf = ci.doFinal();
        if ( obuf != null ) out.write(obuf);
    }

    private void createGroupChat() {
        String name;
        boolean unique;
        messageSender.send("GLST");
        do {
            System.out.println("Group name:");
            name = getInput();
            unique = !groups.contains(name);
            if(unique) {
                messageSender.send("GRUP " + name);
            } else {
                System.out.println("A group with this name already exists.");
            }
        } while (!unique);
        groups.add(name);
        groupChat(name, true);
    }

    private void joinGroupChat() {
        String name;
        boolean joinable;
        messageSender.send("GLST");
        System.out.println("Available groups:");
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (String grp : groups) {
                System.out.println(grp);
            }
            System.out.println("Group name (BACK to go back to the menu):");
            name = getInput();
            joinable = groups.contains(name);
            if(joinable) {
                if(name.equals("banned")) {
                    System.out.println("You have been banned from this group.\nAsk *leader* if they can unban you.");
                    // todo give name of the leader to contact
                } else {
                    // todo check if you are the leader of this group
                }
            } else if(name.equals("BACK")) {
                joinable = true;
            } else {
                System.out.println("This is not an existing group.");
            }
        } while (!joinable);
        if (!name.equals("BACK")) {
            groupChat(name, false); // todo also give leader boolean
        }
    }

    private void groupChat(String name, boolean leader) {

        if(leader) {
            System.out.println("You are the leader of this group.");
            System.out.println("Type KICK *username* to ban someone.");
            System.out.println("Type GDEL to delete the group.");
        }
        System.out.println("Type BACK to back out of the group.");
        boolean back = false;
        while(!back) {
            String input = getInput();
            if (leader) {
                String[] split = input.split("\\s+");
                String start = split[0];
                switch(start) {
                    case "KICK":
                        boolean success = messageSender.send(input);
                        // todo get feedback on success of the ban
                        break;
                    case "GDEL":
                        // todo ask for confirmation
                        // todo delete group
                        // todo remove active users
                        messageSender.send(input);
                        break;
                    case "BACK":
                        back = true;
                        break;
                    default:
                        // todo just send a message
                        // todo new protocol for this
                }
            } else {
                if(!input.startsWith("BACK")) {
                    // todo send message to everyone in the group
                    // todo new protocol for this
                    messageSender.send(input);
                } else {
                    // todo back out of group
                    back = true;
                }
            }
        }
    }

    private class Receiver implements Runnable {
        private BufferedReader reader;
        private boolean quit = false;
        // todo add way to quit when no messages should be able to be received

        private Receiver(InputStream inputStream) {
            reader = new BufferedReader(new InputStreamReader(inputStream));
        }

        public void run() {
            while (!quit) {
                try {
                    String message = reader.readLine();
                    if(message != null) {

                        // todo turn this into a switch statement
                        if (message.equals("PING")) {
                            messageSender.send("PONG");
                        }

                        if (!message.startsWith("+OK") && !message.startsWith("HELO") && !message.equals("PING")) { // TODO do something with these
                            System.out.println(message);
                        }

                        if(message.startsWith("GLST")) {
                            String groupString = message.substring(5);
                            groups.clear();
                            if(!groupString.equals("")) {
                                String[] groupNames = groupString.split(";");
                                groups.addAll(Arrays.asList(groupNames));
                            }
                        }

                        if(message.startsWith("ENC")) {

                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // todo runnable if sending a file?
    // todo runnable for awaiting confirmation of successful send
    private class Sender {

        private OutputStream outputStream;

        private Sender(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        // todo different methods for different messages or just switch statement?

        private boolean send(String message) {

//            if (!message.equals("QUIT") && !message.startsWith("HELO") && !message.startsWith("PM") && !message.equals("PONG")) {
//                message = "BCST " + message;
//            }

            PrintWriter writer = new PrintWriter(outputStream);
            writer.println(message);
            writer.flush();
            // todo check connection, wait for +OK, check connection - still necessary?
            return false;
        }

        private void list(boolean user) {
            PrintWriter writer = new PrintWriter(outputStream);
            if(user) {
                writer.println("ULST");
            } else {
                writer.println("GLST");
            }
            writer.flush();
        }
    }
}
