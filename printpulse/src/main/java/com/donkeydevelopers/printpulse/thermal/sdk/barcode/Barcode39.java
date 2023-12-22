package com.donkeydevelopers.printpulse.thermal.sdk.barcode;

import com.donkeydevelopers.printpulse.thermal.sdk.EscPosPrinterCommands;
import com.donkeydevelopers.printpulse.thermal.sdk.EscPosPrinterSize;
import com.donkeydevelopers.printpulse.thermal.sdk.exceptions.EscPosBarcodeException;

public class Barcode39 extends Barcode {
    public Barcode39(EscPosPrinterSize printerSize, String code, float widthMM, float heightMM, int textPosition) throws EscPosBarcodeException {
        super(printerSize, EscPosPrinterCommands.BARCODE_TYPE_39, code, widthMM, heightMM, textPosition);
    }

    @Override
    public int getCodeLength() {
        return this.code.length();
    }

    @Override
    public int getColsCount() {
        return (this.getCodeLength() + 4) * 16;
    }
}
