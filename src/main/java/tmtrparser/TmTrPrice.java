package tmtrparser;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;

class TmTrPrice {
     private String ourCode;
     private String theirCode;
     private int minPrice=0;
     private int availability=0;



     void setOurCode(String ourCode) {
         this.ourCode = ourCode;
     }

      String getOurCode() {
         return ourCode;
     }

      void setTheirCode(String theirCode) {
         this.theirCode = theirCode;
     }

      String getTheirCode() {
         return theirCode;
     }

      int getAvailability() {
         return availability;
     }

      int getMinPrice() {
         return minPrice;
     }

      void setMinPrice(int minPrice) {
         this.minPrice = minPrice<this.minPrice?minPrice:this.minPrice;
     }

      void addAvailability(int availability) {
         this.availability += availability;
     }
     public TmTrPrice(String ourCode){
         this.ourCode=ourCode;
     }

    @Override
    public String toString() {
        return getOurCode()+" "+getTheirCode()+" "+getAvailability()+" "+getMinPrice();
    }
}
