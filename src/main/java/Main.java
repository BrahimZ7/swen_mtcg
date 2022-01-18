import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import model.Card;
import model.MonsterCard;
import model.SpellCard;
import model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}