package com.example.ankitwal.grabmovieinfo2;
public class ListItem
{
    public final String id;
    public final String url;
    public final String title;
    boolean isHeader;
    boolean isFooter;


    public ListItem(String id, String content,String title,boolean header, boolean footer)
    {
        this.id = id;
        this.url = content;
        isHeader=header;
        isFooter=footer;
        this.title=title;

    }

    @Override
    public String toString() {
        return url;
    }
}