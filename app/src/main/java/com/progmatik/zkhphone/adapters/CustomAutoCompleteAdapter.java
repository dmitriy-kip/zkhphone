package com.progmatik.zkhphone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.progmatik.zkhphone.interfaces.FilterListeners;

// Получаем ссылку на элемент AutoCompleteTextView в разметке
//AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.input_town);
// Создаем адаптер для автозаполнения элемента AutoCompleteTextView
//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, cities);
//autoCompleteTextView.setAdapter(adapter);


public class CustomAutoCompleteAdapter extends ArrayAdapter
{
    private List<String>    tempItems;
    private List<String>    suggestions;
    private FilterListeners filterListeners;
    private final Context mContext;

    public CustomAutoCompleteAdapter(Context context, int resource, List<String> items)
    {
        super(context, resource, 0, items);

        mContext = context;
        tempItems = new ArrayList<>(items); //заполняем массив данными от пользователя
        suggestions = new ArrayList<String>(); //создаем пустой массив - в него будут вернуты отфильтрованные данные
    }

    public void setFilterListeners(FilterListeners filterFinishedListener)
    {
        filterListeners = filterFinishedListener;
    }


    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public Object getItem(int index) {
        return suggestions.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }
        Object item = getItem(position);
        ((TextView) convertView.findViewById(android.R.id.text1)).setText(item.toString());

        return convertView;
    }

    @Override
    public Filter getFilter()
    {
        return nameFilter;
    }

    Filter nameFilter = new Filter()
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            FilterResults filterResults = new FilterResults();
            if (constraint != null)
            {
                ArrayList<String> tmpArray = new ArrayList<String>();
                if (filterListeners != null)
                    tmpArray = filterListeners.filteringFinished();
                filterResults.values = tmpArray;
                filterResults.count = tmpArray.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            if (results != null && results.count > 0)
            {
                suggestions = (ArrayList<String>) results.values;
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    };
}