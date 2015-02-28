package ru.startandroid.vkclient.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;

import ru.startandroid.vkclient.ChatMessage;
import ru.startandroid.vkclient.R;
import ru.startandroid.vkclient.requests.ChatRequest;

/**
 * Адаптер для ListView ChatFragment
 */
public class ChatAdapter extends BaseAdapter {

    LinkedList<ChatMessage> mChatMessageArrayList;
    LayoutInflater mInflater;
    int mLayout;
    int mTextViewId;
    Context mContext;
    boolean hasPhoto;


    public ChatAdapter(Context context, LinkedList<ChatMessage> chatMessageArrayList, int layout, int textViewId){
        mContext = context;
        mChatMessageArrayList = chatMessageArrayList;
        mLayout = layout;
        mTextViewId = textViewId;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mChatMessageArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mChatMessageArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view==null){
            view = mInflater.inflate(mLayout,parent,false);
        }
        ChatMessage message = mChatMessageArrayList.get(position); // Получаем объект с текущим сообщением
        LinearLayout itemLayout = (LinearLayout) view.findViewById(R.id.item_layout); // Корневой layout, родитель messageLayout. У него иеняем фон в зависимости прочитано/не прочитано
        itemLayout.setBackgroundColor(message.isRead() ? Color.parseColor("#FFFFFF") : Color.parseColor("#DCDCDC"));
        LinearLayout messageLayout = (LinearLayout) view.findViewById(R.id.message_layout); // layout, к которому будем присоединять view с текстом, фото, аудио, видео и документами
        messageLayout.removeAllViews(); // Стираем все view
        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if(message.isOut()){ // В зависимости от вида сообщения (входящее/исходящее) делаем отступы и меняем цвет
            messageParams.setMargins(70,10,10,10);
            messageLayout.setBackgroundColor(Color.parseColor("#BFEFFF"));
        }else{
            messageParams.setMargins(10,10,70,10);
            messageLayout.setBackgroundColor(Color.parseColor("#E8E8E8"));
        }
        messageLayout.setLayoutParams(messageParams);
        if (!message.getBody().equals("")){ // Если вообщение содержит текст, создаем view (message_body_layout) с текстом и добавляем к messageLayout
            View tView = mInflater.inflate(R.layout.message_body_layout,messageLayout,false);
            TextView tv = (TextView) tView.findViewById(R.id.tw_text);
            tv.setText(message.getBody());
            messageLayout.addView(tView);
        }
        // Если есть прикрепления(message.getAttachments().size() > 0) присоединяем соответствующие view к messageLayout
        // Фото - photo_attach_layout, аудио - audio_attach_layout
        // Видео - video_attach_layout, документы - doc_attach_layout
        if (message.getAttachments().size() > 0){
            int photoCount = 0; // Счетчик колличества фотографий. Если равен размеру списка, заполняем GridView и присоединяем к messageLayout
            ArrayList<String> urlArrayList = new ArrayList<String>(); // Список url фотографий
            // Если в списке есть фотографии, то они находятся в начале списка. В таком случаем ставим hasPhoto = true
            // Когда дойдет дело до других прикреплений, сработает метод createGridView, к messageLayout добавится view с фото и hasPhoto станет false
            if (message.getAttachments().get(0).get(ChatRequest.TYPE).equals(ChatRequest.PHOTO)){
                hasPhoto = true;
            }
            // Заполнение messageLayout вьюхами из прикреплений
            for ( int i = 0; i < message.getAttachments().size(); i++){
                switch(message.getAttachments().get(i).get(ChatRequest.TYPE)){ // По ключу type смотрим тип прикрепления
                    case ChatRequest.AUDIO:
                        if (hasPhoto){
                            createGridView(messageLayout, urlArrayList);
                        }
                        hasPhoto = false;
                        View audioView = mInflater.inflate(R.layout.audio_attach_layout,messageLayout,false);
                        TextView twSong = (TextView) audioView.findViewById(R.id.tw_song);
                        twSong.setText(message.getAttachments().get(i).get(ChatRequest.TITLE));
                        TextView twBand = (TextView) audioView.findViewById(R.id.tw_band);
                        twBand.setText(message.getAttachments().get(i).get(ChatRequest.ARTIST));
                        ImageView imageViewAudio = (ImageView) audioView.findViewById(R.id.iw_audio);
                        imageViewAudio.setImageResource(R.drawable.audio);
                        messageLayout.addView(audioView);
                        break;
                    case ChatRequest.DOC:
                        if (hasPhoto){
                            createGridView(messageLayout, urlArrayList);
                        }
                        hasPhoto = false;
                        View docView = mInflater.inflate(R.layout.doc_attach_layout,messageLayout,false);
                        TextView twTitle = (TextView) docView.findViewById(R.id.tw_doc_name);
                        twTitle.setText(message.getAttachments().get(i).get(ChatRequest.TITLE));
                        TextView twSize = (TextView) docView.findViewById(R.id.tw_doc_size);
                        twSize.setText(byteToString(message.getAttachments().get(i).get(ChatRequest.SIZE)));
                        ImageView imageViewDoc = (ImageView) docView.findViewById(R.id.iw_doc);
                        imageViewDoc.setImageResource(R.drawable.document);
                        messageLayout.addView(docView);
                        break;
                    case ChatRequest.PHOTO:
                        photoCount++;
                        urlArrayList.add(message.getAttachments().get(i).get(ChatRequest.PHOTO_604));
                        // Если прикреплены были только фотографии, ставим GridView, как дойдем до конца списка.
                        // В противном случае GridView поставится в case audio/video/doc
                        if (photoCount == message.getAttachments().size()){
                            createGridView(messageLayout, urlArrayList);
                        }
                        break;
                    case ChatRequest.VIDEO:
                        if (hasPhoto){
                            createGridView(messageLayout, urlArrayList);
                        }
                        hasPhoto = false;
                        View videoView = mInflater.inflate(R.layout.video_attach_layout,messageLayout,false);
                        ImageView videoImageView = (ImageView) videoView.findViewById(R.id.videoImageView);
                        Picasso.with(mContext).load(message.getAttachments().get(i).get(ChatRequest.PHOTO_320)).into(videoImageView);
                        messageLayout.addView(videoView);
                }
            }
        }
        return view;
    }

    public static String byteToString(String size){
        // Перевод размера документа из байтов в строку
        String stringSize = "";
        double doubleSize = Long.valueOf(size);
        if (doubleSize < 1024){
            stringSize = "Документ " + size + " байт";
        }else if (doubleSize >= 1024 && doubleSize < 1048576){
            stringSize = "Документ " + String.valueOf(new BigDecimal(doubleSize/1024).setScale(1, RoundingMode.DOWN).doubleValue()) + " кб";
        }else if (doubleSize >= 1048576 && doubleSize < 1073741824){
            stringSize = "Документ " + String.valueOf(new BigDecimal(doubleSize/1048576).setScale(1, RoundingMode.DOWN).doubleValue()) + " мб";
        }else if (doubleSize >= 1073741824){
            stringSize = "Документ " + String.valueOf(new BigDecimal(doubleSize/1073741824).setScale(1, RoundingMode.DOWN).doubleValue()) + " гб";
        }
        return stringSize;
    }

    private void createGridView(ViewGroup parent, ArrayList<String> urlArrayList){
        // Метод вызывается, когда список urlArrayList заполнен urlами всех фотографий, прикрепленных к сообщению
        View photoView = mInflater.inflate(R.layout.photo_attach_layout, parent, false);
        GridView gridView = (GridView) photoView.findViewById(R.id.gridView);
        gridView.setAdapter(new GridViewChatAdapter(mContext,urlArrayList));
        LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        par.weight=1;
        photoView.setLayoutParams(par);
        parent.addView(photoView);
    }

}