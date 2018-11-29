package fr.enssat.guillaumelaise.videoplayer;

import java.util.LinkedList;
import java.util.List;

public class ChapterService
{
    private List<ChapterModel> listChapter;

    //Init the chapter list
    public ChapterService()
    {
        this.listChapter = new LinkedList<>();
    }

    //add an existing chapter
    public void add(ChapterModel chapterModel)
    {
        this.listChapter.add(chapterModel);
    }

    //create and add a new chapter
    public void add(int position, String chapterTitle, String url)
    {
        this.listChapter.add(new ChapterModel(position, chapterTitle, url));
    }

    //return the video position by giving the chapter title
    public int getPositionByChapterTitle(String chapterTitle)
    {
        for (ChapterModel chapter:this.listChapter)
        {
            if(chapter.getChapter().equals(chapterTitle))
            {
                return chapter.getPosition();
            }
        }
        return -1;
    }

    //return the chapter title by giving the video position
    public String getChapterTitleByPosition(int position)
    {
        ChapterModel chapterModel = this.listChapter.get(0);
        for (ChapterModel chapter:this.listChapter)
        {
            if(chapterModel.getPosition() < chapter.getPosition() && position > chapter.getPosition())
            {
                chapterModel = chapter;
            }
        }
        return chapterModel.getChapter();
    }

    //return the web view url by giving the video position
    public String getUrlByPosition(int position)
    {
        ChapterModel chapterModel = this.listChapter.get(0);
        for (ChapterModel chapter:this.listChapter)
        {
            if(chapterModel.getPosition() < chapter.getPosition() && position > chapter.getPosition())
            {
                chapterModel = chapter;
            }
        }
        return chapterModel.getUrl();
    }
}
