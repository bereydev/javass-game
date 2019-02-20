package ch.epfl.javass.jass;

import java.util.List;
import java.util.Collections; 
import java.util.Arrays; 

public final class Card {
    
    // IS THAT ALLOWED ? 
    private Color color; 
    private Rank rank; 

    private Card(Color c, Rank r) {
        rank = r; 
        color = c; 
    }
    
    static Card of(Color c, Rank r) {
        return new Card(c,r); 
    }
    
    static Card ofPacked(int packed) {
        //TODO 
        return new Card(Color.CLUB,Rank.AS); 
    }
    
    //Methodes de la classe Packed 
    
    int packed() {
        //TODO
        return 0; 
    }
    
    Color color() {
        //TODO
        return Color.CLUB; 
    }
    
    Rank rank() {
        //TODO
        return Rank.AS; 
    }
    
    boolean isBetter(Color trump, Card that) {
        //TODO 
        return true; 
    }
    int points(Color trump) {
        //TODO
        return 0; 
    }
    
    //METHODES AUTRE 
    
   public boolean equals(Object thatO) {
        // TODO 
        return true; 
    }
   public int hashCode() {
       //TODO 
       return 0; 
   }
   public String toString() {
       //TODO 
       return "lol"; 
   }
    
    public enum Color{
        SPADE("\u2660"),
        HEART("\u2661"),
        DIAMOND("\u2662"), 
        CLUB("\u2663");
        
        static final List<Color> ALL = 
                Collections.unmodifiableList(Arrays.asList(values())); 
        static final int COUNT = 4; 
        private String symbol; 
        
        private Color(String symbol) {
            this.symbol = symbol;
        }
     
        public String toString() {
            return symbol; 
        }
    }
    
    public enum Rank{
        SIX("6"), 
        SEVEN("7"), 
        EIGHT("8"), 
        NINE("9"), 
        TEN("10"), 
        JACK("J"), 
        QUEEN("Q"), 
        KING("K"), 
        AS("A"); 
        
        static final List<Rank> ALL = 
                Collections.unmodifiableList(Arrays.asList(values())); 
        static final int COUNT = 8; 
        
        private String symbol; 
        
        //CHECK !!! 
        public  int trumpOrdinal() {
            int num = this.ordinal(); 
            if(num==3) return 7; 
            else if(num==5) return 8; 
            return this.ordinal(); 
        }
        private Rank(String symbol) {
            this.symbol = symbol; 
        }
        public String toString() {
            return symbol; 
        }
    }

}
