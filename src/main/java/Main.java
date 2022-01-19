import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import model.Card;
import model.MonsterCard;
import model.SpellCard;
import model.User;
import service.DatabaseService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseService databaseService = new DatabaseService();
            databaseService.connectToDatabase();

            Server server = new Server(databaseService);
            server.startListening();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }
}