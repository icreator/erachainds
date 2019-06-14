package org.erachain.entities.peers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

//@Service
public class Peers {
    private    List<Peers> peersMap = new LinkedList<>();

    @Autowired
    private Logger logger;

    private String ip;

    private Peers(String ip) {
        this.ip = ip;

    }
    Peers() throws IOException {

        FileReader fileReader = new FileReader("peers.json");

        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            JsonArray jsonArray = new JsonParser().parse(bufferedReader.readLine()).getAsJsonArray();

            for (JsonElement item : jsonArray) {
                peersMap.add(new Peers(item.toString()));
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
