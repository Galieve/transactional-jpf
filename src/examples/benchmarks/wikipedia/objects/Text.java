package benchmarks.wikipedia.objects;

import database.TRUtility;

public class Text {
    private long id;
    private String text;
    private int page;
    private String flags;



    public Text(String txt){
        this(TRUtility.getValue((s)->(Long.parseLong(s)),txt, 0),
                Integer.parseInt(TRUtility.getValue(txt, 1)),
                TRUtility.getValue(txt, 2),
                TRUtility.getValue(txt, 3)
        );
    }

    public Text(long id, int page, String text, String flags) {
        this.id = id;
        this.text = text;
        this.page = page;
        this.flags = flags;
    }

    @Override
    public String toString() {
        return id  +";"+ page+ ";" + text+";"+ flags;
    }

    public String getText() {
        return text;
    }
}
