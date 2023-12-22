package com.donkeydevelopers.printpulse.thermal.sdk.barcode;

import com.donkeydevelopers.printpulse.thermal.sdk.EscPosPrinterCommands;
import com.donkeydevelopers.printpulse.thermal.sdk.EscPosPrinterSize;
import com.donkeydevelopers.printpulse.thermal.sdk.exceptions.EscPosBarcodeException;

public class BarcodeUPCA extends BarcodeNumber {

    public BarcodeUPCA(EscPosPrinterSize printerSize, String code, float widthMM, float heightMM, int textPosition) throws EscPosBarcodeException {
        super(printerSize, EscPosPrinterCommands.BARCODE_TYPE_UPCA, code, widthMM, heightMM, textPosition);
    }

    @Override
    public int getCodeLength() {
        return 12;
    }
}
