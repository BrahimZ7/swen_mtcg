package model;

import lombok.Getter;

public abstract class Card implements Comparable {
    @Getter
    private final String id;
    private ElementType elementType;
    private final float damage;
    @Getter
    private final String name;

    Card(String id, float damage, String name) {
        this.id = id;
        this.damage = damage;
        this.name = name;

        if (this.name.toLowerCase().contains("fire")) {
            elementType = ElementType.Fire;
        } else if (this.name.toLowerCase().contains("water")) {
            elementType = ElementType.Water;
        } else {
            elementType = ElementType.Normal;
        }
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Damage: " + damage;
    }

    @Override
    public int compareTo(Object o) {
        if (this instanceof MonsterCard && o instanceof MonsterCard) {
            return Float.compare(this.damage, ((Card) o).damage);
        }

        Card card1 = this;
        Card card2 = (Card) o;

        float cardDamage1 = card1.damage;
        float cardDamage2 = card2.damage;


        if (!card1.elementType.equals(card2.elementType))
            if ((card1.elementType == ElementType.Water && card2.elementType == ElementType.Fire) ||
                    (card1.elementType == ElementType.Fire && card2.elementType == ElementType.Normal) ||
                    (card1.elementType == ElementType.Normal && card2.elementType == ElementType.Water)) {
                cardDamage1 *= 2;
                cardDamage2 *= 0.5;
            } else {
                cardDamage1 *= 0.5;
                cardDamage2 *= 2;
            }

        System.out.println("Card1 Damage: " + cardDamage1);
        System.out.println("Card2 Damage: " + cardDamage2);


        return Float.compare(cardDamage1, cardDamage2);
    }

}
