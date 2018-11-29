package fr.enssat.guillaumelaise.videoplayer;

public class ChapterModel
{
    private int position;
    private String chapter;
    private String url;

    public ChapterModel(int position, String chapter, String url)
    {
        this.position = position;
        this.chapter  = chapter;
        this.url      = "https://en.wikipedia.org/wiki/Big_Buck_Bunny#" + url;
    }

    public int getPosition()
    {
        return this.position;
    }

    public String getChapter()
    {
        return this.chapter;
    }

    public String getUrl()
    {
        return this.url;
    }
}