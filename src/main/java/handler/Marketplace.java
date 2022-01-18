package handler;

import lombok.Getter;
import model.Card;
import model.Trade;

import java.util.ArrayList;
import java.util.List;

public class Marketplace {
    private List<List<Card>> cardPackages;
    @Getter
    private List<Trade> activeTrades = new ArrayList<Trade>();

    public Marketplace() {
        this.cardPackages = new ArrayList<List<Card>>();
    }

    public List<Card> buyPackage() throws ArrayIndexOutOfBoundsException {
        if (cardPackages.size() == 0)
            return null;
        return cardPackages.remove(0);
    }

    public void createPackage(List<Card> cardPackage) {
        cardPackages.add(cardPackage);
    }

    public void addTrade(Trade trade) {
        activeTrades.add(trade);
    }

    public Trade getTrade(String tradeID) {
        int index = getIndexOfTrade(tradeID);
        if (index == -1)
            return null;
        else
            return activeTrades.get(index);
    }

    public void deleteTrade(String tradeID) {
        int index = getIndexOfTrade(tradeID);

        activeTrades.remove(index);
    }

    private int getIndexOfTrade(String tradeID) {
        for (int i = 0; i < activeTrades.size(); i++) {
            if (activeTrades.get(i).getId().equals(tradeID)) return i;
        }
        return -1;
    }
}
