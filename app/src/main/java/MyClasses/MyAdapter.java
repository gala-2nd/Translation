package MyClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yangxu.translation.R;

import java.util.LinkedList;

public class MyAdapter extends BaseAdapter{
    private Context mContext;
    private LinkedList<Data> mData;
    private class ViewHolder{
        TextView tv_source,tv_translated,tv_time_date;
    }
    public MyAdapter(){}
    public MyAdapter(LinkedList<Data> mData,Context mContext){
        this.mData=mData;
        this.mContext=mContext;
    }
    @Override
    public int getCount(){
        return mData.size()<=10?mData.size():10;
    }
    @Override
    public Object getItem(int position){
        return null;
    }
    @Override
    public long getItemId(int position){
        return  position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        int index=position;
        ViewHolder holder=null;
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);
            holder=new ViewHolder();
            holder.tv_source=(TextView) convertView.findViewById(R.id.tv_source);
            holder.tv_translated=(TextView)convertView.findViewById(R.id.tv_translated);
            holder.tv_time_date=(TextView)convertView.findViewById(R.id.tv_time_date);

            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        holder.tv_source.setText(mData.get(position).getSource());
        holder.tv_translated.setText((mData.get(position).getTranslated()));
        holder.tv_time_date.setText(mData.get(position).getDate());
        return  convertView;
    }
    public void Add(Data data){
        if(mData==null){
            mData=new LinkedList<>();
        }
        mData.add(data);
        notifyDataSetChanged();
    }
    public void ClearData(){
        mData.removeAll(mData);
        notifyDataSetChanged();
    }
}
