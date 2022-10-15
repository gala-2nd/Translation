package MyClasses;

public class Data {
    public String source,translated,date,hour;
    public  Data(){}
    public  Data(String source,String translated,String date){
        this.source=source;
        this.translated=translated;
        this.date=date;
    }
    public String getSource(){
        return this.source;
    }
    public String getTranslated(){
        return this.translated;
    }
    public String getDate() {
        return this.date;
    }

}
