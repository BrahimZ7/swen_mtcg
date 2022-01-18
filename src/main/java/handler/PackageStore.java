package handler;

import model.Card;

import java.util.ArrayList;
import java.util.List;

public class PackageStore {

    private List<List<Card>> cardPackages;

    public PackageStore() {
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

}
