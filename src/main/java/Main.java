import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Main extends JFrame
{
    private static String username = "ProperPoli777";
    private static String gamemode = "offline";
    private static int count = 0;
    private static boolean automatic = true;
    public static int playersOnline;
    public static int maxplayersonline;
    public static String level = "Loading";
    public static String lastgamemode;
    public static String online;
    public static Boolean startUp = true;

    public static int getPlayersOnline() {
        try {
            MCQuery mcQuery = new MCQuery("play.hyperlandsmc.net", 19132);
            QueryResponse response = mcQuery.basicStat();
            return response.getOnlinePlayers();
        } catch (NullPointerException v) {
             return 0;
        }
    }

    public static void updateStats(String username) throws IOException {
        try {
            System.out.println("-Thread0 [NOTIFICATION] UpdateStats was ran");

            // HIDDEN
            String sURL = " " + username;
            URL url = new URL(sURL);
            URLConnection request = url.openConnection();
            request.connect();

            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.

            lastgamemode = rootobj.get("last").getAsString();

            level = rootobj.get("Level").getAsString();

            online = rootobj.get("status").getAsString();

        } catch (NullPointerException v) {
            if (startUp = true) {
                lastgamemode = "Lobby-1";
                level = "Intializing";
                online = "OFFLINE";
                System.out.println("-Thread3 [STARTUP] Startup was delayed due to rate limit");
            } else {
                lastgamemode = "Lobby-1";
                level = "Intializing";
                online = "OFFLINE";
                System.out.println("-Thread3 [ERROR] Error reading data, probably timeout. Trying again");
            }
        }
    }

    public static void main(final String[] args) {

        Thread two = new Thread(() -> {
            while (true) {
                try {
                    if (automatic) {
                        Thread.sleep(5001);
                        try {
                            updateStats(username);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        lastgamemode = lastgamemode.replace('"', ' ');
                        online = online.replace('"', ' ');

                        switch (lastgamemode) {
                            case "Lobby-1":
                            case "Lobby-2":
                                gamemode = "lobby";
                                break;

                            case "SWSolos-1":
                            case "SWSolos-2":
                                gamemode = "skywarssolo";
                                break;

                            case "Sumo-1":
                            case "Sumo-2":
                                gamemode = "sumo";
                                break;

                        }

                        if (online.equalsIgnoreCase("OFFLINE")) {
                            gamemode = "offline";
                        } else {
                            gamemode = "lobby";
                        }
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        two.start();

        Thread one = new Thread(() -> {
            final DiscordRPC lib = DiscordRPC.INSTANCE;
            final String applicationId = "745549006926643200";
            final String steamId = "";
            final DiscordEventHandlers handlers = new DiscordEventHandlers();
            lib.Discord_Initialize(applicationId, handlers, true, steamId);
            final DiscordRichPresence presence = new DiscordRichPresence();

            while (true) {
                try {

                    playersOnline = getPlayersOnline();
                    maxplayersonline = playersOnline + 1;

                    count++;

                    if (count == 5) { startUp = false; }

                    switch (gamemode) {

                        case "offline":
                            presence.details = "IGN: " + username;
                            presence.state = "Not online";
                            presence.largeImageKey = "hyperlandsmain";
                            presence.largeImageText = "play.hyperlandsmc.net";
                            presence.smallImageText = null;
                            presence.smallImageKey = null;
                            presence.partySize = playersOnline;
                            presence.partyMax = maxplayersonline;
                            break;

                        case "lobby":
                            presence.details = "Roaming the Lobby";
                            presence.state = "Level: " + level;
                            presence.largeImageKey = "hyperlandsmain";
                            presence.largeImageText = "play.hyperlandsmc.net";
                            presence.smallImageKey = "hyperlandslobby";
                            presence.smallImageText = "Lobby-1";
                            presence.partySize = playersOnline;
                            presence.partyMax = maxplayersonline;
                            break;

                        case "sumo":
                            presence.details = "Dueling in Sumo";
                            presence.state = "Level: " + level;
                            presence.largeImageKey = "hyperlandsmain";
                            presence.largeImageText = "play.hyperlandsmc.net";
                            presence.smallImageKey = "hyperlandslobby";
                            presence.smallImageText = "Sumo-1";
                            presence.partySize = playersOnline;
                            presence.partyMax = maxplayersonline;
                            break;


                    }


                    System.out.println("-Thread1 [" + count + "] Username: " + username + "   Gamemode: " + gamemode + "   Players: " + playersOnline + "/" + maxplayersonline + "   Level: " + level + "   Status: " + online);
                    lib.Discord_UpdatePresence(presence);
                    Thread.sleep(1000);
                } catch (InterruptedException v) {
                    System.out.println(v);
                }
            }
        });
        one.start();

        final JFrame Window = new JFrame();

        final JCheckBox autocheckbox = new JCheckBox("Automatically detect Lobby");
        autocheckbox.setSelected(false);
        final JCheckBox offlinecheckbox = new JCheckBox("Offline Mode");
        offlinecheckbox.setSelected(false);


        final JButton lbselect = new JButton();
        lbselect.setText("Lobby");
        lbselect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                offlinecheckbox.setSelected(false);
                autocheckbox.setSelected(false);
                gamemode = "lobby";
            }
        });


        final JButton bwselect = new JButton();
        bwselect.setText("Bedwars");
        bwselect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                offlinecheckbox.setSelected(false);
                autocheckbox.setSelected(false);
                gamemode = "bedwars";
            }
        });


        final JButton tbselect = new JButton();
        tbselect.setText("The Bridge");
        tbselect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                offlinecheckbox.setSelected(false);
                autocheckbox.setSelected(false);
                gamemode = "bridge";
            }
        });


        final JButton swselect = new JButton();
        swselect.setText("Skywars");
        swselect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                offlinecheckbox.setSelected(false);
                autocheckbox.setSelected(false);
                gamemode = "skywars";
            }
        });


        final JButton uhcselect = new JButton();
        uhcselect.setText("UHC Meetup");
        uhcselect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                offlinecheckbox.setSelected(false);
                autocheckbox.setSelected(false);
                gamemode = "uhcm";
            }
        });

        autocheckbox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                offlinecheckbox.setSelected(false);
                swselect.setSelected(false);
                bwselect.setSelected(false);
                lbselect.setSelected(false);
                tbselect.setSelected(false);
            }
        });


        offlinecheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                gamemode = "offline";
            }
        });


        final JPanel panel1 = new JPanel();
        panel1.add(lbselect);
        panel1.add(bwselect);
        panel1.add(tbselect);
        panel1.add(swselect);
        panel1.add(uhcselect);
        panel1.add(autocheckbox);
        panel1.add(offlinecheckbox);

        Window.add(panel1);
        Window.setVisible(true);
        Window.setSize(800, 75);
        Window.setResizable(false);
        Window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Window.setTitle("Hyperlands RPC");
        Window.setLocation(400, 200);
    }
}