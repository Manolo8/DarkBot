package com.github.manolo8.darkbot.utils;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class TextSelection implements Transferable {
    protected String text;

    public final static DataFlavor UNICODE_FLAVOR = new DataFlavor(
            "text/plain; charset=unicode; "+
                    "class=java.io.InputStream", "Unicode Text");

    public final static DataFlavor LATIN1_FLAVOR = new DataFlavor(
            "text/plain; charset=iso-8859-1; "+
                    "class=java.io.InputStream", "Latin-1 Text");

    public final static DataFlavor ASCII_FLAVOR = new DataFlavor(
            "text/plain; charset=ascii; "+
                    "class=java.io.InputStream", "ASCII Text");

    public static DataFlavor[] SUPPORTED_FLAVORS = {
            DataFlavor.stringFlavor,
            UNICODE_FLAVOR,
            LATIN1_FLAVOR,
            ASCII_FLAVOR
    };


    public TextSelection(String selection){
        text=selection;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return SUPPORTED_FLAVORS;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        for(int i=0; i< SUPPORTED_FLAVORS.length; i++){
            if(SUPPORTED_FLAVORS[i].equals(flavor)) return true;
        }
        return false;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return null;
    }
}
