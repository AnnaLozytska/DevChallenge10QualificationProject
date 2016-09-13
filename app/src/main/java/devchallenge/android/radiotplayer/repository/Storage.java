package devchallenge.android.radiotplayer.repository;

import com.yahoo.squidb.data.ISQLiteDatabase;
import com.yahoo.squidb.data.ISQLiteOpenHelper;
import com.yahoo.squidb.data.SquidDatabase;
import com.yahoo.squidb.sql.Table;

import devchallenge.android.radiotplayer.repository.modelspec.PodcastInfoRow;

class Storage extends SquidDatabase {
    private static volatile Storage sInstance;
    private static final String DB_NAME = "radio_t_player.db";
    private static final int VERSION = 1;

    public static Storage getInstance() {
        synchronized (Storage.class) {
            if (sInstance == null) {
                sInstance = new Storage();
            }
        }
        return sInstance;
    }

    @Override
    public String getName() {
        return DB_NAME;
    }

    @Override
    protected int getVersion() {
        return VERSION;
    }

    @Override
    protected Table[] getTables() {
        return new Table[] {
                PodcastInfoRow.TABLE
        };
    }

    @Override
    protected boolean onUpgrade(ISQLiteDatabase db, int oldVersion, int newVersion) {
        return false;
    }

    @Override
    protected ISQLiteOpenHelper createOpenHelper(String databaseName, OpenHelperDelegate delegate, int version) {
        return null;
    }
}
