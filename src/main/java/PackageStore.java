import model.Card;

import java.util.ArrayList;
import java.util.List;

public class PackageStore {

    private List<List<Card>> cardPackages;

    PackageStore() {
        this.cardPackages = new ArrayList<List<Card>>();
    }

    public List<Card> buyPackage() throws ArrayIndexOutOfBoundsException {
        return cardPackages.remove(0);
    }

    public void createPackage(List<Card> cardPackage) {
        cardPackages.add(cardPackage);
    }

}
