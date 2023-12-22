package com.donkeydevelopers.printpulse.thermal.sdk.textparser;

import com.donkeydevelopers.printpulse.thermal.sdk.EscPosPrinterCommands;
import com.donkeydevelopers.printpulse.thermal.sdk.exceptions.EscPosConnectionException;
import com.donkeydevelopers.printpulse.thermal.sdk.exceptions.EscPosEncodingException;

public interface IPrinterTextParserElement {
    int length() throws EscPosEncodingException;
    IPrinterTextParserElement print(EscPosPrinterCommands printerSocket) throws EscPosEncodingException, EscPosConnectionException;
}
