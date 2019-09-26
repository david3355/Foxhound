package com.jagerdev.foxhoundpricetracker.utils;

import android.content.Context;
import android.net.Uri;
import androidx.core.content.FileProvider;

import java.io.File;

import static com.jagerdev.foxhoundpricetracker.database.DBConstants.DATABASE_NAME;

public class DbFileProvider extends FileProvider
{
       public Uri getDatabaseURI(Context c) {
//              File data = Environment.getDataDirectory();
              String currentDBPath = ExternalStorageHelper.getPublicDatabasePath(c, DATABASE_NAME);

//              File exportFile = new File(data, currentDBPath);
              File exportFile = new File(currentDBPath);

              return getFileUri(c, exportFile);
       }

       public Uri getFileUri(Context c, File f){
              return getUriForFile(c, "com.jagerdev.foxhoundpricetracker.dbfileprovider", f);
       }
}
