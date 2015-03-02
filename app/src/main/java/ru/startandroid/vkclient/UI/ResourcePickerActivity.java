package ru.startandroid.vkclient.UI;

import android.content.Intent;
import android.os.Bundle;
import java.util.HashMap;
import ru.startandroid.vkclient.R;
import ru.startandroid.vkclient.adapters.AudioAdapter;
import ru.startandroid.vkclient.adapters.DocAdapter;
import ru.startandroid.vkclient.adapters.GridViewAlbumAdapter;

/**
 * Активность-хост для фрагментов прикрепленных файлов(PhotoFragment,AlbumFragment,OnePhotoFragment,AudioFragment,DocFragment
 */
public class ResourcePickerActivity extends NavigationDrawerActivity implements PhotoFragment.PhotoFragmentListener,GridViewAlbumAdapter.GridViewAlbumAdapterListener,AudioAdapter.AudioAdapterListener,DocAdapter.DocAdapterListener{

    PhotoFragment mPhotoFragment;
    AudioFragment mAudioFragment;
    DocFragment mDocFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_resource_picker);
        super.onCreate(savedInstanceState);
        switch (getIntent().getIntExtra("fragment",0)){
            case 1:
                setPhotoFragment();
                break;
            case 2:
                setAudioFragment();
                break;
            case 3:
                setDocFragment();
                break;
        }
    }

    @Override
    public void onClickFriends() {
        startActivity(new Intent(this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                |Intent.FLAG_ACTIVITY_SINGLE_TOP).putExtra(MainActivity.NAVIGATION_DRAWER_EVENT,MainActivity.FRIENDS_FRAGMENT));
        mDrawerLayout.closeDrawers();
        finish();
    }

    @Override
    public void onClickChooseChat() {
        startActivity(new Intent(this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                |Intent.FLAG_ACTIVITY_SINGLE_TOP).putExtra(MainActivity.NAVIGATION_DRAWER_EVENT,MainActivity.CHOOSE_CHAT_FRAGMENT));
        mDrawerLayout.closeDrawers();
        finish();
    }

    @Override
    public void onClickLogout() {
        startActivity(new Intent(this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                |Intent.FLAG_ACTIVITY_SINGLE_TOP).putExtra(MainActivity.NAVIGATION_DRAWER_EVENT,MainActivity.LOGOUT));
        mDrawerLayout.closeDrawers();
        finish();
    }


    private void setPhotoFragment(){
        mPhotoFragment = new PhotoFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.attachments_container,mPhotoFragment).commit();
    }

    private void setAudioFragment(){
        mAudioFragment = new AudioFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.attachments_container,mAudioFragment).commit();
    }

    private void setDocFragment(){
        mDocFragment = new DocFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.attachments_container,mDocFragment).commit();
    }



    @Override
    public void onClickAlbum(long id) {
        // Нажатие на альбом - ставим AlbumFragment и загружаем фотографии из этого альбома
        AlbumFragment albumFragment = new AlbumFragment();
        albumFragment.setAlbumId(id);
        getSupportFragmentManager().beginTransaction().replace(R.id.attachments_container,albumFragment).addToBackStack(null).commit();
    }

    @Override
    public void onClickPhoto(HashMap<String,String> onePhotoMap) {
        // Нажатие на фото - ставим OnePhotoFragment и загружаем в него фотографию
        OnePhotoFragment onePhotoFragment = new OnePhotoFragment();
        onePhotoFragment.setPhotoUrl(onePhotoMap);
        getSupportFragmentManager().beginTransaction().replace(R.id.attachments_container,onePhotoFragment).addToBackStack(null).commit();
    }

    @Override
    public void onClickPick(HashMap<String,String> onePhotoMap) {
        // В AlbumFragment нажата галочка на какой то фотографии, отдаем ее в ChatFragment
        setResult(RESULT_OK, new Intent(ChatFragment.PHOTO_FRAGMENT_ACTION)
                .putExtra(ChatFragment.PHOTO_ID_KEY, onePhotoMap.get("id"))
                .putExtra(ChatFragment.PHOTO_URL_130_KEY,onePhotoMap.get("photo_130"))
                .putExtra(ChatFragment.PHOTO_URL_604_KEY,onePhotoMap.get("photo_604")));
        finish();

    }

    @Override
    public void onClickAudio(HashMap<String,String> oneAudioMap) {
        // Выбрана аудиозапись
        setResult(RESULT_OK,new Intent(ChatFragment.AUDIO_FRAGMENT_ACTION)
                .putExtra(ChatFragment.AUDIO_ID_KEY,oneAudioMap.get("id"))
                .putExtra(ChatFragment.AUDIO_TITLE_KEY,oneAudioMap.get("title"))
                .putExtra(ChatFragment.AUDIO_ARTIST_KEY,oneAudioMap.get("artist"))
                .putExtra(ChatFragment.AUDIO_URL_KEY, oneAudioMap.get("url")));
        finish();
    }

    @Override
    public void onClickDoc(HashMap<String, String> oneDocMap) {
        // Выбран документ
        setResult(RESULT_OK,new Intent(ChatFragment.DOC_FRAGMENT_ACTION)
                .putExtra(ChatFragment.DOC_ID_KEY,oneDocMap.get("id"))
                .putExtra(ChatFragment.DOC_TITLE_KEY,oneDocMap.get("title"))
                .putExtra(ChatFragment.DOC_SIZE_KEY,oneDocMap.get("size")));
        finish();
    }
}
