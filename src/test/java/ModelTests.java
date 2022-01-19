import model.ElementType;
import model.HTTPModel;
import model.MonsterCard;
import model.SpellCard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ModelTests {
    @Test
    public void testFireType() {
        MonsterCard monsterCard = new MonsterCard("1234", 20, "FireGoblin");

        assertEquals(monsterCard.getElementType(), ElementType.Fire, "FireGoblin should have the ElementType Fire");
    }

    @Test
    public void testWaterType() {
        MonsterCard monsterCard = new MonsterCard("1234", 20, "WaterGoblin");

        assertEquals(monsterCard.getElementType(), ElementType.Water, "WaterGoblin should have the ElementType Fire");
    }

    @Test
    public void testNormalType() {
        MonsterCard monsterCard = new MonsterCard("1234", 20, "Goblin");

        assertEquals(monsterCard.getElementType(), ElementType.Normal, "Goblin should have the ElementType Fire");
    }

    @Test
    public void testFireWaterEffectiveness() {
        MonsterCard monsterCard = new MonsterCard("1234", 40, "FireGoblin");
        SpellCard spellCard = new SpellCard("1234", 15, "WaterSpell");

        assertEquals(spellCard.compareTo(monsterCard), 1, "Water -> Fire");
    }

    @Test
    public void testFireNormalEffectiveness() {
        MonsterCard monsterCard = new MonsterCard("1234", 15, "FireGoblin");
        SpellCard spellCard = new SpellCard("1234", 40, "NormalSpell");

        assertEquals(spellCard.compareTo(monsterCard), -1, "Fire -> Normal");
    }

    @Test
    public void testNormalWaterEffectiveness() {
        SpellCard spellCard = new SpellCard("1234", 30, "WaterGoblin");
        MonsterCard monsterCard = new MonsterCard("1234", 15, "Goblin");

        assertEquals(spellCard.compareTo(monsterCard), -1, "Normal -> Water");
    }

    @Test
    public void testHTTPModelParse() {
        String header = """
                POST /transactions/packages HTTP/1.1
                Host: localhost:10001
                User-Agent: curl/7.77.0
                Accept: */*
                Content-Type: application/json
                Authorization: Basic kienboec-mtcgToken
                Content-Length: 0
                """;

        HTTPModel httpModel = new HTTPModel(header, "");

        assertEquals(httpModel.getPath(), "/transactions/packages");
        assertEquals(httpModel.getRequestMethod(), "POST", "This is a Post Request");
        assertNotEquals(httpModel.getContentType(), "test/plain", "The Content-Type is application/json");
    }


}
