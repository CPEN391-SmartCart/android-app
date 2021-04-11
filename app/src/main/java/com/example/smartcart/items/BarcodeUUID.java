package com.example.smartcart.items;

import java.util.Objects;
import java.util.UUID;

public class BarcodeUUID {
    private String barcode;
    private UUID uniqueID;

    public BarcodeUUID(String barcode, UUID uniqueID) {
        this.barcode = barcode;
        this.uniqueID = uniqueID;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setUniqueID(UUID uniqueID) {
        this.uniqueID = uniqueID;
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BarcodeUUID that = (BarcodeUUID) o;
        return Objects.equals(barcode, that.barcode) &&
                Objects.equals(uniqueID, that.uniqueID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(barcode, uniqueID);
    }
}
