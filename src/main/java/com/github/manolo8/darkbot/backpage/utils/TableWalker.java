package com.github.manolo8.darkbot.backpage.utils;

public final class TableWalker extends AbstractWalker {

    public TableWalker(String data, int start, int end) {
        super(data, start, end);
    }

    public final boolean hasNextRow() {

        while (index < end) {

            char c = data.charAt(index);

            if (c == '<' && nextIsEquals("tr")) {
                index++;
                return true;
            }

            index++;
        }

        return false;
    }

    public final String nextCol() {

        boolean start = false;
        boolean text  = false;

        int indexTextStart = -1;

        while (index < end) {

            char c = data.charAt(index);

            if (text) {
                if (c == '<' && (nextIsEquals("/td") || nextIsEquals("/th"))) {
                    String value = data.substring(indexTextStart, index);
                    index += 5;
                    return value;
                }
            } else if (start) {
                if (c == '>') {
                    indexTextStart = index + 1;
                    text = true;
                }
            } else {
                if (c == '<' && (nextIsEquals("td") || nextIsEquals("th"))) {
                    start = true;
                }
            }


            index++;
        }

        return "";
    }

    public final void skipCol() {
        while (index < end) {

            char c = data.charAt(index);

            if (c == '<' && (nextIsEquals("/td") || nextIsEquals("/th"))) {
                index += 5;
                break;
            }

            index++;
        }
    }
}
