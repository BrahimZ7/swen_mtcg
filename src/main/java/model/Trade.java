package model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Trade {
    final String Id;
    final String CardToTrade;
    final CardType Type;
    final float MinimumDamage;
    final String userID;

    public Trade(String id, String cardID, CardType type, float minimumDamage, String userID) {
        this.Id = id;
        this.CardToTrade = cardID;
        this.Type = type;
        this.MinimumDamage = minimumDamage;
        this.userID = userID;
    }
}
