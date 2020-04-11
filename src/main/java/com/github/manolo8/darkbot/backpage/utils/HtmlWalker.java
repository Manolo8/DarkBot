package com.github.manolo8.darkbot.backpage.utils;

public class HtmlWalker extends AbstractWalker {

    private boolean inAttribute;

    public HtmlWalker(String data, int start, int end) {
        super(data, start, end);
    }

    public String nextElement() {

        boolean start = false;
        int     begin = -1;

        inAttribute = false;

        while (index < end) {
            char c = data.charAt(index);
            if (start) {
                if (c == '>' || c == ' ')
                    return data.substring(begin, index++);
            } else if ((c == '<' && (!nextIsEquals("/")))) {
                start = true;
                begin = index + 1;
            }
            index++;
        }

        return null;
    }

    public String nextElementAttribute() {

        boolean start = false;
        int     begin = -1;

        inAttribute = true;

        while (index < end) {
            char c = data.charAt(index);
            if (c == '>') {
                index++;
                return null;
            } else if (start) {

                if (c == '"')
                    start = false;
                else if (c == '=')
                    return data.substring(begin, index++);

            } else if (c != ' ') {
                start = true;
                begin = index;
            }
            index++;
        }

        return null;
    }

    public String content() {
        if (inAttribute)
            return contentInAttribute();
        else
            return contentBasic();
    }

    private String contentBasic() {

        int begin = index;

        while (index < end) {
            char c = data.charAt(index);

            if (c == '>')
                begin = index + 1;
            else if (c == '<' || (c == '/' && nextIsEquals('>')))
                return data.substring(begin, index++);

            index++;
        }

        return null;
    }

    private String contentInAttribute() {

        boolean start = false;
        int     begin = -1;

        while (index < end) {
            char c = data.charAt(index);

            if (start) {

                if (c == '"')
                    return data.substring(begin, index++);

            } else if (c == '"') {
                start = true;
                begin = index + 1;
            }

            index++;
        }

        return null;
    }

    public int contentAsInteger() {
        return Integer.parseInt(content());
    }

    public long contentAsALong() {return Long.parseLong(content());}
}
