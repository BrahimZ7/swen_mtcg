package handler;

import lombok.Getter;
import model.Card;
import model.Trade;
import service.DatabaseService;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Marketplace {
    private final DatabaseService databaseService;
    private List<List<Card>> cardPackages;
    @Getter
    private List<Trade> activeTrades = new ArrayList<Trade>();

    public Marketplace(DatabaseService databaseService) {
        this.cardPackages = new ArrayList<List<Card>>();
        this.databaseService = databaseService;
    }

    public void init() {
        if (databaseService != null) {
            List<Trade> activeTrades = databaseService.getTrades();

            this.activeTrades.addAll(activeTrades);

            List<List<Card>> packages = databaseService.getCardPackages();

            this.cardPackages.addAll(packages);
        }
    }

    public List<Card> buyPackage() throws ArrayIndexOutOfBoundsException {
        System.out.println(cardPackages.size());

        if (cardPackages.size() == 0)
            return null;
        List<Card> cardPackage = cardPackages.remove(0);
        System.out.println("We remove the Package");

        return cardPackage;
    }

    public void createPackage(List<Card> cardPackage) {
        cardPackages.add(cardPackage);
        if (databaseService != null)
            databaseService.savePackage(cardPackage, cardPackages.size() - 1);
    }

    public void addTrade(Trade trade) {
        activeTrades.add(trade);
        if (databaseService != null)
            databaseService.saveTrade(trade);
    }

    public Trade getTrade(String tradeID) {
        int index = getIndexOfTrade(tradeID);
        if (index == -1)
            return null;
        else
            return activeTrades.get(index);
    }

    public boolean deleteTrade(String tradeID) {
        int index = getIndexOfTrade(tradeID);

        if (index == -1)
            return false;

        activeTrades.remove(index);

        if (databaseService != null)
            databaseService.deleteTrade(tradeID);

        return true;
    }

    private int getIndexOfTrade(String tradeID) {
        for (int i = 0; i < activeTrades.size(); i++) {
            if (activeTrades.get(i).getId().equals(tradeID)) return i;
        }
        return -1;
    }
}
